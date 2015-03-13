package sapere.model.reaction;

public class LsaPropertyValue extends PropertyValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5938347510545140594L;
	private String reagentIdentifier;
	
	public LsaPropertyValue(String reagentIdentifier){
		this.reagentIdentifier = reagentIdentifier;
	}
	
	public String getReagentIdentifier() {
		return reagentIdentifier;
	}

	public void setReagentIdentifier(String reagentIdentifier) {
		this.reagentIdentifier = reagentIdentifier;
	}
	
	public String toString(){
		return "{"+reagentIdentifier+"}";
	}

	@Override
	public PropertyValue getCopy() {
		return new LsaPropertyValue(reagentIdentifier);
	}

	@Override
	public boolean equals(Object p) {
		return (p instanceof LsaPropertyValue) && ((LsaPropertyValue)p).getReagentIdentifier().equals(this.reagentIdentifier) ;
	}
}
