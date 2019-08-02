package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import core.annotation.Action;
import core.annotation.Forward;
import core.annotation.Param;
import core.annotation.Redirect;
import core.annotation.Service;
import core.db.DBConnectionUtil;
import core.util.CommUtils;
import core.util.Const;
import core.util.CoreDataUtil;
/**
 * 加载文件的类
 * @author qiwx
 *
 */
@SuppressWarnings("unchecked")
public final class ClassLoader implements Filter {
  private static String SUFFIX="";  //后缀
  private static final String BASE_CONFIG_FILE_KEY="base.service.file.name";
  private static final String BASE_CONFIG_FILE_NAME = "conf";
  private static final String DEFAULT_CODE = "UTF-8";
  private static final String REQUEST_URL_SUFFIX = "base.request.url.suffix";//请求的后缀
  private static final String BASE_DATASOURCE_KEY = "base.datasource.name";
  private static Logger logger = core.util.Logger.getLogger(ClassLoader.class);
  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
      ServletException {
  }

  @Override
  public void init(FilterConfig chain) throws ServletException {
    SUFFIX = CommUtils.getPropValByKey(REQUEST_URL_SUFFIX, BASE_CONFIG_FILE_NAME);//后缀
    try {
      String path=Thread.currentThread().getContextClassLoader().getResource("").toString();  
      String cofigPath = path.substring( 5 );
      if ( CommUtils.isNotNull(cofigPath) ) {
        cofigPath = StringUtils.removeEnd(cofigPath, File.separator);
      }
      initAction( cofigPath );
      getDataSource( cofigPath );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 加载Action
   * @param basePath
   * @throws Exception
   */
  private void initAction( String basePath  ) throws Exception {
    String baseServiceXml = CommUtils.getPropValByKey(BASE_CONFIG_FILE_KEY, BASE_CONFIG_FILE_NAME);
    String xmlDoc = getXml( basePath + File.separator + baseServiceXml );
    getXmlPath(basePath, xmlDoc, Const.LOAD.SERVICE_FIX );
  }
  
  private void getXmlPath(String basePath, String xmlDoc, String flag  ) throws Exception {
    Document doc = CommUtils.loadXMLStr(xmlDoc);
    Element root = doc.getRootElement();
    List<Element> datas = root.selectNodes( flag );
    for ( int i=0; i<datas.size(); i++ ) {
      Element e = datas.get(i);
      String path = e.attributeValue("path");
      String realPath = basePath + File.separator + path;
      String xml = getXml( realPath );
      if (Const.LOAD.SERVICE_FIX.equalsIgnoreCase(flag) ) {
        getAction( xml );
      } 
    }
  }
  
  /**
   * 读取path的目录文件下的内容
   * @param path
   * @return
   * @throws UnsupportedEncodingException 
   * @throws IOException 
   */
  private  static String getXml( String xmlpath ) throws Exception  {
    InputStream is = null;
    try {
      is = new FileInputStream( xmlpath );
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    BufferedReader myReader = new BufferedReader( new InputStreamReader ( is, DEFAULT_CODE ) );
    String line = null;
    String result = "";
    try {
      while ( (line = myReader.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if ( myReader != null ) {
        try {
          myReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  /**
   * 获得Action
   * 加载Action类的方法,属性等
   * @param xmlDoc
   * @return
   * @throws Exception
   */
  private void getAction( String xmlDoc ) throws Exception {
    Document doc = CommUtils.loadXMLStr(xmlDoc);
    Element root = doc.getRootElement();
    List<Element> datas = root.selectNodes( Const.LOAD.BEAN );
    for ( int i=0; i<datas.size(); i++ ) {
      Element e = datas.get(i);
      String classPath = e.attributeValue("class");
      Class<?> actionClazz = Class.forName( classPath );
      logger.debug( "[ *********加载类:" + classPath + " ******* ]");
      Action action = (Action)actionClazz.getAnnotation( Action.class );
      if ( action == null || CommUtils.isNull(action.namespace()) )  {
        throw new Exception(classPath+" 没有定义 namespace或者namespace为空");
      }
      String namespace = action.namespace();
      ReflectionCache.putClassNameCache(namespace, classPath);
      Method[] methods = actionClazz.getDeclaredMethods();//不包括继承来的
      Field[] fields   = actionClazz.getDeclaredFields();//所有属性
      for ( Method method : methods ) {
        core.annotation.Method mth = 
        (core.annotation.Method)method.getAnnotation(core.annotation.Method.class);
        if ( mth == null ) {  continue; }
        String methodName = method.getName();
        String suffixMethodName = mth.name();
        String requestUri = "/"+ namespace + "/" + suffixMethodName ;
        if ( CommUtils.isNotNull(SUFFIX) ) {
          requestUri += "."+ SUFFIX ;
        }
        logger.debug( "    |-------------加载方法:" + requestUri + " ------------>");
        String requestType = mth.type();
        Forward[] forwards = mth.forwards();
        Redirect[] redirects = mth.redirects();
        Unit unit = new Unit( classPath,  methodName,  requestUri,  requestType );
        setRequestCatalgy(forwards, redirects, unit); //缓存forwards和redirect方法
        setDefaultMethod( actionClazz, unit );        //缓存默认的方法
        setServices( fields, unit );                  //缓存带@Service的属性
        setParams( fields, unit );
        ReflectionCache.putCache(requestUri, unit);  
      }
    }
  }
  
  /**
   * 属性
   * @param fields
   * @param unit
   * @throws Exception
   */
  private void setServices( Field[] fields, Unit unit ) throws Exception {
    for ( Field field : fields ) {
      field.setAccessible(true);
      Service service = field.getAnnotation(Service.class);
      if ( service == null ) { continue; }
      String classpath = field.getGenericType().toString().split(" ")[1];
      Class<?> fieldClass = Class.forName(classpath); 
      Object fieldObject  = fieldClass.newInstance();
      unit.putField(field);
      unit.putFieldObject(fieldObject);//不缓存实例对象
      unit.putFieldClass(fieldClass);
    }
  }
  
  /**
   * 设置参数，此参数是用来封装entity用的
   * @param fields
   * @param unit
   * @throws Exception
   */
  private void setParams( Field[] fields, Unit unit ) throws Exception {
    for ( Field field : fields ) {
      field.setAccessible(true);
      Param entity = field.getAnnotation(Param.class);
      if ( entity == null ) { continue; }
      String classpath = field.getGenericType().toString().split(" ")[1];
      Class<?> fieldClass = Class.forName(classpath); 
      unit.putParamsClass(fieldClass, entity.name());
      unit.putParamsFieldObjects(field, entity.name());
    }
  }
  
  /**
   * 保存forward与redirect
   * @param forwards
   * @param redirects
   * @param unit
   * @throws Exception
   */
  private void setRequestCatalgy(Forward[] forwards, Redirect[] redirects, Unit unit) throws Exception {
    for (Forward forward : forwards ) {
      String name = forward.name();
      String url = forward.url();
      unit.putForwards(name, url);
    }
    for (Redirect redirect : redirects) {
      String name = redirect.name();
      String url = redirect.url();
      unit.putRedirects(name, url);
    }
  }
  
  /**
   * 默认的方法
   * @param classIntance
   * @param unit
   * @throws Exception
   */
  private void setDefaultMethod( Class<?> classIntance, Unit unit ) throws Exception {
    unit.setClazz(classIntance);
    //Object instance = classIntance.newInstance(); //此处可以考虑不用单个实例，可以在unit中调用classIntance.newInstance()，每次生成一个。
    //unit.setObj(instance);
    Method setRequestMethod = classIntance.getMethod(Unit.METHOD_SETREQUEST, HttpServletRequest.class);//方法名称，参数类型
    unit.putDefault( setRequestMethod );
    Method setResponseMethod = classIntance.getMethod(Unit.METHOD_SETRESPONSE, HttpServletResponse.class);
    unit.putDefault( setResponseMethod );
    Method sessionMethod = classIntance.getMethod(Unit.METHOD_SETSESSION, HttpSession.class);
    unit.putDefault( sessionMethod );
    Method setDaoMethod = classIntance.getMethod(Unit.METHOD_SETDAO, CoreDao.class);
    unit.putDefault( setDaoMethod );
    Method setResultMethod = classIntance.getMethod(Unit.METHOD_SETRESULT, Result.class);
    unit.putDefault( setResultMethod );
  }
  /**
   * 加载 数据源
   * @param basePath
   * @throws Exception
   */
  private void getDataSource( String basePath ) throws Exception {
    String dataSourceXml = CommUtils.getPropValByKey(BASE_DATASOURCE_KEY, BASE_CONFIG_FILE_NAME);
    String xmlDoc = getXml( basePath + File.separator + dataSourceXml );
    getDataSource( basePath, xmlDoc );
    DBConnectionUtil.init();
  }
  /**
   * 加载 数据源
   * @param basePath
   * @throws Exception
   */
  private void getDataSource( String basePath, String xmlDoc ) throws Exception{
    Document doc = CommUtils.loadXMLStr(xmlDoc);
    Element root = doc.getRootElement();
    List<Element> datas = root.selectNodes("source");
    for ( int i=0; i<datas.size(); i++ ) {
      Element e = datas.get(i);
      String key = e.attributeValue("id");
      String classPath = e.attributeValue("class");
      Class<?> classIntance = Class.forName( classPath );
      List<Element> properties = e.selectNodes("property");
      Object classObject = classIntance.newInstance();   
      for ( int k=0; k<properties.size(); k++) {
        Element property = properties.get(k);
        String  propertyName = property.attributeValue("name");
        String  methodValue  = property.getText();
        String  methodName   = "set"+ propertyName.substring(0,1).toUpperCase()+ propertyName.substring(1);
        Method  method       = classIntance.getMethod(methodName, String.class);//取要执行的方法
        method.invoke(classObject, methodValue);//methodValue是执行方法时传入的参数
      }
      if ( CoreDataUtil.getDataSourceCache().containsKey(key) ) {
        throw new Exception( key + "已经存在");
      }
      CoreDataUtil.putSource( key, classObject );//加载数据源到缓存中
    }
  }
}
