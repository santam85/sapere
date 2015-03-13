package sapere.model.reaction;


public abstract class Rate {
	public abstract long getNextOccurrence(double score, long currentTime);
}
