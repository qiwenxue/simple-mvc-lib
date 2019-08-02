package core;

import java.util.Hashtable;
import java.util.Map;
/**
 * class缓存
 * @author Administrator
 *
 */
public class ReflectionCache {
  
  private static Map<String, Unit> classCache;
  private static Map<String, String> classNameCache;
  
  static {
    classCache = new Hashtable<String, Unit>();
    classNameCache = new Hashtable<String, String>();
  }
    
  public static void putCache( String requestUri, Unit clazz ) throws Exception {
    if ( classCache.containsKey( requestUri ) ) {
      throw new Exception("[ "+requestUri + "] 已经存在!请更换名称");
    }
    classCache.put(requestUri, clazz);
  }
  
  public static void putClassNameCache( String namespace, String clazzName ) throws Exception {
    if ( classNameCache.containsKey(namespace) ) {
      throw new Exception("命名空间 [ "+namespace + " ]已经存在!请更换名称");
    }
    classNameCache.put(namespace, clazzName);
  }
  
  public static Unit getClazz( String requestUri ) {
    return classCache.get( requestUri );
  }
}
