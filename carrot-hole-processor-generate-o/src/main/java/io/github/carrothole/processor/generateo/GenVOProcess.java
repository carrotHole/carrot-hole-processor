package io.github.carrothole.processor.generateo;

import io.github.carrothole.processor.generateo.anno.GenQueryVO;
import io.github.carrothole.processor.generateo.anno.GenQueryVOField;
import io.github.carrothole.processor.generateo.anno.GenVO;
import io.github.carrothole.processor.generateo.anno.GenVOField;
import io.github.carrothole.processor.generateo.entity.ClassInfo;
import io.github.carrothole.processor.generateo.entity.FieldInfo;
import io.github.carrothole.processor.generateo.enums.VOTypeEnum;
import io.github.carrothole.processor.generateo.service.ProcessorService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

/**
 * @author moon
 * @since 0.0.3
 */
public class GenVOProcess extends AbstractProcessor implements ProcessorService {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
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
                        classInfo.addFields(new FieldInfo(field.getSimpleName().toString() + "Begin", type.toString(), describe + "开始",annotation.type()));
                        classInfo.addFields(new FieldInfo(field.getSimpleName().toString() + "End", type.toString(), describe + "结束",annotation.type()));
                        if (!annotation.ignoreSelf()) {
                            classInfo.addFields(new FieldInfo(field.getSimpleName().toString(), type.toString(), describe,annotation.type()));
                        }
                    } else {
                        classInfo.addFields(new FieldInfo(field.getSimpleName().toString(), type.toString(), describe,annotation.type()));
                    }

                }
            }
            write(classInfo,processingEnv);
        }
        return false;
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
