package sapere.model.reaction;

import java.io.Serializable;

public class Property implements Serializable,Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9158861225716275243L;
	private String name;
	private PropertyValue value;
	
	private Reagent parentReagent;
	
	public Property(String name) {
		this(name,null,null);
	}
	
	public Property(String name, Reagent parentReagent){
		this(name,null,parentReagent);
	}
	
	public Property(String name, PropertyValue value) {
		this(name,value,null);
	}
	
	public Property(String name, PropertyValue value, Reagent parentReagent) {
		this.name = name;
		this.value = value;
		this.parentReagent = parentReagent;
	}

	public String getName() {
		return name;
	}

	public PropertyValue getValue() {
		return value;
	}

	public void setValue(PropertyValue value) {
		this.value = value;
	}
	
	Reagent getParentReagent() {
		return parentReagent;
	}

	void setParentReagent(Reagent parentReagent) {
		this.parentReagent = parentReagent;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	@Override
	public String toString(){
		try{
			return value.toString();
		}
		catch(Exception ex){
			return "";
		}
	}

	public Property getCopy() {
		return new Property(this.name,this.value.getCopy(),this.parentReagent);
	}

	
	
}
