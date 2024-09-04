package io.github.carrothole.processor.generateo.service;

import io.github.carrothole.processor.generateo.anno.AppendField;
import io.github.carrothole.processor.generateo.anno.GenQueryVOField;
import io.github.carrothole.processor.generateo.entity.ClassInfo;
import io.github.carrothole.processor.generateo.entity.FieldInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static java.util.Locale.ENGLISH;

/**
 * Description: processor基类 <br>
 * Date: 2024/9/4 15:44 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public interface ProcessorService<T> {


    default void setField(AppendField[] append, ClassInfo classInfo,  TypeElement typeElement, ProcessingEnvironment processingEnv) {
        for (AppendField appendField : append) {
            if (!appendField.ignore()) {
                classInfo.addFields(new FieldInfo(appendField.name(), appendField.typeName(), appendField.describe()));
            }
        }

        // 类成员变量
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element field : enclosedElements) {
            GenQueryVOField annotation = field.getAnnotation(GenQueryVOField.class);
            // 添加了GenQueryVOField注解的类
            if (annotation != null && !annotation.ignore() && field.getKind() == ElementKind.FIELD) {
                // 字段名
                TypeMirror type = field.asType();
                // 字段类型
                Element type_ = processingEnv.getTypeUtils().asElement(type);
                if (type_ != null) {
                    type = type_.asType();
                }
                // 字段描述
                String describe = annotation.describe();

                if (annotation.between()) {
                    // 起始字段
                    classInfo.addFields(new FieldInfo(field.getSimpleName().toString() + "Begin", type.toString(), describe + "开始"));
                    classInfo.addFields(new FieldInfo(field.getSimpleName().toString() + "End", type.toString(), describe + "结束"));
                    if (!annotation.ignoreSelf()) {
                        classInfo.addFields(new FieldInfo(field.getSimpleName().toString(), type.toString(), describe));
                    }
                } else {
                    classInfo.addFields(new FieldInfo(field.getSimpleName().toString(), type.toString(), describe));
                }

            }
        }
    }

    default void write(ClassInfo classInfo, ProcessingEnvironment processingEnv) {
        try {
            // 创建.java文件
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(classInfo.getPackageName());

            try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                writer.println("package " + classInfo.getPackageName() + ";");
                // 导包
                for (String anImport : classInfo.getImports()) {
                    writer.println("import "+anImport+";");
                }

                // 类名
                writer.println("@Schema(description = \"" + classInfo.getDescription() + "\")");
                writer.println("public class " + classInfo.getName() + " implements java.io.Serializable {");

                // 生成构造器
                writer.println("    public " + classInfo.getName() + "() {}");

                // 成员变量
                for (FieldInfo field : classInfo.getFields()) {
                    writer.println("    @Schema(description = \"" + field.getDescribe() + "\")");
                    writer.println("    private " + field.getType() + " " + field.getName() + ";");
                    writer.println(" ");
                }

                // getter/setter
                for (FieldInfo field : classInfo.getFields()) {
                    writer.println("    public " + field.getType() + " get" + capitalize(field.getName()) + "() { return this." + field.getName() + "; }");
                    writer.println("    public void set" + capitalize(field.getName()) + "(" + field.getType() + " " + field.getName() + ") { this." + field.getName() + " = " + field.getName() + "; }");
                    writer.println(" ");
                }

                writer.println("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to create source file "+ classInfo.toString());
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    static String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }
}
