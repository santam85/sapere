package sapere.model;

import java.io.Serializable;

public class SpaceOperation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -199312946939588667L;
	private Lsa lsa;
	private String lsaId;
	private SpaceOperationType type;
	private Content cont;
	private String destIp;

	public SpaceOperation(SpaceOperationType type, String lsaId, Lsa lsa, Content cont){
		this(type,lsaId,lsa,cont,null);
	}
	
	public SpaceOperation(SpaceOperationType type, String lsaId, Lsa lsa, Content cont, String ipDest){
		this.type = type;
		this.lsaId = lsaId;
		this.lsa = lsa;
		this.cont = cont;
		this.destIp = ipDest;
	}

	public Lsa getLsa() {
		return lsa;
	}
	
	public void setLsa(Lsa lsa){
		this.lsa = lsa;
	}

	public String getLsaId() {
		return lsaId;
	}
	
	public void setLsaId(String id){
		lsaId = id;
	}

	public SpaceOperationType getType() {
		return type;
	}
	
	public void setType(SpaceOperationType type){
		this.type = type;
	}

	public Content getNewContent() {
		return cont;
	}
	
	public void setNewContent(Content cont) {
		this.cont = cont;
	}
	
	public String getDestIp() {
		return destIp;
	}
	
	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}
	
	@Override
	public String toString(){
		String res = "operation[type="+type;
		if(lsaId != null)
			res+=",lsaId="+lsaId;
		if(lsa != null)
			res+=",lsa="+lsa;
		if (cont != null)
			res+=",newContent="+cont;
		return res+="]";
	}
}
