package sapere.controller.reactionmanager;

import sapere.model.communication.SubscriptionRequest;

public class SapereEvent<E> implements Comparable<SapereEvent<?>>{
	public static final int EXTERNAL = 0;
	public static final int INTERNAL = 1;
	
	private long time;
	private E event;
	private SubscriptionRequest sub;
	private int source;
	
	public SapereEvent(long time, E event, SubscriptionRequest sub, int source){
		this.time = time;
		this.event = event;
		this.sub = sub;
		this.source = source;
	}

	public SapereEvent(long time, E event, int source) {
		this.time = time;
		this.event = event;
		this.source = source;
	}

	protected long getTime() {
		return time;
	}

	protected void setTime(long time) {
		this.time = time;
	}

	protected E getEvent() {
		return event;
	}

	protected void setEvent(E event) {
		this.event = event;
	}
	
	protected SubscriptionRequest getSubscription() {
		return sub;
	}

	protected void setSubscription(SubscriptionRequest sub) {
		this.sub = sub;
	}

	public int getSource() {
		return source;
	}

	@Override
	public int compareTo(SapereEvent<?> se) {
		long diff = time - se.getTime();
		//Confronta il tempo. Se i tempi sono uguali confronta la sorgente: fai prima quelli esterni.
		return (diff<0)?-1:((diff>0)?1:(source==EXTERNAL)?(se.getSource()==INTERNAL?-1:0):(se.getSource()==INTERNAL?0:1));
	}
	
	@Override
	public String toString() {
		return "<"+this.time+" - "+((this.source==INTERNAL)?"internal":"external") +"> "+this.event;
	}

}
