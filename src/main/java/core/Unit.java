package core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
/**
 * 请求单元
 * 每一个方法对应一个请求块
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class Unit implements Serializable{
  private String classPath;//此方法所在类路径
  private String method;  //方法名
  private String requestUri;//方法对应的请求地址
  private String requestType;//post或get
  private Map<String, String> forwardsCache; //跳转类型
  private Map<String, String> redirectsCache;//重定向类型
  private Map<String, Method> defaultCache;  //默认的请求方法
  private Class<?> clazz;
  private Object obj;
  
  protected static final String METHOD_SETREQUEST="setRequest";
  protected static final String METHOD_SETRESPONSE="setResponse";
  protected static final String METHOD_SETSESSION="setSession";
  protected static final String METHOD_SETDAO="setDao";
  protected static final String METHOD_SETRESULT="setResult";
  protected static final String METHOD_GETRESULT="getResult";
  
  private List<Field> fieldOfServiceCache;  //存放@Service属性
  private List<Object> fieldOfServiceObjectCache; //存放@Service属性对象
  private List<Class<?>> fieldOfServiceClassCache;//存放@Service属性对应的Class
  
  private Map<String, Class<?>> paramsCacheClass; //存放带有@Param注解的Class
  private Map<String, Object> paramsObjects;      //存放带有@Param注解的Class的实体类
  private Map<String, Field> paramsFieldObjects;  //存放带有@Param注解的属性
  
  
  public List<Class<?>> getFieldClassCache() {
    return fieldOfServiceClassCache;
  }
  
  /**
   * 每次获取一个对象，都会把这个对象的带@Param的属性
   * 便利一下，生成一个对象存到缓存
   * @return
   * @throws Exception
   */
  public Object getObj() throws Exception {
    obj = clazz.newInstance();//不采用单利模式
    for ( Map.Entry<String, Class<?>> entry : paramsCacheClass.entrySet() ) {
      Class<?> clazz = entry.getValue();
      String objectKey = entry.getKey() ;
      Object paramObject = clazz.newInstance();//新生成对象
      paramsObjects.put(objectKey, paramObject);
    }
    return obj;
  }
  public void setObj(Object obj) {
    this.obj = obj;
  }
  public Class<?> getClazz() {
    return clazz;
  }
  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public Unit() {
    forwardsCache = new Hashtable<String, String>();
    redirectsCache = new Hashtable<String, String>();
    defaultCache = new Hashtable<String, Method>();
    fieldOfServiceCache = new ArrayList<Field>();
    fieldOfServiceObjectCache = new ArrayList<Object>();
    fieldOfServiceClassCache = new ArrayList<Class<?>>();
    paramsCacheClass = new Hashtable<String, Class<?>>();
    paramsObjects = new Hashtable<String, Object>();
    paramsFieldObjects = new Hashtable<String, Field>();
  }
  
  public Unit(String classPath, String methodName, String requestUri, String requestType) {
    this.classPath = classPath;
    this.method = methodName;
    this.requestType = requestType;
    this.requestUri = requestUri;
    forwardsCache = new Hashtable<String, String>();
    redirectsCache = new Hashtable<String, String>();
    defaultCache = new Hashtable<String, Method>();
    fieldOfServiceCache = new ArrayList<Field>();
    fieldOfServiceObjectCache = new ArrayList<Object>();
    fieldOfServiceClassCache = new ArrayList<Class<?>>();
    paramsCacheClass = new Hashtable<String, Class<?>>();
    paramsObjects = new Hashtable<String, Object>();
    paramsFieldObjects = new Hashtable<String, Field>();
  } 
  
  public String getRequestUri() {
    return requestUri;
  }

  public void setRequestUri(String requestUri) {
    this.requestUri = requestUri;
  }

  public String getRequestType() {
    return requestType;
  }

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  public void putForwards(String name, String uri) {
    String key = classPath +"."+ method + ".forword." +name;
    forwardsCache.put(key, uri);
  }
  
  public String getForwardUri( String name ) {
    String key = classPath +"."+ method + ".forword." +name;
    return forwardsCache.get(key);
  }
  
  public void putRedirects(String name, String uri) {
    String key = classPath +"."+ method + ".redirect." +name;
    redirectsCache.put(key, uri);
  }
  
  public String getRedirectUri( String name ) {
    String key = classPath +"."+ method + ".redirect." +name;
    return redirectsCache.get(key);
  }
  
  public void putDefault( Method method ) {
    String key = classPath +".default." +method.getName();
    defaultCache.put(key, method);
  }
  
  public Method getDefaultMethodByName( String methodName ) {
    String key = classPath +".default." + methodName;
    return defaultCache.get(key);
  }
  
  public void putField( Field field ) {
    fieldOfServiceCache.add( field );
  }
  
  public void putFieldObject( Object fieldObj ) {
    fieldOfServiceObjectCache.add(fieldObj);
  }
  
  public void putFieldClass( Class<?> e) {
    fieldOfServiceClassCache.add(e);
  }
  
  public void putParamsClass( Class<?> clazz, String name ) {
    String key = classPath +".params.class." + name;
    paramsCacheClass.put(key, clazz);
  }
  
  public void putParamsObject( Object object, String name ) {
    String key = classPath +".params.class." + name;
    paramsObjects.put(key, object);
  }
  
  public void putParamsFieldObjects( Field field, String name ) {
    String key = classPath +".params.class." + name;
    paramsFieldObjects.put(key, field);
  }
  
  public Object getParamsObject( String name ) {
    String key = classPath +".params.class." + name;
    return paramsObjects.get( key );
  }
  
  public String getClassPath() {
    return classPath;
  }

  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
  
  /**
   * 注入Service
   * @param clazz
   * @throws Exception
   */
  public void injectServices( Object clazz, CoreDao dao ) throws Exception {
    int index = 0;
    for ( Field field  : fieldOfServiceCache ) {
      Class<?> fileClass = fieldOfServiceClassCache.get(index);
      Object fileObj = fileClass.newInstance();//fieldOfServiceObjectCache.get(index);不采用单利模式
      field.set(clazz, fileObj);
      fileClass.getMethod(METHOD_SETDAO, CoreDao.class).invoke(fileObj, dao);//service注入dao
      index ++;
    }
  }
  
  /**
   * 注入参数，将request里的参数封装成一个entity对象
   * @param request
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   */
  public void injectParams( HttpServletRequest request, Object clazz ) throws Exception {
    Map<?,?> allParams = request.getParameterMap();
    for ( Map.Entry<?, ?> entry : allParams.entrySet() ) {
      String properties = (String)entry.getKey();
      String[] values   = (String[])entry.getValue();
      Pattern p = Pattern.compile( PATTANER );//user.name=xxxx这样的格式     
      Matcher m = p.matcher(properties);  
      if ( m.matches() ) {
        String[] params  = properties.split( DIAN );
        String objectKey = params[0];
        String property  = params[1];
        Object entity    = getParamsObject( objectKey );
        if ( values.length >0 && entity != null ) {
         switch ( values.length ) {
          case 1:
            BeanUtils.copyProperty( entity, property, values[0] );
            break;
          default:
            BeanUtils.copyProperty( entity, property, values );
            break;
         }
        }
      }
    }
    //所有的参数带@Param注解的属性
    for ( Map.Entry<String, Object> entry : paramsObjects.entrySet() ) { 
      String key     = entry.getKey();
      Object fileObj = entry.getValue();
      Field  field   = paramsFieldObjects.get(key); //取出属性
      if ( field == null ) {
        continue;
      }
      field.set(clazz, fileObj);
    }
  }
  
  private static final String PATTANER = "^\\w+\\.\\w+$";
  private static final String DIAN = "\\.";
}
