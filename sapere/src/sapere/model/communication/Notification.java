package sapere.model.communication;

import java.io.Serializable;

import sapere.model.Content;
import sapere.model.SpaceOperationType;


public class Notification extends Message implements Serializable{
	
	private static final long serialVersionUID = -6417756713784371365L;
	private SpaceOperationType type;
	private String subject;
	private Content content;
	
	public Notification(SpaceOperationType type, String sub, Content mod){
		this.type = type;
		this.subject = sub;
		//inseriti solo dal reaction manager
		this.content = mod;
	}
	
	public SpaceOperationType getType() {
		return type;
	}
	
	public Notification getCopy(){
		if (content!=null){
			return new Notification(type, subject, content.getCopy());
		}
		return new Notification(type, subject, null);
	}
	
	public Content getNewContent() {
		return content;
	}
	
	public String getLsaSubjectId() {
		return subject;
	}
	
	@Override
	public String toString(){
		if (content!=null)
			return "Notify[type="+type+" subject="+subject+" newContent="+content+"]";
		else
			return "Notify[type="+type+" subject="+subject+"]";
	}
}
