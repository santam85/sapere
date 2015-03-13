package sapere.model;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

	private ArrayList<SpaceOperation> ops;

	public Transaction() {
		ops = new ArrayList<SpaceOperation>();
	}

	public Transaction(List<SpaceOperation> ops) {
		this.ops = new ArrayList<SpaceOperation>(ops);
	}

	public List<SpaceOperation> getOperations() {
		return ops;
	}

	@Override
	public String toString() {
		return ops.toString();
	}
}
