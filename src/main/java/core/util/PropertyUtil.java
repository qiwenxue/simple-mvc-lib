package core.util;

import java.util.ResourceBundle;
/**
 * properties工具类
 * @author Administrator
 *
 */
public class PropertyUtil {
  
  private static PropertyUtil util = null;
  private PropertyUtil() {}
  public static PropertyUtil getInstance() {
    if ( util == null ) {
      util = new PropertyUtil();
    }
    return util;
  }
  
  public String getValueByKey(String key, String fileName) {
    ResourceBundle bundle = ResourceBundle.getBundle( fileName );
    String value = bundle.getString(key);
    return value;
  }
  
}
