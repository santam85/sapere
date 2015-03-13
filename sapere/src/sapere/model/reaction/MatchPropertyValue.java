package sapere.model.reaction;


public class MatchPropertyValue extends PropertyValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1356827931370033257L;
	private String matchSource;
	private String type;
	private PropertyValue matchTarget;
	
	
	public MatchPropertyValue(String matchSource,PropertyValue matchTarget, String type){
		this.matchSource = matchSource;
		this.matchTarget = matchTarget;
		this.type=type;
	}
	
	public String getMatchSource() {
		return matchSource;
	}

	public void setMatchSource(String matchSource) {
		this.matchSource = matchSource;
	}

	public PropertyValue getMatchTarget() {
		return matchTarget;
	}

	public void setMatchTarget(PropertyValue matchTarget) {
		this.matchTarget = matchTarget;
	}
	
	public String toString(){
		return "{"+matchSource+": "+matchSource+" "+type+" "+matchTarget+"}";	
	}

	@Override
	public PropertyValue getCopy() {
		return this;
	}

	@Override
	public boolean equals(Object p) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getMatchType() {
		return type;
	}
}
