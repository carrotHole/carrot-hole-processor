package com.carrothole.processor.generateo;

import com.carrothole.processor.generateo.anno.AppendField;
import com.carrothole.processor.generateo.anno.GenQueryVO;
import com.carrothole.processor.generateo.anno.GenQueryVOField;
import com.carrothole.processor.generateo.entity.ClassInfo;
import com.carrothole.processor.generateo.entity.FieldInfo;
import com.carrothole.processor.generateo.service.ProcessorService;

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
import java.util.HashSet;
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
public class GenerateQueryVOProcessor extends AbstractProcessor implements ProcessorService {

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

        for (Element typeElement_ : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) typeElement_;

            GenQueryVO genQueryVO = typeElement.getAnnotation(GenQueryVO.class);
            String newClassName = typeElement.getSimpleName().toString() + genQueryVO.suffix();

            ClassInfo classInfo = new ClassInfo();
            classInfo.setPackageName(elementUtils.getPackageOf(typeElement).getQualifiedName().toString() + ".vo." + newClassName);
            classInfo.setName(newClassName);
            classInfo.addImport("io.swagger.v3.oas.annotations.media.Schema");

            // 设置字段
            setField(genQueryVO.append(), classInfo, typeElement,processingEnv);
            write(classInfo,processingEnv);
        }
        return true;
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
