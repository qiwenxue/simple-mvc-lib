/**
 * 
 */
package core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiwx
 * 参数标注
 */
@Target(ElementType.FIELD) // 用来描述类中的属性
@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface Param {
  public String name();
}
