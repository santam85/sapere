package sapere.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.lib.JavaLibrary;

import sapere.model.reaction.Property;

public class LogicLsaContent extends Content {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2972774335689925807L;
	protected Term term;
	private static Prolog engine;
	/*private static String theory = "" +
		"lsa(test(content(20))).\n"+
		"lsa(test(listc([1,2,3]),numc(20))).\n"+
		"\n"+
		"getname(Lsa,N):-Lsa=..[N|_].\n"+
		"setname(ILsa,N,OLsa):-ILsa=..[_|T],OLsa=..[N|T].\n"+
		"getproperty(Lsa,Pname,Pval):-Lsa=..L,member(P,L),P=..[Pname,Pval].\n"+
		"setproperty(ILsa,Pname,Pval,OLsa):-ILsa=..L,sp(L,LO,Pname,Pval),OLsa=..LO.\n"+
		"sp([],[],_,_).\n"+
		"sp([H|T],[P|T],N,V):-H=..[N|_],!,P=..[N,V].\n"+
		"sp([H|T1],[H|T2],N,V):-sp(T1,T2,N,V).\n"+
		"\n"+
		"\n"+
		"member2([H|T],H,T).\n"+
		"member2([H|T],H2,[H|T2]):-member(T,H2,T2).";*/
	
	public LogicLsaContent(Content content){
		engine = new Prolog();
		
		String tmp = "";
		Property[] ps = content.getProperties();
		int i=0;
		for(;i<ps.length-1;i++)
			tmp+=ps[i].getName()+"("+ps[i].getValue()+"),";
		tmp+=ps[i].getName()+"("+ps[i].getValue()+")";
		term = Term.createTerm(content.getName()+"("+tmp+")");
	}
	
	public LogicLsaContent(String term){
		engine = new Prolog();
		
		this.term = Term.createTerm(term);
	}

	public LogicLsaContent(String name, Property[] properties){
		engine = new Prolog();
		
		String tmp = "";
		int i=0;
		for(;i<properties.length-1;i++)
			tmp+=properties[i].getName()+"("+properties[i].getValue()+"),";
		tmp+=properties[i].getName()+"("+properties[i].getValue()+")";
		term = Term.createTerm(name+"("+tmp+")");
	}
	
	public LogicLsaContent(Term term) {
		engine = new Prolog();
		
		this.term=term;
	}

	@Override
	public String getName(){
		try {
			engine.solve(term.toString()+"=..L.").getVarValue("L");
			String tmp = term.toString();
			return tmp.substring(0,tmp.indexOf("("));
		} catch (NoSolutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedGoalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void setName(String name){
		String tmp = name + term.toString().substring(term.toString().indexOf("("));
		this.term=Term.createTerm(tmp);
	}
	
	@Override
	public void addProperty(Property p){
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		c.addProperty(p);
		term = Term.createTerm(toString(c));
	}
	
	@Override
	public boolean hasProperty(String propertyName) {
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		return c.hasProperty(propertyName);
	}
	
	@Override
	public Property getProperty(String propertyName){
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		return c.getProperty(propertyName);
	}
	
	@Override
	public void setProperty(Property p){
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		c.setProperty(p);
		term = Term.createTerm(toString(c));
	}
	
	@Override
	public void deleteProperty(String propertyName){
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		c.deleteProperty(propertyName);
		term = Term.createTerm(toString(c));
	}
	
	@Override
	public LogicLsaContent getCopy(){
		return new LogicLsaContent(term.toString());
	}

	@Override
	public Property[] getProperties() {
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		return c.getProperties();
	}

	@Override
	public void setProperties(Property[] properties) {
		Content c = new JavaLsaContent(parseName(term.toString()),parseProperties(term.toString()).values().toArray(new Property[0]));
		c.setProperties(properties);
		term = Term.createTerm(toString(c));
	}
	
	public Term getTerm(){
		return term;
	}
	
	private static Map<String,Property> parseProperties(String content) {
		String[] toks = content.split("(?=,|\\[|\\]|\\(|\\))|(?<=,|\\[|\\]|\\(|\\))");

		int i=0;
		String toklist = "[";
		for(;i<toks.length-1;i++){
			toklist+="'"+toks[i]+"',";
		}
		toklist+="'"+toks[i]+"']";
		
		String theory =":-load_library('alice.tuprolog.lib.DCGLibrary').\n" +
		"\n"+
		"reagent-->name(N),['('],properties,[')'].\n"+
		"name(X)-->[X],{atom(X)}.\n"+
		"properties-->property,propertyrest.\n"+
		"properties-->[].\n"+
		"propertyrest-->[','],property,propertyrest.\n"+
		"propertyrest-->[].\n"+
		"property-->name(X),['('],value(V),[')'],{java_object('sapere.model.reaction.Property',[X,V,_],O),p<-put(X,O)}.\n"+
		"value(O)-->[X],{atom(X),java_object('sapere.model.reaction.SinglePropertyValue',[X],O)}.\n"+
		"value(O)-->['['],{java_object('sapere.model.reaction.SetPropertyValue',[],O)},valueset(O),[']'].\n"+
		"valueset(O)-->[X],valuesetrest(O),{atom(X),O<-addValue(X)}.\n"+
		"valuesetrest(O)-->[','],[X],valuesetrest(O),{atom(X),O<-addValue(X)}.\n"+
		"valuesetrest(_)-->[].\n";
		
		synchronized(engine){
		Map<String, Property> p = new HashMap<String, Property>();
			try {
				engine.setTheory(new Theory(theory));
				((JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).unregister(new Struct("p"));
				((JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(new Struct("p"), p);
				engine.solve("phrase(reagent,"+toklist+").");
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			return p;
		}
	}

	private static String parseName(String content) {
		return content.substring(0, content.toString().indexOf('('));
	}
	
	public static String[] toArray(Term term){
		List<String> tmp = new LinkedList<String>();
		try {
			SolveInfo si = engine.solve("member(X,"+term+").");
			if(si.isSuccess()){
				tmp.add(si.getVarValue("X").toString());
				while(si.hasOpenAlternatives()){
					si=engine.solveNext();
					if(si.isSuccess())
						tmp.add(si.getVarValue("X").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp.toArray(new String[tmp.size()]);
	}
	
	public static Term listToTerm(String[] list){
		String tmp = "[";
		int i=0;
		for(;i<list.length-1;i++)
			tmp+=list[i]+",";
		return Term.createTerm(tmp+=list[i]+"].");
	}
	
	private String toString(Content c){
		String tmp = "";
		int i=0;
		for(;i<c.getProperties().length-1;i++)
			tmp+=c.getProperties()[i].getName() + "(" +c.getProperties()[i].getValue()+"),";
		tmp+=c.getProperties()[i].getName() + "(" +c.getProperties()[i].getValue()+")";
		return c.getName()+"("+tmp+")";
	}
	
	@Override
	public String toString(){
		return toString(this);
	}

}
