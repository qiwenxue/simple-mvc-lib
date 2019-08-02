package core.util;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

/**
 *  日志类
 * @author Administrator
 *
 */
public class Logger {
  private static org.apache.log4j.Logger log ;
  private  Logger( Class<?> clazz ) {
    log = org.apache.log4j.Logger.getLogger(clazz);
  }
  
  public static org.apache.log4j.Logger getLogger( Class<?> clazz ){
    URL url = Thread.currentThread().getContextClassLoader().getResource("");
    String realPath = url.getPath();
    log = org.apache.log4j.Logger.getLogger(clazz);
    PropertyConfigurator.configure(realPath + "/log4j.properties" );
    return log;
  }
  
  public static org.apache.log4j.Logger getLogger( String logname ){
    URL url = Thread.currentThread().getContextClassLoader().getResource("");
    String realPath = url.getPath();
    log = org.apache.log4j.Logger.getLogger(logname);
    PropertyConfigurator.configure(realPath + "/log4j.properties" );
    return log;
  }
  
}
