package com.carrothole.processor.generateo;

import com.carrothole.processor.generateo.anno.AppendField;
import com.carrothole.processor.generateo.anno.GenQueryVO;
import com.carrothole.processor.generateo.anno.GenQueryVOField;
import com.carrothole.processor.generateo.entity.FieldInfo;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.Filer;
import static java.util.Locale.ENGLISH;


/**
 * Description:  <br>
 * Date: 2024/9/3 11:19 <br>
 *
 * @author moon
 * @since 0.0.1
 */
//@SupportedAnnotationTypes("com.carrothole.processor.generateo.anno.GenQueryVO")
public class GenerateOProcessor extends AbstractProcessor {

    private Filer filer;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GenQueryVO.class);
        for (Element typeElement_ :  elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) typeElement_;
            String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
            String className = typeElement.getSimpleName().toString();
            String newClassName = className + "QueryVO";
            GenQueryVO genQueryVO = typeElement.getAnnotation(GenQueryVO.class);
            AppendField[] append = genQueryVO.append();

            ArrayList<FieldInfo> fieldInfos = new ArrayList<>();
            for (AppendField appendField : append) {
                if (!appendField.ignore()){
                    fieldInfos.add(new FieldInfo(appendField.name(), appendField.typeName(), appendField.describe()));
                }
            }
            try {
                // 创建新的.java文件
                JavaFileObject fileObject = filer.createSourceFile(packageName + ".vo." + newClassName);

                try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                    writer.println("package " + packageName + ".vo;");
                    writer.println("import io.swagger.v3.oas.annotations.media.Schema;");
                    writer.println("@Schema(description = \""+genQueryVO.describe()+"\")");
                    writer.println("public class " + newClassName + " implements java.io.Serializable {");

                    // 生成构造器
                    writer.println("    public " + newClassName + "() {}");

                    // 输出字段和构造器
                    List<? extends Element> enclosedElements = typeElement.getEnclosedElements();

                    System.out.println(fieldInfos);
                    for (Element field : enclosedElements) {
                        GenQueryVOField annotation = field.getAnnotation(GenQueryVOField.class);
                        // 添加了GenQueryVOField注解的类
                        if (annotation != null && !annotation.ignore() && field.getKind() == ElementKind.FIELD){
                            // 字段名
                            TypeMirror type = field.asType();
                            // 字段类型
                            Element type_ = this.typeUtils.asElement(type);
                            if (type_!=null){
                                type = type_.asType();
                            }
                            // 字段描述
                            String describe = annotation.describe();
                            if (annotation.between()){
                                fieldInfos.add(new FieldInfo(field.getSimpleName().toString()+"Begin",type.toString(),describe+"开始"));
                                fieldInfos.add(new FieldInfo(field.getSimpleName().toString()+"End",type.toString(),describe+"结束"));
                            }else {
                                fieldInfos.add(new FieldInfo(field.getSimpleName().toString(),type.toString(),describe));
                            }
                        }
                    }
                    for (FieldInfo fieldInfo : fieldInfos) {
                        writer.println("    @Schema(description = \""+fieldInfo.getDescribe()+"\")");
                        writer.println("    private " + fieldInfo.getType() + " " + fieldInfo.getName() + ";");
                        writer.println(" ");
                    }
                    for (FieldInfo fieldInfo : fieldInfos) {
                        writer.println("    public " + fieldInfo.getType() + " get" + capitalize(fieldInfo.getName()) + "() { return this." + fieldInfo.getName() + "; }");
                        writer.println("    public void set" + capitalize(fieldInfo.getName()) + "(" + fieldInfo.getType() + " " + fieldInfo.getName() + ") { this." + fieldInfo.getName() + " = " + fieldInfo.getName() + "; }");
                        writer.println(" ");
                    }

                    writer.println("}");
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to create source file", typeElement);
            }
        }

        return true; // 表示注解处理成功
    }

    public static String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(GenQueryVO.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
