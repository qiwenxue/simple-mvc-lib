/**
 * 
 */
package core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Administrator
 *
 */
@Target(ElementType.TYPE) //ElementType.TYPE 用来描述类,接口等
@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface Action {
  public String namespace();  //列对应的字段名称
}
