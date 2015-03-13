package sapere.model;

import java.util.HashMap;
import java.util.Map;

import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.lib.JavaLibrary;

import sapere.model.reaction.Property;

public class LsaFactory {
	private static Prolog engine;
	private static LsaFactory instance;
	private static Class<? extends Content> type;
	
	public LsaFactory(){
		engine = new Prolog();
	}
	
	public static LsaFactory getInstance(){
		return getInstance(JavaLsaContent.class);
	}
	
	public static LsaFactory getInstance(Class<? extends Content> c){
		if(instance==null){
			instance = new LsaFactory();
			type=c;
		}
		return instance;
	}
	
	public Lsa createLsa(String name, Property[] properties) {
		return new Lsa(createContent(name,properties));
	}
	
	public Lsa createLsa(String id, String name, Property[] properties) {
		return new Lsa(id,createContent(name,properties));
	}
	
	public Content createContent(String name, Property[] properties) {
		try {
			return type.getConstructor(String.class,Property[].class).newInstance(name,properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Lsa createLsaFromProlog(Term term){
		return createLsaFromProlog(null,term.toString());
	}
	
	public Lsa createLsaFromProlog(String id, Term term){
		return createLsaFromProlog(id,term.toString());
	}
	
	public Lsa createLsaFromProlog(String term){
		return createLsaFromProlog(null,term);
	}
	
	public Lsa createLsaFromProlog(String id,String term){
		return new Lsa(id,createContentFromProlog(term));
	}
	
	public Content createContentFromProlog(String term){
		return createContentFromProlog(type,term);
	}
	
	public <T extends Content> T createContentFromProlog(Class<T> type, String term){
		try {
			String name = parseName(term);
			Property[] properties = parseProperties(term);
			if(name == null || properties == null)
				return null;
			return type.getConstructor(String.class,Property[].class).newInstance(new Object[]{name,properties});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String contentToPrologString(Lsa lsa){
		String content = "";
		
		for(Property p:lsa.getContent().getProperties()){
			content += p.getName()+"("+p.getValue()+"),";
		}
		content = content.substring(0, content.length()-1);
		
		return lsa.getContent().getName()+"("+content+")";
		
	}
	
	private static Property[] parseProperties(String content) {
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
				return null;
			}
			
			return p.values().toArray(new Property[p.size()]);
		}
	}

	private static String parseName(String content) {
		int index = content.indexOf('(');
		if(index > 0 && index<content.length())
			return content.substring(0, index);
		else return null;
	}

	

}
