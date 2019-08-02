package socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import cache.Memcached;
import core.util.CommUtils;

public class ServerSocketListener implements ServletContextListener{
  
  private static IoAcceptor acceptor = null;
  private static int PORT = 8901;
  private static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() + 1;  //默认处理器个数
    
  public static void main(String[] args) throws Exception {
    //start();
  }
  
  /**
   * 初始化 socket服务的ip
   */
  @SuppressWarnings("unused")
  private void initIpConfig() {
    //CommUtils.getLocalIP() + CommUtils.getPropValByKey("socket.port", "conf");
  	String ip = Config.socketServerIp + Config.socketServerPort;
  	Memcached.getInstance().getCached().set(ip, 0);
  	String serverips = ","+(String)Memcached.getInstance().getCached().get("serverips")+",";
  	if (! serverips.contains(","+ ip +",")) {
  	  Memcached.getInstance().getCached().set(serverips, serverips+ip);
  	}
  }
  
  private void initService() throws Exception {
   //String fullPath = CoreFileter.class.getResource("").getPath();//
   //String cofigPath = fullPath.substring(0, fullPath.indexOf("/classes/") + 1 ) + "/classes/";
   String path=Thread.currentThread().getContextClassLoader().getResource("").toString();  
   //String cofigPath = fullPath.substring(0, fullPath.indexOf("/classes/") ) + "/classes";
   String cofigPath = path.substring( 5 );
   SocketServiceConfig.getInstance().initService( cofigPath );
  }
  
  /**
   * 初始化socket监听
   * @throws IOException
   */
  private void initSocket() throws Exception {
	    String port = CommUtils.getPropValByKey("socket.port", "conf");
      destroy();
      List<SocketAddress> addresses = new ArrayList<SocketAddress>();
      if ( CommUtils.isNull(port) ) {
        addresses.add( new InetSocketAddress(PORT) );
      } else {
        String[] ports = port.split(",");
        for (String pot : ports) {
          if ( CommUtils.isNotNull(pot) ) {
            addresses.add( new InetSocketAddress(Integer.parseInt( pot )) );
          }
        }
      }
      // 创建服务端监控线程
      acceptor = new NioSocketAcceptor( DEFAULT_SIZE );//创建socket
      // 设置日志记录器
      acceptor.getFilterChain().addLast("Logger", new LoggingFilter());
      // 设置编码过滤器
      acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory( Charset.forName("UTF-8"))));
      acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));  //线程池
      acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);//session每5s钟检查一下session的状态
      // 指定业务逻辑处理器
      SocketInvoker invoker = null;
      String defaultInvokerClass = CommUtils.getPropValByKey("socket.default.invoker", "conf");
      defaultInvokerClass = CommUtils.isNull(defaultInvokerClass) ? "socket.DefaultSocketInvokerImpl" : defaultInvokerClass;
      invoker = (SocketInvoker)Class.forName(defaultInvokerClass).newInstance();
      acceptor.setHandler( new CoreServerSocketHandler( acceptor, invoker ) );
      // 设置端口号
      //acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
      acceptor.setDefaultLocalAddresses(addresses);
      // 启动监听线程
      acceptor.bind();
  }
  
  private void start() throws Exception {
    boolean useSocket = false;  //是否 使用 socket
    String isuse = CommUtils.getPropValByKey("socket.isuse", "conf");
    useSocket = CommUtils.isNull(isuse)?false:Boolean.parseBoolean(isuse);
    if ( useSocket ) {
      //initIpConfig();
      initSocket();
      initService();
    }
  }
  
  private static void destroy() {
    if ( acceptor!= null ) {
      acceptor.unbind();
      acceptor.dispose(true);
      acceptor = null;
    }
  }
  /**
   * 获取socekt sessions， 可以用这个给客户端推送消息
   * @return
   */
  public static Map<Long, IoSession> getSockets() {
    if ( acceptor != null ) {
      Map<Long, IoSession> map = acceptor.getManagedSessions();
      return map;
    }
    return null;
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    destroy();
  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    try {
      start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
