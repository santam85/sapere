package sapere.model;

import java.io.Serializable;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;

public class LogicContentFilter implements IContentFilter,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4558583445338331514L;
	private final static Prolog engine = new Prolog();
	private Term pattern;
	
	public LogicContentFilter(Term t){
		pattern = t;
	}
	
	public LogicContentFilter(String t){
		pattern = Term.createTerm(t);
	}
	
	@Override
	public Content filters(Content c) {
		if (c instanceof LogicLsaContent){
			LogicLsaContent cont = (LogicLsaContent)c;
			SolveInfo info;
			try{
				String goal = "unify("+pattern+","+cont.getTerm()+").";
				info = engine.solve(goal);
				return new LogicLsaContent(info.getVarValue("X"));
			} catch (Exception ex){
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return pattern+"";
	}
	
	public Term getTerm(){
		return pattern;
	}
}
