package sapere.model;

import java.io.Serializable;

public class Lsa implements Serializable{
	
	private static final long serialVersionUID = -2289686273126069066L;
	protected String id;
	protected Content content;
	
	public Lsa(Content content){
		this(null,content);
	}
	
	public Lsa(String id, Content content){
		this.id = id;
		this.content = content;
	}

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}

	public Content getContent(){
		return content;
	}

	public void setContent(Content content){
		this.content = content;
	}
	
	public String toString(){
		return "<"+id+","+content.toString()+">";
	}

	public Lsa getCopy() {
		return new Lsa(this.id,this.content.getCopy());
	}
	
}
