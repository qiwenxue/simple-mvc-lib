package socket.client;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;

public class ClientHandler implements IoHandler {
   private static int i=0;
	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {

	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println((i++)+"server message:"+message.toString());// 显示接收到的消息
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
       System.out.println("send message:"+ message.toString());
	}

	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
    System.out.println("----------session closed---------");
	}

	@Override
	public void sessionCreated(IoSession arg0) throws Exception {

	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

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
