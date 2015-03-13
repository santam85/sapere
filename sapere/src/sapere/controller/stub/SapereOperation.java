package sapere.controller.stub;

import java.io.Serializable;

import sapere.model.Content;
import sapere.model.IContentFilter;
import sapere.model.Lsa;

public class SapereOperation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4208008724529365093L;

	private Lsa lsa;
	private String lsaId;
	private SapereOperationType type;
	private Content content;
	private IContentFilter filter;
	
	public SapereOperation(SapereOperationType type, String lsaId, Lsa subject, Content newCon, IContentFilter filter){
		this.type = type;
		this.lsaId = lsaId;
		this.lsa = subject;
		this.content = newCon;
		this.filter = filter;
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
		this.lsaId = id; 
	}

	public SapereOperationType getType() {
		return type;
	}
	
	public void setType(SapereOperationType type){
		this.type = type; 
	}
	
	public Content getNewContent() {
		return content;
	}
	
	public void setNewContent(Content con){
		this.content = con; 
	}
	
	public IContentFilter getFilter(){
		return filter;
	}
	
	public void setFilter(IContentFilter filter){
		this.filter = filter;
	}
	@Override
	public String toString(){
		String res = "operation[type="+type;
		if(lsaId != null)
			res+=",lsaId="+lsaId;
		if(lsa != null)
			res+=",lsa="+lsa;
		if (content != null)
			res+=",newContent="+content;
		if (filter != null)
			res+=",filter="+filter;
		return res+="]";
	}
}
