package io.github.carrothole.processor.generateo.anno;

import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

/**
 * Description:  <br>
 * Date: 2024/9/4 16:38 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public @interface GenVO {

    /**
     * 是否忽略<br/>
     * true: 其他配置均不生效,忽略此字段
     */
    boolean ignore() default false;

    /**
     * 类名<br/>
     */
    String describe() default "";

    /**
     * 附加字段<br/>
     */
    AppendField[] append() default {};

    /**
     * 类型<br/>
     */
    VOTypeEnum[] type() default {};

}
