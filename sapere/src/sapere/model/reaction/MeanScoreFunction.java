package sapere.model.reaction;

public class MeanScoreFunction extends ReactionScoreFunction {

	@Override
	public double getScore() {
		double tmp = 0.0;
		int i = 0;
		for(Reagent re:parentReaction.getReagents()){
			tmp+=parentReaction.getCandidateLsaScore(re.getIdentifier());
			i++;
		}
		return tmp/i;
	}

}
