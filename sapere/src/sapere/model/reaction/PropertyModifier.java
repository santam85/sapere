package sapere.model.reaction;

import sapere.model.IReaction;

public class PropertyModifier {
	protected PropertyModifierType type;
	protected PropertyValue modification;
	protected String modifiedPropertyName;
	protected IReaction parentReaction;
	protected Product parentProduct;
	
	public PropertyModifier(PropertyModifierType type, String modifiedPropertyName, PropertyValue modification, Product parentProduct){
		this.type = type;
		this.modification = modification;
		this.parentProduct = parentProduct;
		this.parentReaction = parentProduct.getParentReaction();
		this.modifiedPropertyName = modifiedPropertyName;
	}

	public PropertyValue getModification(){
		return modification;
	}
	
	public void setModification(PropertyValue modification){
		this.modification = modification;
	}

	public PropertyModifierType getType() {
		return type;
	}
	
	public String getModifiedPropertyName(){
		return modifiedPropertyName;
	}
	
	public String toString(){
		String tmp = modifiedPropertyName;
		switch(type){
			case UpdateProperty: tmp+="=";
				break;
			case AddValue: tmp+="+=";
				break;
			case RemoveValue: tmp+="-=";
				break;
			default: tmp+="=";
				break;
		}
		tmp+=modification!=null?modification.toString():"";
		return tmp;
	}
}
