package sapere.model.reaction;

import java.util.Random;

public class MarkovianRate extends Rate {
	private static Random r = new Random();
	
	private double rate;
	
	public MarkovianRate(double rate){
		this.rate = rate;
	}
	
	@Override
	public long getNextOccurrence(double score, long currentTimeMillis) {
		double cts = (currentTimeMillis*1.0)/1000;
		double tmp = cts + Math.log((1.0/r.nextDouble())/(rate*score));
		return Math.round(tmp*1000);
	}
	
	public String toString(){
		return "markovian("+rate+")";
	}
}