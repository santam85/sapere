package sapere.controller.skel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sapere.controller.SpaceAgent;
import sapere.controller.stub.SapereOperation;
import sapere.controller.stub.SapereOperationType;
import sapere.model.communication.Notification;
import sapere.model.communication.Response;

public class SkelAgent extends SpaceAgent{
	
	private Socket socket;

	public SkelAgent(String id, Socket socket) {
		super(id);
		this.socket = socket;
	}
	
	public void run(){
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			SapereOperation op = (SapereOperation)ois.readObject();
			if (op.getType() == SapereOperationType.Inject){
				doInject(op.getLsa());
				Response res = new Response(true,op);
				oos.writeObject(res);
			}
			else if(op.getType() == SapereOperationType.Observe){
				boolean res = doObserve(op.getLsaId(),op.getFilter());
				if (res){
					Notification note = (Notification)input.take();
					oos.writeObject(note);
				}
				else{
					Response ms = new Response(false,op);
					oos.writeObject(ms);
				}
			}
			else if(op.getType() == SapereOperationType.InjectObserve){
				doInjectObserve(op.getLsa(), op.getFilter());
				
				Notification note = (Notification)input.take();
				oos.writeObject(note);
			}
			else if(op.getType() == SapereOperationType.Update){
				boolean ris = doUpdate(op.getLsaId(), op.getNewContent());
				Response res = new Response(ris,op);
				oos.writeObject(res);
			}
			else if(op.getType() == SapereOperationType.Remove){
				boolean ris = doRemove(op.getLsaId());
				Response res = new Response(ris,op);
				oos.writeObject(res);
			}
			socket.close();
			spy("shutting down...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
