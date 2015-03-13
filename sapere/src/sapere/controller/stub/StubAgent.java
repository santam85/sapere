package sapere.controller.stub;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sapere.controller.InternalAgent;
import sapere.model.communication.Message;
import sapere.model.communication.Notification;

public class StubAgent implements Runnable{

	private InternalAgent service;
	private SapereOperation op;
	private int port;
	
	public StubAgent(SapereOperation op, InternalAgent service,int port){
		this.service = service;
		this.op = op;
		this.port = port;
	}
	
	@Override
	public void run() {
		Socket sock = null;
		try {
			sock = new Socket("localhost", port);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(op);
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Message note = (Message)ois.readObject();
			sock.close();
			service.sendMessage(note);
		} catch (Exception e) {
			service.sendMessage(new Notification(null, e.getMessage(), null));
		}
	}
}
