package core.wrap;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import core.util.CommUtils;
/**
 * 用户加解密修改request
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class IMRequestWrapper extends HttpServletRequestWrapper {
  
  
  private Map params;
  
  public IMRequestWrapper(HttpServletRequest request, Map params){
    super(request);
    this.params = params;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Map getParameterMap() {
    return params;
  }
  
  @Override
  public String getParameter(String name) {
    if ( CommUtils.isNull(name) ) {
      return "";
    }
    String data = (String)params.get(name);
    //todo,此处进行解密
    return data;
  }
  
  @Override
  public String[] getParameterValues(String name) {
    if ( CommUtils.isNull(name) ) {
      return null;
    } 
    Object o = params.get(name);
    if ( o == null ) {
      return null;
    } else if ( o instanceof String[]) {
      String[] strArr=(String[]) o;
      return strArr;
    } else if ( o instanceof String ) {
      //进行解密
      return new String[]{(String)o};
    } else {
      return null;
    }
  }
}
