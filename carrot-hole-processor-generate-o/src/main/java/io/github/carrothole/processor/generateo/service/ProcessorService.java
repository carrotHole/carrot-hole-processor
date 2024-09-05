package io.github.carrothole.processor.generateo.service;

import io.github.carrothole.processor.generateo.anno.AppendField;
import io.github.carrothole.processor.generateo.entity.ClassInfo;
import io.github.carrothole.processor.generateo.entity.FieldInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Locale.ENGLISH;

/**
 * Description: processor基类 <br>
 * Date: 2024/9/4 15:44 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public interface ProcessorService<T> {


    default void setAppendField(AppendField[] append, ClassInfo classInfo, TypeElement typeElement, ProcessingEnvironment processingEnv) {
        for (AppendField appendField : append) {
            if (!appendField.ignore()) {
                classInfo.addFields(new FieldInfo(appendField.name(), appendField.typeName(), appendField.describe()));
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
