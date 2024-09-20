package io.github.carrothole.processor.generateo;

import io.github.carrothole.processor.generateo.anno.*;
import io.github.carrothole.processor.generateo.entity.ClassInfo;
import io.github.carrothole.processor.generateo.entity.FieldInfo;
import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static java.util.Locale.ENGLISH;

/**
 * @author moon
 * @since 0.0.4
 */
@SupportedAnnotationTypes("io.github.carrothole.processor.generateo.anno.GenVO")
public class GenVOProcess extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 启动日志
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "start generate vo");
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GenVO.class);

        for (Element typeElement_ : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) typeElement_;

            GenVO genQueryVO = typeElement.getAnnotation(GenVO.class);


            VOTypeEnum[] types = genQueryVO.type();

            ClassInfo classInfo = new ClassInfo();
            classInfo.setPackageName(processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString());
            classInfo.setDescription(genQueryVO.describe());
            classInfo.setName(typeElement.getSimpleName().toString());

            // 设置附加字段
            setAppendField(genQueryVO.append(), classInfo,processingEnv);


            // 类成员变量
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            setClassField(enclosedElements, classInfo);
            // 父类成员变量

            TypeMirror typeMirror = typeElement.asType();
            // 获取父类的TypeMirror
            for (TypeMirror superclassTypeMirror : processingEnv.getTypeUtils().directSupertypes(typeMirror)) {
                // 检查父类是否是已解析的类
                if (superclassTypeMirror.getKind() == TypeKind.DECLARED) {
                    DeclaredType superclassDeclaredType = (DeclaredType) superclassTypeMirror;
                    TypeElement superclassTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(superclassDeclaredType);

                    // 父类字段
                    List<? extends Element> superclassFields = superclassTypeElement.getEnclosedElements();

                    setClassField(superclassFields, classInfo);
                }
            }

            for (VOTypeEnum type : types) {
                String targetClassName = classInfo.getName()+type.getSuffix();
                write(classInfo,targetClassName,type,processingEnv,(Element[])elementsAnnotatedWith.toArray(new Element[0]));
            }


        }
        return false;
    }


    public void setAppendField(AppendField[] append, ClassInfo classInfo, ProcessingEnvironment processingEnv) {
        for (AppendField appendField : append) {
            if (!appendField.ignore()) {
                classInfo.addFields(new FieldInfo(appendField.name(), appendField.typeName(), appendField.describe(), appendField.type(), appendField.annotations()));
            }
        }


    }

    String SEPARATOR= ";";
    String NEWLINE = "\n";

    public void write(ClassInfo classInfo, String newClassName, VOTypeEnum type, ProcessingEnvironment processingEnv, Element[] elements) {
        try {
            final StringBuilder builder = new StringBuilder();

            // 包名
            String packageName = classInfo.getPackageName()+"."+type.getPrefix();
            builder.append("package ").append(packageName).append(SEPARATOR).append(NEWLINE);
            builder.append(NEWLINE);

            // 固定导包
            builder.append("import io.swagger.v3.oas.annotations.media.Schema;").append(NEWLINE);
            builder.append("import java.io.Serializable;").append(NEWLINE);

            // 动态导包
            for (String anImport : classInfo.getImports()) {
                builder.append("import ").append(anImport).append(SEPARATOR).append(NEWLINE);
            }

            builder.append(NEWLINE);

            // swagger注释
            builder.append("@Schema(description = \"").append(classInfo.getDescription()).append("\"").append(")").append(NEWLINE);
            // 类名
            builder.append("public class ").append(newClassName).append(" implements Serializable {").append(NEWLINE);


            // 成员变量
            for (FieldInfo field : classInfo.getFields()) {
                if (field.hasThis(type)){
                    // 自定义注解
                    for (String simpleAnnotation : field.getSimpleAnnotations()) {
                        builder.append("    @").append(simpleAnnotation).append(NEWLINE);
                    }
                    // swagger注解
                    builder.append("    @Schema(description = \"").append(field.getDescribe()).append("\"").append(")").append(NEWLINE);
                    // 字段
                    builder.append("    private ").append(field.getSimpleType()).append(" ").append(field.getName()).append(SEPARATOR).append(NEWLINE);
                    builder.append(NEWLINE);
                }

            }

            // getter/setter
            for (FieldInfo field : classInfo.getFields()) {
                if (field.hasThis(type)){
                    builder.append("    public ").append(field.getSimpleType()).append(" get").append(capitalize(field.getName())).append("() { return this.").append(field.getName()).append(SEPARATOR).append(" }").append(NEWLINE);
                    builder.append("    public void set").append(capitalize(field.getName())).append("(").append(field.getSimpleType()).append(" ").append(field.getName()).append(") { this.").append(field.getName()).append(" = ").append(field.getName()).append("; }").append(NEWLINE);
                    builder.append(NEWLINE);
                }
            }
            builder.append("}");

            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + newClassName, elements);
            try (Writer writer = new OutputStreamWriter(sourceFile.openOutputStream(), StandardCharsets.UTF_8)){
                writer.write(builder.toString());
                writer.flush();
            }

        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "create vo file field: "+ classInfo.toString());
            e.printStackTrace();
        }
    }

    static String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    private void setClassField(List<? extends Element> enclosedElements, ClassInfo classInfo) {
        for (Element field : enclosedElements) {
            GenVOField annotation = field.getAnnotation(GenVOField.class);
            // 添加了GenQueryVOField注解的类
            if (verify(annotation,field)) {
                // 字段类型
                TypeMirror type = convertType(field);
                // 字段描述
                String describe = annotation.describe();

                if (annotation.between()) {
                    // 起始字段
                    classInfo.addFields(new FieldInfo(field.getSimpleName().toString() + "Begin", type.toString(), describe + "开始",annotation.type(), new String[0]));
                    classInfo.addFields(new FieldInfo(field.getSimpleName().toString() + "End", type.toString(), describe + "结束",annotation.type(), new String[0]));
                    if (!annotation.ignoreSelf()) {
                        classInfo.addFields(new FieldInfo(field.getSimpleName().toString(), type.toString(), describe,annotation.type(), annotation.annotations()));
                    }
                } else {
                    classInfo.addFields(new FieldInfo(field.getSimpleName().toString(), type.toString(), describe,annotation.type(), annotation.annotations()));
                }

            }
        }
    }

    private TypeMirror convertType(Element field) {
        // 字段类型
        TypeMirror type = field.asType();
        // 字段类型
        Element type_ = processingEnv.getTypeUtils().asElement(type);
        if (type_ != null) {
            type = type_.asType();
        }
        return type;
    }


    public boolean verify(GenVOField annotation, Element element){
        return annotation != null && !annotation.ignore() && element.getKind() == ElementKind.FIELD;
    }
}
