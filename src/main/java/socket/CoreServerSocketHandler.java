package socket;

import java.util.Hashtable;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;

import core.util.CommUtils;
/**
 * socket核心处理类
 * @author qiwx
 *
 */
public class CoreServerSocketHandler implements IoHandler {
	
	private IoAcceptor acceptor;
	private Map<Long, IoSession> map = new Hashtable<Long, IoSession>();
	private  SocketInvoker invoker = null;
	
	public CoreServerSocketHandler( IoAcceptor acceptor, SocketInvoker invoker) {
		this.acceptor = acceptor;
		this.invoker  = invoker;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable)
			throws Exception {
      //发生异常时
	}
  /**
   * 消息接收事件
   * 接收消息数据格式
   * {id: 111, to:'12344', from:'2222', msg:'XXXXXX', key:'handlerKey'}
   */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
       //收到客户端那发过来的消息
	   String str = message.toString();
	   map = acceptor.getManagedSessions();
	   System.out.println("----- socket池的大小 >>>>>"+ map.size()+"<<<<<<<");
	   str =  CommUtils.isNotNull(str) ?  replaceRow( str ) : str;
	   JSONObject obj = JSONObject.fromObject(str);
	   Message msgObj = (Message)JSONObject.toBean(obj, Message.class);
	   invoker = (SocketInvoker) SocketServiceConfig.getInstance().getObjectInstance( msgObj.getKey() );
	   invoker.doService( session, acceptor, str );  //子类处理一些业务
	}
	
	public String replaceRow( String str ){
	  str = str.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
    str = str.trim();
	  return str;
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	  //消息发送的时候调用
	  invoker.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
      //消息关闭时调用
		invoker.sessionClosed(session);  //子类处理一些业务
		session = null;
	}
	
 //session建立的时候创建
	@Override
	public void sessionCreated(IoSession session) throws Exception {
	  session.getConfig().setUseReadOperation(true);
	  invoker.sessionCreated(session); //子类处理一些业务
	}
					   
	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		//当连接变成闲置状态的时候，此方法被调用。
		long idleTime = (session.getLastBothIdleTime() - session.getCreationTime())/1000;
		if ( idleTime > 1800 )  {
		  session.close( true );
		  session = null;
		}
	}

	@Override
  //当有新的连接打开的时候，该方法被调用。该方法在 sessionCreated之后被调用。
	public void sessionOpened(IoSession session) throws Exception {
		invoker.sessionOpened(session);  //子类处理一些业务
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void event(IoSession session, FilterEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
