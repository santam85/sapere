package sapere.model.communication;

import java.io.Serializable;

import sapere.controller.InternalAgent;
import sapere.model.IContentFilter;


public class SubscriptionRequest extends Message implements Serializable{

	private static final long serialVersionUID = -5721538661270246401L;
	private String subjectId;
	private InternalAgent subscriber;
	private IContentFilter contentFilter;
	
	public SubscriptionRequest(String id, InternalAgent subscriber, IContentFilter filter){
		this.subjectId = id;
		this.subscriber = subscriber;
		this.contentFilter = filter;
	}
	public InternalAgent getSubscriber() {
		return subscriber;
	}

	public String getLsaSubjectId() {
		return subjectId+"";
	}
	@Override
	public SubscriptionRequest getCopy() {
		return new SubscriptionRequest(subjectId, subscriber, contentFilter);
	}
	public IContentFilter getContentFilter() {
		return contentFilter;
	}
	@Override
	public String toString(){
		return "Subscription[subjectId="+subjectId+" subscriber="+subscriber+"]";
	}
}
