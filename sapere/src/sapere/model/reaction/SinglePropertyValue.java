package sapere.model.reaction;

public class SinglePropertyValue extends PropertyValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6945224732359799259L;
	private String value;
	
	public SinglePropertyValue(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		return value;
	}

	@Override
	public PropertyValue getCopy() {
		return new SinglePropertyValue(this.value);
	}

	@Override
	public boolean equals(Object p) {
		return (p instanceof SinglePropertyValue) && ((SinglePropertyValue)p).getValue().equals(value);
	}
	
}
