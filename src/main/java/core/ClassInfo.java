package core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
/**
 * 用于缓存的class
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class ClassInfo implements Serializable {
  private String className; //类路径
  private String nameSpace; //命名空间
  private Map<String, Method> methodDefaultCache; //一个类中默认方法
  private Map<String, Unit> methodAnnotationCache;//一个类中标有注入的方法
  
  public ClassInfo(String className, String namespace) {
    this.className = className;
    this.nameSpace = namespace;
    methodDefaultCache = new Hashtable<String, Method>();
    methodAnnotationCache = new Hashtable<String, Unit>();
  }
  
  public String getNameSpace() {
    return nameSpace;
  }

  public void setNameSpace(String nameSpace) {
    this.nameSpace = nameSpace;
  }

  public void putDefaultMethod(Method method) {
    String key = className + "." + method.getName();
    methodDefaultCache.put(key, method);
  }
  
  public void putAnnotationMethod( Unit unit ) {
    String key = className + "." + unit.getMethod();
    methodAnnotationCache.put(key, unit);
  }
  
  public String getClassName() {
    return className;
  }
  public void setClassName(String className) {
    this.className = className;
  }
  public Map<String, Method> getMethodDefaultCache() {
    return methodDefaultCache;
  }
  public void setMethodDefaultCache(Map<String, Method> methodDefaultCache) {
    this.methodDefaultCache = methodDefaultCache;
  }
  public Map<String, Unit> getMethodAnnotationCache() {
    return methodAnnotationCache;
  }
  public void setMethodAnnotationCache(Map<String, Unit> methodAnnotationCache) {
    this.methodAnnotationCache = methodAnnotationCache;
  }
  
  
}
