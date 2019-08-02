package core.templete;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import core.util.CommUtils;

/**
 * 采用velocity写的简单读取模板
 * @author Administrator
 *
 */
public class SimpleTemplete {
  private static Logger log 
                      = core.util.Logger.getLogger(SimpleTemplete.class);
  /**
   * 通过读取模板获得内容
   * @param templete  模板文件的相对路径
   * @param param     传入的参数
   * @return
   * @throws Exception
   */
  public static String getTempleteContent(String templete, Map<String, String> param) 
                                                                                   throws Exception {
    URL url = Thread.currentThread().getContextClassLoader().getResource("");
    String realPath = url.getPath();
    VelocityEngine ve = new VelocityEngine(); 
    Properties p =new Properties();  
    p.put(Velocity.FILE_RESOURCE_LOADER_PATH, realPath);   
    StringWriter writer = null;
    try {
      ve.init(p);  
      Template t = ve.getTemplate( templete ,"UTF-8" );
      VelocityContext context = new VelocityContext();
      if ( CommUtils.isNotNullMap(param) ) {
        Set<String> keys = param.keySet();
        for ( String key : keys ) {
          context.put(key, param.get(key));
        }
      }
      writer = new StringWriter();
      t.merge(context, writer);
      log.info("******>>>"+writer.toString());
    } catch ( Exception e ) {
      log.error("<===获得模板出错==>", e);
      throw e;
    }  
    return writer.toString();
  }
  
  public static void main(String[] args) throws Exception {
    Map<String, String> param = new HashMap<String, String>();
    param.put("name", "helloword");
    param.put("date", "星期六");
    String dd = getTempleteContent("templete/content.vm", param);
    System.out.println( "****"+ dd + "****");
  }
}
