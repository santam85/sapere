package sapere.model;

import java.io.Serializable;

import sapere.model.reaction.Property;

public abstract class Content implements Serializable{

	private static final long serialVersionUID = -7722544337704990342L;
	
	public abstract String getName();

	public abstract void setName(String name);
	
	public abstract void addProperty(Property p);
	
	public abstract boolean hasProperty(String propertyName);
	
	public abstract Property getProperty(String propertyName);
	
	public abstract void setProperty(Property p);
	
	public abstract void deleteProperty(String propertyName);
	
	public abstract Property[] getProperties();
	
	public abstract void setProperties(Property[] properties);
	
	public abstract Content getCopy();
}
