package core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
/**
 * 对象转换工具把对象转换为json
 * @author qwx
 *
 */
public class ObjectUtil {
 
  /**
   * 把一个Object 对象转换为map
   * @param object
   * @return
   * @throws NoSuchMethodException 
   * @throws SecurityException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws IllegalArgumentException 
   */
  public static Map<String, String> object2Map(Object obj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

    Map<String, String> map = new HashMap<String, String>();
    if(obj != null){
      Field[] fields = obj.getClass().getDeclaredFields();//所有的属性
      for(int cnt=0; cnt<fields.length; cnt++){
        String fName = fields[cnt].getName();
        Method method = obj.getClass().getMethod("get"+fName.substring(0,1).toUpperCase()+fName.substring(1), new Class[]{});
        Object value = method.invoke(obj, new Object[]{}); 
        map.put(fName, (String)value);
      }
    }
    return map;
  }
  
  /**
   * 把一个对象转换为json对象
   * @param obj
   * @return
   * @throws NoSuchMethodException 
   * @throws SecurityException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws IllegalArgumentException 
   */
  public static String object2Json(Object obj) throws Exception{
    JSONObject beanObj = new JSONObject();
    if(obj != null){
      Field[] fields = obj.getClass().getDeclaredFields();//所有的属性
      for(int cnt=0; cnt<fields.length; cnt++){
        String fName = fields[cnt].getName();
        if("serialVersionUID".equalsIgnoreCase(fName)){
          continue;
        }
        Method method = obj.getClass().getMethod("get"+fName.substring(0,1).toUpperCase()+fName.substring(1), new Class[]{});
        Object value = method.invoke(obj, new Object[]{}); 
        beanObj.put(fName, value);
        if(fields[cnt].getType().getName().toString().equalsIgnoreCase("java.lang.String")){
          //value = CommUtils.encodeSpecial(String.valueOf(value));
        }
      }
    }
    return beanObj.toString();
  }
}
