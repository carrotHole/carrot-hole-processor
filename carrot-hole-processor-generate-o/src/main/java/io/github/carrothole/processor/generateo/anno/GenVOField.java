package io.github.carrothole.processor.generateo.anno;

import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 生成VO字段注解 <br>
 * Date: 2024/9/13 9:35 <br>
 *
 * @author moon
 * @since 0.0.3
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface GenVOField {

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
     * 不生成原始字段<br/>
     * true: 其他部分配置生效,不生成此字段
     */
    boolean ignoreSelf() default false;

    /**
     * 生成类型
     */
    VOTypeEnum[] type() default {VOTypeEnum.QUERY, VOTypeEnum.RESULT};

    /**
     * 是否使用起始值两个字段<br/>
     * 描述信息使用{@link GenVOField#describe()}内容,后坠为'开始'和'结束',如'创建时间开始/创建时间结束'
     */
    boolean between() default false;

}
