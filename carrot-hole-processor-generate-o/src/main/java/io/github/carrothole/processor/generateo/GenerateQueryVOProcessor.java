package io.github.carrothole.processor.generateo;

import io.github.carrothole.processor.generateo.anno.GenQueryVO;
import io.github.carrothole.processor.generateo.anno.GenQueryVOField;
import io.github.carrothole.processor.generateo.entity.ClassInfo;
import io.github.carrothole.processor.generateo.entity.FieldInfo;
import io.github.carrothole.processor.generateo.service.ProcessorService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.Filer;


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
            classInfo.setPackageName(elementUtils.getPackageOf(typeElement).getQualifiedName().toString() + ".qo");
            classInfo.setDescription(genQueryVO.describe());
            classInfo.setName(newClassName);
            // 附加字段
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();

            // 设置成员变量
            setAppendField(genQueryVO.append(), classInfo, typeElement,processingEnv);


            setClassField(enclosedElements, classInfo);

            // 获取父类成员变量
            TypeMirror typeMirror = typeElement.asType();
            // 获取父类的TypeMirror
            for (TypeMirror superclassTypeMirror : typeUtils.directSupertypes(typeMirror)) {
                // 检查父类是否是已解析的类
                if (superclassTypeMirror.getKind() == TypeKind.DECLARED) {
                    DeclaredType superclassDeclaredType = (DeclaredType) superclassTypeMirror;
                    TypeElement superclassTypeElement = (TypeElement) typeUtils.asElement(superclassDeclaredType);

                    // 父类字段
                    List<? extends Element> superclassFields = superclassTypeElement.getEnclosedElements();

                    setClassField(superclassFields, classInfo);
                }
            }

            write(classInfo,processingEnv);
        }
        return true;
    }

    private void setClassField(List<? extends Element> enclosedElements, ClassInfo classInfo) {
        // 类成员变量
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


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(GenQueryVO.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
