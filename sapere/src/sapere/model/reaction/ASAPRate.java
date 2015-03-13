package sapere.model.reaction;

public class ASAPRate extends Rate {
	
	@Override
	public long getNextOccurrence(double score, long currentTimeMillis) {
		return currentTimeMillis;
	}
	
	public String toString(){
		return "now";
	}
}