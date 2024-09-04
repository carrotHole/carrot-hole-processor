package com.carrothole.processor.generateo.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Description: 生成QueryVO的注解 <br>
 * Date: 2024/9/3 9:35 <br>
 *
 * @author moon
 * @since 0.0.1
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface GenQueryVO {

    /**
     * 是否忽略<br/>
     * true: 其他配置均不生效,忽略此字段
     */
    boolean ignore() default false;

    /**
     * 字段描述
     */
    String describe() default "";


    /**
     * 附加字段<br/>
     */
    AppendField[] append() default {};

    /**
     * 新类名后缀
     */
    String suffix() default "QueryVO";


}
