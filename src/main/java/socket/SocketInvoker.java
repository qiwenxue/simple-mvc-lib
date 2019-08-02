package socket;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;

public abstract class SocketInvoker {
 protected abstract String invoke( IoSession session,  IoAcceptor acceptor, String... cmdObj ) throws Exception;
 
 public String doService( IoSession session, IoAcceptor acceptor, String... cmdObj  ) throws Exception {
   return invoke(  session, acceptor, cmdObj );
 }
 /**
  * session打开的时候
  * @param session
  */
 protected abstract void sessionOpened(IoSession session);
 
 /**
  * session关闭后处理
  * @param session
  * @throws Exception
  */
 protected abstract void sessionClosed(IoSession session) throws Exception;
 
 /**
  * session创建的时候
  * @param session
  * @throws Exception
  */
 protected abstract void sessionCreated(IoSession session) throws Exception;
 
 /**
  * 当发送消息的时候
  * @param session
  * @param message
  * @throws Exception
  */
 protected abstract void messageSent(IoSession session, Object message) throws Exception;
 /**
  * 发生异常的时候
  * @param session
  * @param throwable
  * @throws Exception
  */
 protected abstract void exceptionCaught(IoSession session, Throwable throwable)  throws Exception;
}
