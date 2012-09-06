package devutilsplugin.utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class SocketServer {
	SocketAcceptor acceptor = null;
	IoHandler handler = null;
	List<Integer> portList = new ArrayList<Integer>();
	
	public SocketServer() {
	}
	
	public void setHandler(IoHandler handler){
		this.handler = handler;
	}
	
	public List<Integer> addBind(int port) throws Exception {
		dispose();
		acceptor = new NioSocketAcceptor();
		if(handler != null){
			acceptor.setHandler(handler);
		}
		acceptor.setReuseAddress(true);

		if(!portList.contains(port)){
			portList.add(port);
		}
		List<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();
		for(int i=0; i<portList.size(); i++){
			InetSocketAddress addr = new InetSocketAddress(portList.get(i));
			try{
				acceptor.bind(addr);
			}catch(Exception e){
				continue;
			}
			addrs.add(new InetSocketAddress(portList.get(i)));
		}
		portList.clear();
		for(int i=0; i<addrs.size(); i++){
			portList.add(addrs.get(i).getPort());
		}
		return portList;
	}

	public List<Integer> unBind(int port) throws Exception{
		acceptor.unbind(new InetSocketAddress(port));
		if(portList.contains(port)){
			portList.remove((Object)port);
		}
		return portList;
	}
	
	public void dispose(){
		if(acceptor != null){
			acceptor.dispose(false);
		}
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SocketServer serv = new SocketServer();
		serv.addBind(1234);
		serv.addBind(5678);
		

	}

}
