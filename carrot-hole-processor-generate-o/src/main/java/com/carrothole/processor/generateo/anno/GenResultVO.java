package com.carrothole.processor.generateo.anno;

/**
 * Description:  <br>
 * Date: 2024/9/4 16:38 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public @interface GenResultVO{

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
    String suffix() default "ResultVO";

}
