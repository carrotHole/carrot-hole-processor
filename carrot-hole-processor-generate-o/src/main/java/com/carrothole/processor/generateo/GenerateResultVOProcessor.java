package com.carrothole.processor.generateo;

import com.carrothole.processor.generateo.anno.GenQueryVO;
import com.carrothole.processor.generateo.anno.GenResultVO;
import com.carrothole.processor.generateo.entity.ClassInfo;
import com.carrothole.processor.generateo.service.ProcessorService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;


/**
 * Description:  <br>
 * Date: 2024/9/3 11:19 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public class GenerateResultVOProcessor extends AbstractProcessor implements ProcessorService {

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
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GenResultVO.class);

        for (Element typeElement_ : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) typeElement_;

            GenResultVO genQueryVO = typeElement.getAnnotation(GenResultVO.class);
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
