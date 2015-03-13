package sapere.model.reaction;

import sapere.model.IReaction;


public class VarPropertyValue extends PropertyValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1189522282504808401L;
	private String varName;
	private IReaction parentReaction;
	
	public VarPropertyValue(String varName, Property parentProperty){
		this.varName = varName;
		this.parentProperty = parentProperty;
		this.parentReaction = parentProperty.getParentReagent().getParentReaction();
	}
	
	public VarPropertyValue(String varName, IReaction parentReaction){
		this.varName = varName;
		this.parentReaction = parentReaction;
	}
	
	public PropertyValue getValue() {
		return parentReaction.getVars().get(varName);
	}

	public void setValue(PropertyValue value) {
		parentReaction.getVars().put(varName,value);
	}
	
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}
	
	public boolean isBinded(){
		return parentReaction.getVars().containsKey(varName);
	}
	
	@Override
	public String toString(){
		if(isBinded())
			return getValue().toString();
		else
			return "{"+varName+"}";
	}
	
	@Override
	protected void setParentProperty(Property parentProperty){
		super.setParentProperty(parentProperty);
		this.parentReaction = parentProperty.getParentReagent().getParentReaction();
	}

	@Override
	public PropertyValue getCopy() {
		return new VarPropertyValue(this.varName,this.parentReaction);
	}

	@Override
	public boolean equals(Object p) {
		//CONTROLLO TRA VAR E VALORI UGUALI SI PUO' METTERE ANCHE QUI.
		return (p instanceof VarPropertyValue) && ((VarPropertyValue)p).getVarName().equals(varName);
	}
	
}
