package io.github.carrothole.processor.generateo.anno;

import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Description:  <br>
 * Date: 2024/9/3 13:15 <br>
 *
 * @author moon
 * @since 0.0.1
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface AppendField {

    /**
     * 字段名
     */
    String name();

    /**
     * 字段类型
     * 如: String.class.getName()
     */
    String typeName();

    /**
     * 字段描述
     */
    String describe();

    /**
     * 注解<br/>
     * 如: com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm")
     */
    String[] annotations() default {};

    boolean ignore() default false;

    /**
     * 类型
     */
    VOTypeEnum[] type() default {VOTypeEnum.QUERY, VOTypeEnum.RESULT};

}
