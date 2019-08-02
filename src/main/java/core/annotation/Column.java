/**
 * 
 */
package core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标注列名
 * @author Administrator
 *
 */
@Target(ElementType.FIELD) // 用来描述类中的属性
@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface Column {
  public String  name() default "";  //列对应的字段名称
  public int     length() default 0;    //字段长度
  public boolean id() default false; //是不是主键
  public boolean isAutoIncrement() default true;//主键是否自增长,默认是
  public String comment() default ""; //字段描述
}
  