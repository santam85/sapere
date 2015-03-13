package sapere.model.communication;

import java.io.Serializable;

import sapere.controller.stub.SapereOperation;

public class Response extends Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2362326240457177409L;
	private boolean success;
	private SapereOperation op;
	
	public Response(boolean success, SapereOperation op){
		this.success = success;
		this.op = op;
	}
	public SapereOperation getOperation(){
		return op;
	}
	@Override
	public Message getCopy() {
		return new Response(success, op);
	}
	@Override
	public String toString(){
		return success+" - "+op;
	}
}
