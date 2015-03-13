package sapere.controller;

import sapere.controller.notifier.Notifier;
import sapere.controller.reactionmanager.ReactionManager;
import sapere.model.Content;
import sapere.model.IContentFilter;
import sapere.model.ISpaceManager;
import sapere.model.Lsa;
import sapere.model.SpaceOperation;
import sapere.model.SpaceOperationType;
import sapere.model.communication.SubscriptionRequest;

public class SpaceAgent extends InternalAgent {

	protected ISpaceManager spaceManager;
	
	public SpaceAgent(String idAgent) {
		super(idAgent);
		try {
			spaceManager = ReactionManager.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected boolean doInject(Lsa lsa){
		try {
			spaceManager.queueOperation(new SpaceOperation(SpaceOperationType.Inject, null, lsa, null),null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	protected boolean doRemove(String lsa){
		try {
			spaceManager.queueOperation(new SpaceOperation(SpaceOperationType.Remove, lsa, null, null),null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	protected boolean doUpdate(String lsa, Content newCont){
		try {
			spaceManager.queueOperation(new SpaceOperation(SpaceOperationType.Update, lsa, null, newCont),null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	protected boolean doObserve(String lsa, IContentFilter filter){
		try {
			Notifier.getInstance().sendMessage(new SubscriptionRequest(lsa,this,filter));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	protected boolean doInjectObserve(Lsa lsa, IContentFilter filter){
		//non so ancora l'id dell'lsa...
		SubscriptionRequest sub = new SubscriptionRequest(null,this,filter);
		try {
			spaceManager.queueOperation(new SpaceOperation(SpaceOperationType.Inject, null, lsa, null),sub);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
