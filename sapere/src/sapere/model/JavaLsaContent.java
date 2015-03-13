package sapere.model;

import java.util.HashMap;
import java.util.Map;

import sapere.model.reaction.Property;

public class JavaLsaContent extends Content {

	private static final long serialVersionUID = -7967884215688322695L;
	protected String name;
	protected Map<String,Property> properties;
	
	public JavaLsaContent(Content content){
		this(content.getName(),content.getProperties());
	}
	
	public JavaLsaContent(String name){
		this(name,new Property[0]);
	}

	public JavaLsaContent(String name, Property[] properties){
		this.name = name;
		
		this.properties = new HashMap<String, Property>();
		for(Property p:properties)
			this.properties.put(p.getName(), p);
	}
	
	@Override
	public String getName(){
		return name;
	}

	@Override
	public void setName(String name){
		this.name=name;
	}
	
	@Override
	public void addProperty(Property p){
		properties.put(p.getName(), p);
	}
	
	@Override
	public boolean hasProperty(String propertyName) {
		return properties.containsKey(propertyName);
	}
	
	@Override
	public Property getProperty(String propertyName){
		return properties.get(propertyName);
	}
	
	@Override
	public void setProperty(Property p){
		properties.put(p.getName(), p);
	}
	
	@Override
	public void deleteProperty(String propertyName){
		properties.remove(propertyName);
	}
	
	@Override
	public JavaLsaContent getCopy(){
		Property[] ps = new Property[properties.size()];
		int i=0;
		for(Property p:properties.values())
			ps[i++] = p.getCopy();
		
		return new JavaLsaContent(this.name,ps);
	}

	@Override
	public Property[] getProperties() {
		return properties.values().toArray(new Property[properties.size()]);
	}

	@Override
	public void setProperties(Property[] properties) {
		this.properties = new HashMap<String, Property>();
		for(Property p:properties)
			this.properties.put(p.getName(), p);
	}
	
	@Override
	public String toString(){
		return name+"("+properties.toString().replaceAll("\\{|\\}", "")+")";
	}

}
