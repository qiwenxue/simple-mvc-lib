package socket;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
/**
 * socket默认处理器
 * @author qwx0
 *
 */
public class DefaultSocketInvokerImpl extends SocketInvoker {
  
  @Override
  protected String invoke(IoSession session, IoAcceptor acceptor, String... cmdObj) throws Exception {
    return null;
  }

  @Override
  protected void sessionOpened(IoSession session) {
    session.write("\0");
  }

  @Override
  protected void sessionClosed(IoSession session) throws Exception {
  }

  @Override
  protected void sessionCreated(IoSession session) throws Exception {
    StringBuffer auth = new StringBuffer();
    auth.append("<cross-domain-policy>");
    auth.append(" <allow-access-from domain=\"*\" to-ports=\"9999\"/>");//domain="*"表示任意域名， 192.168.155.1
    auth.append("</cross-domain-policy>");
    auth.append("\0");
    session.write(auth.toString());
  }

  @Override
  protected void messageSent(IoSession session, Object message) throws Exception {
  
  }

  @Override
  protected void exceptionCaught(IoSession session, Throwable throwable) throws Exception {

  }

}
