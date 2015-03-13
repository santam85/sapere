package sapere.model.reaction;

import java.io.Serializable;

public abstract class PropertyValue implements Serializable,Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8889321223322000280L;
	protected Property parentProperty;

	protected Property getParentProperty() {
		return parentProperty;
	}

	protected void setParentProperty(Property parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	public abstract PropertyValue getCopy();
	
	public abstract boolean equals(Object p);

}
