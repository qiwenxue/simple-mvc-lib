package core.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 把一个请求参数转换成一个对象
 * @author qwx
 *
 */
public class RequestUtil {
  /**
   * 将request中的所有参数设置到entityClass类型的对象上
   * @param entityClass
   * @param request
   * @return
   * @throws Exception 
   */
  public static <T> T copyParam( Class<T> entityClass,HttpServletRequest request ) throws Exception {
    try {
      T entity = entityClass.newInstance();
      Map<?,?> allParams = request.getParameterMap();
      for (Map.Entry<?, ?> entry : allParams.entrySet()) {
        String property = (String)entry.getKey();
        String[] values = (String[])entry.getValue();
        if ( values != null && values.length > 0 ) {
          if ( values.length == 1 ) {
            BeanUtils.copyProperty( entity, property, values[0] );
          } else {
            BeanUtils.copyProperty( entity, property, values );
          }
        }
      }
      return entity;
    } catch (Exception e) {
      throw new Exception("参数转换为"+entityClass.getName()+ "时出错");
    }
  }
}

