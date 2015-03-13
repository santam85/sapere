package sapere.model;

import sapere.model.communication.SubscriptionRequest;

public interface ISpaceManager {
	void queueTransaction(Transaction t) throws Exception;
	void queueOperation(SpaceOperation iso, SubscriptionRequest sub);
}
