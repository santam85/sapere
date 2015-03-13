package sapere.model.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sapere.model.IReaction;
import sapere.model.Lsa;
import sapere.model.SpaceOperation;
import sapere.model.Transaction;

public class Reaction implements IReaction {

	private ArrayList<Reagent> reagents;
	private HashMap<String,PropertyValue> vars;
	private HashMap<String,String> mapping;
	private ArrayList<Product> products;
	
	private ReactionScoreFunction score;
	
	private Transaction transactionInstance;

	private Rate rate;
	
	private HashMap<String,Lsa> candidateLsa;
	private HashMap<String,Double> candidateLsaScore;
	
	public Reaction(Rate rate, ReactionScoreFunction score){
		score.setParentReaction(this);
		
		this.rate = rate;
		this.score = score;

		reagents = new ArrayList<Reagent>();
		products = new ArrayList<Product>();
		
		vars = new HashMap<String,PropertyValue>();
		mapping = new HashMap<String, String>();
		
		candidateLsa = new HashMap<String, Lsa>();
		candidateLsaScore = new HashMap<String, Double>();
	}
	
	public double getCandidateLsaScore(String id){
		return candidateLsaScore.get(id).doubleValue();
	}
	
	public Lsa getCandidateLsa(String id){
		return candidateLsa.get(id);
	}
	
	public void setCandidateLsa(String id,Lsa candidateLsa){
		this.candidateLsa.put(id,candidateLsa.getCopy());
		
		setMapping(id, candidateLsa.getId());
		if(this.getReagent(id)!=null){
			for(Property p:this.getReagent(id).properties.values()){
				if(p.getValue() instanceof VarPropertyValue){
					VarPropertyValue var = ((VarPropertyValue)p.getValue());
					var.setValue(candidateLsa.getContent().getProperty(p.getName()).getValue().getCopy());
				}
				if(p.getValue() instanceof MatchPropertyValue){
					MatchPropertyValue var = ((MatchPropertyValue)p.getValue());
					vars.put(var.getMatchSource(), candidateLsa.getContent().getProperty(p.getName()).getValue().getCopy());
				}
			}
		}
	}
	
	public void setCandidateLsaScore(String id,double candidateLsaScore){
		this.candidateLsaScore.put(id, new Double(candidateLsaScore));
	}

	@Override
	public Rate getRate(){
		return rate;
	}
	
	@Override
	public void setRate(Rate rate){
		this.rate = rate;
	}

	@Override
	public void addReagent(Reagent r){
		r.setParentReaction(this);
		reagents.add(r);
	}
	
	@Override
	public void addProduct(Product p){
		p.setParentReaction(this);
		products.add(p);	
	}
	
	@Override
	public HashMap<String, PropertyValue> getVars() {
		return vars;
	}
	
	@Override
	public Reagent getReagent(String identifier){
		for(Reagent r:reagents)
			if(r.getIdentifier().equals(identifier))
				return r;
		return null;
	}
	
	@Override
	public String[] getReagentIds(){
		String[] tmp = new String[reagents.size()];
		int i=0;
		for(Reagent r:reagents)
			tmp[i++]=r.getIdentifier();
			
		return tmp;
	}
	
	@Override
	public Reagent[] getReagents(){
		return reagents.toArray(new Reagent[reagents.size()]);
	}
	
	@Override
	public void clearBindings(){
		vars.clear();
		mapping.clear();
		
		candidateLsa.clear();
		candidateLsaScore.clear();
	}
	
	@Override
	public String toString(){
		String tmp = "";
		for(Reagent r:reagents){
			tmp+=r.toString()+" + ";
		}
		tmp = tmp.substring(0, tmp.length()-3);
		tmp+=" -"+rate+"-> ";
		for(Product p:products){
			tmp+=p.toString() + " + ";
		}
		tmp = tmp.substring(0, tmp.length()-3);
		return tmp;
	}
	
	@Override
	public Transaction getExecutableTransaction() {	
		//NEW TRANSACTION
		transactionInstance = new Transaction();
		List<SpaceOperation> operations = transactionInstance.getOperations();
		//FOREACH PRODUCT GET OPERATION AND ADD TO TRANSACTION
		for(Product p:products){
			SpaceOperation op = p.getOperation();
			if(op!=null)
				operations.add(op);
		}
		//RETURN TRANSACTION	
		return transactionInstance;
	}

	@Override
	public ReactionScoreFunction getScoreFunction() {
		return score;
	}

	@Override
	public void setScore(ReactionScoreFunction score) {
		score.setParentReaction(this);
		this.score=score;
	}
	
	@Override
	public void setMapping(String reagentId, String lsaId){
		mapping.put(reagentId, lsaId);
	}
	
	@Override
	public String getMapping(String reagentId){
		return mapping.get(reagentId);
	}
	
}