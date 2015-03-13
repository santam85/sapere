package sapere.controller.n2n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import sapere.controller.InternalAgent;
import sapere.controller.Node;
import sapere.controller.reactionmanager.ReactionManager;
import sapere.model.SpaceOperation;
import sapere.model.SpaceOperationType;

public class DiffusionServer extends InternalAgent{
	private ServerSocket serv;
	
	private static DiffusionServer instance;
	
	public static DiffusionServer getInstance(){
		if(instance == null)
			return instance = new DiffusionServer();
		else
			return instance;
	}
	
	private DiffusionServer(){
		super("node_manager");
		try {
			serv = new ServerSocket(Node.getDiffPortIn());
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}
	
	public void run(){
		spy("activated");
		while(true){
			try {
				Socket sock = serv.accept();
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				SpaceOperation op = (SpaceOperation)ois.readObject();
				sock.close();
				op.setType(SpaceOperationType.Inject);
				ReactionManager.getInstance().queueOperation(op, null);
				spy("received diffusion "+op.getLsa());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			
		}
	}
}
