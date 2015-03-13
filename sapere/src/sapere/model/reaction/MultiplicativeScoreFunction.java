package sapere.model.reaction;

public class MultiplicativeScoreFunction extends ReactionScoreFunction {

	@Override
	public double getScore() {
		double tmp = 1.0;
		for(Reagent re:parentReaction.getReagents())
			tmp*=parentReaction.getCandidateLsaScore(re.getIdentifier());
		return tmp;
	}

}
