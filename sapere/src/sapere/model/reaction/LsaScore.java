package sapere.model.reaction;

import sapere.model.Lsa;

public class LsaScore implements Comparable<LsaScore> {
	private Lsa lsa;
	private double score;

	public LsaScore(Lsa lsa, double score) {
		this.lsa = lsa;
		this.score = score;
	}

	public Lsa getLsa() {
		return lsa;
	}

	public void setLsa(Lsa lsa) {
		this.lsa = lsa;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public int compareTo(LsaScore arg0) {
		return new Double(score).compareTo(arg0.getScore());
	}

}
