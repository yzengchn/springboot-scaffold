package xyz.yzblog.core.annotation;

import java.lang.annotation.*;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: 忽略Token验证
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

}
