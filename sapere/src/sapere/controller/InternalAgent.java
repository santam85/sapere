package sapere.controller;

import java.util.concurrent.PriorityBlockingQueue;

import sapere.misc.Logger;
import sapere.model.communication.Message;

public class InternalAgent extends Thread{
	protected PriorityBlockingQueue<Message> input;
	private String id;
	
	public InternalAgent(String id){
		input = new PriorityBlockingQueue<Message>(100);
		this.id = id;
	}
	
	public void sendMessage(Message note){
		input.put(note);
	}
	
	protected void spy(String s){
		Logger.getInstance().log("["+System.currentTimeMillis()+"] "+id+" - "+s);
	}
	
	public String getAgentId(){
		return id;
	}
	
	public String toString(){
		return id;
	}
}
