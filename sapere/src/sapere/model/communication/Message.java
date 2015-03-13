package sapere.model.communication;

public abstract class Message implements Comparable<Message>{
	
	private long time;
	
	public Message(){
		time = System.currentTimeMillis();
	}
	
	public abstract Message getCopy();
	
	public long getTimeStamp(){
		return time;
	}
	
	public int compareTo(Message m){
		return (int)(time-m.getTimeStamp());
	}
}
