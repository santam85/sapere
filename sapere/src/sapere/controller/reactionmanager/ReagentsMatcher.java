package sapere.controller.reactionmanager;

import java.util.LinkedList;
import java.util.List;

import sapere.model.IReaction;
import sapere.model.Lsa;
import sapere.model.Transaction;
import sapere.model.reaction.PropertyValue;
import sapere.model.reaction.Reagent;
import sapere.model.reaction.TotalScore;

public abstract class ReagentsMatcher {
	
	/**
	 * Starting from a list of reagent and a reaction, computes the products and the rate of the reaction
	 * 
	 * @param lsas
	 * @param r
	 * @return
	 */
	public List<TotalScore> score(Lsa[] lsas, IReaction r, double treshold) {
		LinkedList<TotalScore> list = new LinkedList<TotalScore>();
		r.clearBindings();
		//ottenere la lista ordinata dei reagenti
		Reagent[] reagents = r.getReagents();
		
		//se solo prodotti, scriverli con l'appropriato rate
		if(reagents.length == 0){
			//finiti i reagenti, ottenere la transazione
			Transaction t = r.getExecutableTransaction();
			
			//salvare l'elenco dei candidati originali, potrebbe tornare utile
			String[] candids = new String[reagents.length];
			int i=0;
			for(Reagent reag:reagents)
				candids[i++]=r.getCandidateLsa(reag.getIdentifier()).getId();
			
			//aggiungere il nuovo score alla lista
			list.add(new TotalScore(1,r,candids,t));
		}
		
		Lsa[][] reagentCandidates = new Lsa[r.getReagentIds().length][];
		//per ogni reagente trovare la lista dei candidati
		for(int i =0;i<reagents.length;i++){
			reagentCandidates[i] = filterCandidates(reagents[i],lsas,treshold);
			//se un reagente non ha candidati, la reazione non viene eseguita
			if(reagentCandidates[i].length<1)
				return list;
		}
		
		//dalla lista di liste dei candidati, ottenere la lista di liste delle combinazioni
		Lsa[][] combs = getCombinations(reagentCandidates);
		
		//per ogni combinazione di reagenti
		for(Lsa[] comb:combs){
			//pulire la mappa dei candidati, delle variabili e degli id
			r.clearBindings();
			
			//impostare i candidati tutti in una volta
			for(int i=0;i<reagents.length;i++){
				Reagent re = reagents[i];
				Lsa lsa = comb[i];
				r.setCandidateLsa(re.getIdentifier(),lsa);
			}
			
			boolean clashes=false;
			
			//scorrendo in ordine i reagenti, unificare reagente e lsa, sostituire mano a mano le variabili settando il candidate lsa per ciascuno
			for(int i =0;i<reagents.length;i++){
				Reagent re = reagents[i];
				Lsa lsa = comb[i];
				
				for(Reagent r1:reagents){
					if(r.getMapping(r1.getIdentifier()).equals(r.getMapping(re.getIdentifier())) && !r1.getIdentifier().equals(re.getIdentifier())){
						clashes = true;
						break;
					}
				}
				
				double lsascore = match(re,lsa);
				if(!(lsascore>treshold)){
					clashes=true;
					break;
				}
				
				r.setCandidateLsaScore(re.getIdentifier(),lsascore);
			}
			if(clashes)
				continue;
			
			//finiti i reagenti, ottenere la transazione
			Transaction t = r.getExecutableTransaction();
			
			//salvare l'elenco dei candidati originali, potrebbe tornare utile
			String[] candids = new String[reagents.length];
			int i=0;
			for(Reagent reag:reagents)
				candids[i++]=r.getCandidateLsa(reag.getIdentifier()).getId();
			
			//aggiungere il nuovo score alla lista
			list.add(new TotalScore(r.getScoreFunction().getScore(),r,candids,t));
		}
	
		return list;
	}
	
	protected abstract double match(Reagent r, Lsa lsa);
	protected abstract double fuzzyMatch(PropertyValue lsaProp, PropertyValue rProp, String string);
	
	protected Lsa[] filterCandidates(Reagent r, Lsa[] lsas, double treshold){
		LinkedList<Lsa> tmp = new LinkedList<Lsa>();
		for(Lsa lsa:lsas){
			double score = match(r,lsa);
			if(score>treshold)
				tmp.add(lsa);
		}
			
		return tmp.toArray(new Lsa[tmp.size()]);
	}
	
	protected static Lsa[][] getCombinations(Lsa[][] lsas){
		int[] indexes = new int[lsas.length];
		int combs = 1;
		for(int i=0;i<lsas.length;i++)
			combs*=lsas[i].length;

		Lsa[][] oo = new Lsa[combs][];
		
		for(int i=0;i<oo.length;i++){
			Lsa[] tmp = new Lsa[lsas.length];
			for(int j=0;j<lsas.length;j++)
				tmp[j]=lsas[j][indexes[j]];
			oo[i]=tmp;
			boolean carry = true;
			for(int j=lsas.length-1;j>=0;j--){
				if(carry){
					indexes[j]++;
					carry=false;
					if(indexes[j]==lsas[j].length){
						indexes[j]=0;
						carry = true;
					}
				}		
			}
		}
		
		return oo;
	}
}
