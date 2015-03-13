package sapere.controller.skel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sapere.controller.InternalAgent;
import sapere.controller.Node;

public class SkelServer extends InternalAgent{
	private ServerSocket serv;
	private Executor executor;
	
	private static SkelServer instance;
	
	public static SkelServer getInstance(){
		if(instance == null)
			return instance = new SkelServer();
		else
			return instance;
	}
	
	public SkelServer(){
		super("skel_manager");
		try {
			serv = new ServerSocket(Node.getServicesPort());
			executor = Executors.newCachedThreadPool();
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		spy("activated");
		while(true){
			try {
				Socket sock = serv.accept();
				SkelAgent skel = new SkelAgent("skelAgent-"+sock.hashCode(),sock);
				executor.execute(skel);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
