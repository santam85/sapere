package sapere.model.reaction;

import java.util.HashMap;
import java.util.Map;

import sapere.model.IReaction;

public class Reagent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5061460770741155224L;

	protected String identifier;
	protected String name;
	
	
	
	protected Map<String,Property> properties;
	protected IReaction parentReaction;
	
	public Reagent(String identifier,String name, IReaction r1){
		this(identifier,name,r1,new Property[0]);
	}
	
	public Reagent(String identifier,String name, IReaction parentReaction, Property[] properties){
		this.identifier = identifier;
		this.name = name;
		this.parentReaction = parentReaction;
		
		this.properties=new HashMap<String,Property>();
		for(Property p:properties)
			this.properties.put(p.getName(), p);
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name;
	}

	public void addProperty(Property p){
		properties.put(p.getName(), p);
	}

	public Property getProperty(String propertyName){
		return properties.get(propertyName);
	}

	public Property setProperty(Property p){
		return properties.put(p.getName(), p);
	}

	public Property deleteProperty(String propertyName){
		return properties.remove(propertyName);
	}

	public Reagent getCopy(){
		Property[] ps = new Property[properties.size()];
		int i=0;
		for(Property p:properties.values())
			ps[i++] = p.getCopy();
		
		return new Reagent(identifier,name,parentReaction,ps);
	}

	public Property[] getProperties() {
		return properties.values().toArray(new Property[properties.size()]);
	}

	public void setProperties(Property[] properties) {
		this.properties = new HashMap<String, Property>();
		for(Property p:properties)
			this.properties.put(p.getName(), p);
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	public IReaction getParentReaction() {
		return parentReaction;
	}

	public void setParentReaction(Reaction parentReaction) {
		this.parentReaction = parentReaction;
	}
	
	public String toString(){
		String tmp = properties.toString();

		return "{"+identifier+"}:"+name+"["+tmp.substring(1,tmp.length()-1)+"]";
	}

}
