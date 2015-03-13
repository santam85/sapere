package sapere.controller.n2n;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sapere.misc.Logger;
import sapere.model.Content;
import sapere.model.Lsa;
import sapere.model.SpaceOperation;
import sapere.model.SpaceOperationType;

public class DiffusionWorker implements Runnable{
	
	private Lsa lsa;
	private String destAddress;

	public DiffusionWorker(Lsa lsa,String destAddress){
		this.lsa = lsa;
		this.destAddress = destAddress;
	}
	
	@Override
	public void run() {
		try{
			Content resLsa = lsa.getContent();
			if (destAddress!=null && resLsa!=null){
				try {
					String[] addr = destAddress.split(":");
					Socket sock = new Socket(addr[0],Integer.parseInt(addr[1]));
					SpaceOperation diff = new SpaceOperation(SpaceOperationType.Inject,null,new Lsa(resLsa),null);
					ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
					oos.writeObject(diff);
					spy("successfully diffused "+lsa);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				throw new Exception("Diffusion Error: malformed LSA");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void spy(String m){
		Logger.getInstance().log("["+System.currentTimeMillis()+"] diffuse_worker - "+m);
	}
}
