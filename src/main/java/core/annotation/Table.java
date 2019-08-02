package core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 用来标识类对应的表
 * @author Administrator
 *
 */
@Target(ElementType.TYPE) //ElementType.TYPE 用来描述类,接口等
@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface Table {
  public String name(); //对应数据库表明
}
