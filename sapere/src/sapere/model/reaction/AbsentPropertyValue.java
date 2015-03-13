package sapere.model.reaction;

public class AbsentPropertyValue extends PropertyValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5938347510545140594L;
	
	public AbsentPropertyValue(){
	}
	
	public String toString(){
		return "!";
	}

	@Override
	public PropertyValue getCopy() {
		return new AbsentPropertyValue();
	}

	@Override
	public boolean equals(Object p) {
		return false ;
	}
}
