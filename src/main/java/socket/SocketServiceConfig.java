package socket;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import core.ReflectionCache;
import core.Unit;
import core.util.CommUtils;

public class SocketServiceConfig {
  
  private SocketServiceConfig() {}
  private static SocketServiceConfig config = new SocketServiceConfig();
  
  public  static SocketServiceConfig getInstance() {
    return config;
  }
  
  /**
   * 加载 service
   * @param basePath
   * @throws Exception
   */
  public  void initService( String basePath  ) throws Exception {
    String baseServiceXml = CommUtils.getPropValByKey("socket.file.name", "conf");
    String xmlDoc = getXml( basePath + "/" + baseServiceXml );
    getXmlPath( basePath, xmlDoc );
  } 
  
  @SuppressWarnings("unchecked")
  private void getXmlPath(String basePath, String xmlDoc ) throws Exception {
    Document doc = CommUtils.loadXMLStr(xmlDoc);
    Element root = doc.getRootElement();
    List<Element> datas = root.selectNodes("socket");
    for ( int i=0; i<datas.size(); i++ ) {
      Element e = datas.get(i);
      String id = e.attributeValue("id");
      String clazz = e.attributeValue("class");
      Unit socketUnit = new Unit();
      Class<?> socket = Class.forName(clazz);
      socketUnit.setClazz(socket);
      ReflectionCache.putCache(id, socketUnit); //把socket缓存起来
    }
  }
  
  public Object getObjectInstance( String key ) throws Exception {
    Class<?> classIntance = ReflectionCache.getClazz(key).getClazz();//Class.forName( clazz );
    Object classObject = classIntance.newInstance();   
    return classObject;
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
    BufferedReader myReader = new BufferedReader( new InputStreamReader ( is, "utf8" ) );
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
  
  /*public static void main(String[] args) throws Exception {
    String fullPath = CoreFileter.class.getResource("").getPath();//
    String cofigPath = fullPath.substring(0, fullPath.indexOf("/classes/") + 1 ) + "/classes/";
    SocketServiceConfig.getInstance().initService( cofigPath );
    SocketInvoke invoke = (SocketInvoke)SocketServiceConfig.getInstance().getObjectInstance("auth");
    invoke.doService(null, "{'key':'auth'}", null);
  }*/
}
