package core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 应用在method方法
 * @author Administrator
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface Method {
  public static final String POST = "post";
  public static final String  GET = "get";
  public static final String ALL = "all";
  public String name();//*.do的名称
  public String type() default ALL;
  public Forward[] forwards() default {};
  public Redirect[] redirects() default {};
}
