package sapere.model.reaction;

import sapere.model.IReaction;
import sapere.model.Transaction;

public class TotalScore implements Comparable<TotalScore>{
	public double score;
	public IReaction reaction;
	public Transaction transaction;
	public String[] candidates;
	
	public TotalScore(double score,IReaction reaction,String[] candidates, Transaction transaction){
		this.score=score;
		this.reaction=reaction;
		this.candidates=candidates;
		this.transaction=transaction;
	}
	
	@Override
	public int compareTo(TotalScore sc) {
		return Double.compare(score,sc.score);
	}
	
}
