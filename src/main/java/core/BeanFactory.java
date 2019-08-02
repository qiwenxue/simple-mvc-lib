package core;


/**
 * 类工厂，减小耦合度
 * @author Administrator
 * @param <T>
 */
public class BeanFactory<T> {
  
  private BeanFactory(){}
  
  /**
   * 泛型方法
   * @param <T>
   * @param clazz
   * @return
   * @throws Exception
   */
  public static <T> T getClazz( Class<T> clazz ) throws Exception {
    T clazzObj = (T)clazz.newInstance();
    return clazzObj;
  }
}
