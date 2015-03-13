package sapere.controller.notifier;

import java.util.ArrayList;
import java.util.Hashtable;

import sapere.controller.InternalAgent;
import sapere.model.Content;
import sapere.model.IContentFilter;
import sapere.model.SpaceOperationType;
import sapere.model.communication.Message;
import sapere.model.communication.Notification;
import sapere.model.communication.SubscriptionRequest;

public class Notifier extends InternalAgent{
	
	private Hashtable<String, ArrayList<SubscriptionRequest>> subscribers;
	private static Notifier instance;
	
	public static Notifier getInstance() {
		if(instance == null)
			return instance = new Notifier();
		else
			return instance;
	}
	
	private Notifier() {
		super("notifier");
		subscribers = new Hashtable<String, ArrayList<SubscriptionRequest>>();
		start();
	}
	
	public void run(){
		spy("activated");
		while(true){
			Message note = null;
			try {
				note = input.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (note instanceof SubscriptionRequest){
				manageSubscription((SubscriptionRequest)note);
			}
			else if(note instanceof Notification){
				manageNotification((Notification)note);
			}
		}
	}
	private void manageSubscription(SubscriptionRequest sub){
		spy("subscription received.");
		ArrayList<SubscriptionRequest> res = subscribers.get(sub.getLsaSubjectId());
		if (res!=null){
			res.add(sub);
		}
		else
		{
			ArrayList<SubscriptionRequest> list = new ArrayList<SubscriptionRequest>();
			list.add(sub);
			
			subscribers.put(sub.getLsaSubjectId(), list);
		}
		printSubscribersList();
	}
	
	private void manageNotification(Notification notify){
		spy("notification received: "+notify);
		String lsaId = notify.getLsaSubjectId();
		ArrayList<SubscriptionRequest> notificables = subscribers.get(lsaId);

		if (notificables==null || notificables.size()==0) return;
		
		spy("found subscribers...");
		for (SubscriptionRequest n : notificables){
			if (notify.getType()==SpaceOperationType.Update){
				IContentFilter filter = n.getContentFilter();
				Content cont = notify.getNewContent();
				if (filter!=null) cont = filter.filters(notify.getNewContent());
				Notification newNote = new Notification(notify.getType(), notify.getLsaSubjectId(), cont);
				try {
					n.getSubscriber().sendMessage(newNote);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					n.getSubscriber().sendMessage(notify.getCopy());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//L'iscrizione viene rimossa
			subscribers.remove(lsaId+"");
		}
		printSubscribersList();
	}
	
	private void printSubscribersList(){
		String msg ="subscribers list: ";
		
		if (subscribers.keySet().isEmpty())
			msg+="empty";
		else{
			msg+=" \n-----------\n";
			for (String id : subscribers.keySet()){
				msg += id+" : [";
				for (SubscriptionRequest ia : subscribers.get(id)){
					msg+=" "+ia.getSubscriber()+" ";
				}
				msg+="]"+"\n";
			}
			msg+="-----------";
		}
		spy(msg);
	}
	
}
