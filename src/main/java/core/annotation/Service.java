package core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * service属性
 * @author qiwx
 *
 */
@Target(ElementType.FIELD) // 用来描述类中的属性
@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface Service {
}
