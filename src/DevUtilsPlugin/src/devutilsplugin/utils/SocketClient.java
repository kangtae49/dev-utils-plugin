package devutilsplugin.utils;

import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.example.echoserver.ssl.BogusSslContextFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class SocketClient {
	NioSocketConnector connector;
	IoHandler handler = null;
	boolean bSSL = false;
	
	public SocketClient() {
		init();
		
	}
	public void setSSL(boolean bSSL) {
		this.bSSL = bSSL;
		init();
		connector.setHandler(handler);
	}	
	public void init() {
		dispose();
		
		int processors = Runtime.getRuntime().availableProcessors(); 
		connector = new NioSocketConnector(processors*2);
		connector.getSessionConfig().setReuseAddress(true);
//		connector.getSessionConfig().setReaderIdleTime(30);
//		connector.getSessionConfig().setWriterIdleTime(30);
//		connector.getSessionConfig().setBothIdleTime(idleTime);
//		connector.getSessionConfig().setUseReadOperation(true);
		connector.setConnectTimeoutMillis(3*1000);
		
		if(bSSL){
			try{
				SslFilter sslFilter = new SslFilter(BogusSslContextFactory.getInstance(false));
				sslFilter.setUseClientMode(true);
				connector.getFilterChain().addFirst("sslFilter", sslFilter);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public void setHandler(IoHandler handler){
		this.handler = handler;
		connector.setHandler(handler);
	}
	
	public ConnectFuture connect(String host, int port){
		ConnectFuture cf = connector.connect(new InetSocketAddress(host, port));
		System.out.println("cf:" + cf);
		return cf;
	}
	
	public WriteFuture send(ConnectFuture cf, byte [] data){
		System.out.println("start");
		cf.awaitUninterruptibly();
		IoBuffer message = IoBuffer.allocate(data.length);
		message.put(data);
		message.flip();
		System.out.println("cf::" + cf);
		System.out.println("cf.getSession()::" + cf.getSession());
		WriteFuture wf = cf.getSession().write(message);
		System.out.println("end");
		return wf;
	}
	
	public void close(ConnectFuture cf){
		cf.getSession().close(true);
	}
	
	public void dispose(){
		if(connector != null){
			connector.dispose();
		}
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SocketClient client = new SocketClient();
		client.setHandler(new IoHandler() {
			
			@Override
			public void sessionOpened(IoSession session) throws Exception {
//				System.out.println("sessionOpened");
			}
			
			@Override
			public void sessionIdle(IoSession session, IdleStatus idle) throws Exception {
//				System.out.println("sessionIdle");
			}
			
			@Override
			public void sessionCreated(IoSession session) throws Exception {
				System.out.println("sessionCreated");
			}
			
			@Override
			public void sessionClosed(IoSession session) throws Exception {
				System.out.println("sessionClosed");
			}
			
			@Override
			public void messageSent(IoSession session, Object message) throws Exception {
				System.out.println("messageSent");
				
			}
			
			@Override
			public void messageReceived(IoSession arg0, Object message) throws Exception {
				System.out.println("messageReceived");
				
			}
			
			@Override
			public void exceptionCaught(IoSession arg0, Throwable t)
					throws Exception {
				
			}
		});
		ConnectFuture cf = client.connect("127.0.0.1", 1234);
		
		client.send(cf, "hello".getBytes());
		client.send(cf, " world!".getBytes());
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.close(cf);
		
		client.dispose();
	}

}
