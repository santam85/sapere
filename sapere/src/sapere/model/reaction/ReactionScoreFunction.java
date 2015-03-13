package sapere.model.reaction;

import sapere.model.IReaction;

public abstract class ReactionScoreFunction {
	
	protected IReaction parentReaction;
	
	public abstract double getScore();

	protected IReaction getParentReaction() {
		return parentReaction;
	}

	protected void setParentReaction(IReaction parentReaction) {
		this.parentReaction = parentReaction;
	}
	
}
