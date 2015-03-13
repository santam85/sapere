package sapere.model.reaction;

public class NotPropertyValue extends PropertyValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6945224732359799259L;
	private PropertyValue value;
	
	public NotPropertyValue(PropertyValue value){
		this.value = value;
	}
	
	public PropertyValue getValue() {
		return value;
	}

	public void setValue(PropertyValue value) {
		this.value = value;
	}
	
	public String toString(){
		return "!("+value.toString()+")";
	}

	@Override
	public PropertyValue getCopy() {
		return new NotPropertyValue(this.value);
	}

	@Override
	public boolean equals(Object p) {
		return (p instanceof NotPropertyValue) && ((NotPropertyValue)p).getValue().equals(value);
	}
	
}
