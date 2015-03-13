package sapere.model;

import java.util.HashMap;

import sapere.model.reaction.Product;
import sapere.model.reaction.PropertyValue;
import sapere.model.reaction.Rate;
import sapere.model.reaction.ReactionScoreFunction;
import sapere.model.reaction.Reagent;

public interface IReaction {

	public double getCandidateLsaScore(String id);
	
	public Lsa getCandidateLsa(String id);
	
	public void setCandidateLsa(String id,Lsa candidateLsa);
	
	public void setCandidateLsaScore(String id,double candidateLsaScore);
	
	public Rate getRate();

	public void setRate(Rate rate);

	public void addReagent(Reagent r);

	public void addProduct(Product p);

	public HashMap<String, PropertyValue> getVars();

	public Reagent getReagent(String identifier);

	public String[] getReagentIds();
	
	public Reagent[] getReagents();
	
	public void clearBindings();

	public Transaction getExecutableTransaction();

	public ReactionScoreFunction getScoreFunction();
	
	public void setScore(ReactionScoreFunction score);

	public void setMapping(String reagentId, String lsaId);

	public String getMapping(String reagentId);

}