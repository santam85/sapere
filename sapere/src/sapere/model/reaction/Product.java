package sapere.model.reaction;

import java.util.HashMap;

import sapere.controller.space.Space;
import sapere.model.Content;
import sapere.model.Lsa;
import sapere.model.LsaFactory;
import sapere.model.SpaceOperation;
import sapere.model.SpaceOperationType;
import sapere.model.reaction.PropertyModifier;

public class Product {
	private ProductType type;
	private String identifier;
	private String name;
	private String copyToIdentifier;
	private String diffusionDestination;
	private HashMap<String,Property> properties;
	private HashMap<String,PropertyModifier> propMods;
	
	private Reaction parentReaction;
	
	public Product(String identifier, String copyToIdentifier,Reaction parentReaction){
		this(ProductType.Copy,identifier,null,copyToIdentifier,null,parentReaction);
	}
	
	public Product(String identifier ,Reaction parentReaction){
		this(ProductType.Update,identifier,null,null,null,parentReaction);
	}
	
	public Product(ProductType type, String identifier, String name, String copyToIdentifier, String diffusionDestination, Reaction parentReaction){
		this.type = type;
		this.name = name;
		this.identifier = identifier;
		this.parentReaction = parentReaction;
		this.diffusionDestination = diffusionDestination;
		
		properties = new HashMap<String, Property>();
		propMods = new HashMap<String, PropertyModifier>();
	}
	
	public void addProperty(Property p){
		properties.put(p.getName(), p);
	}
	
	public Property getProperty(String propertyName){
		return properties.get(propertyName);
	}
	
	public void addPropertyMod(PropertyModifier p){
		propMods.put(p.getModifiedPropertyName(), p);
	}
	
	public PropertyModifier getPropertyMod(String propertyName){
		return propMods.get(propertyName);
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getCopyToIdentifier() {
		return copyToIdentifier;
	}

	public ProductType getType() {
		return type;
	}
	
	public String getDiffusionDestination(){
		return diffusionDestination;
	}
	
	public void setDiffusionDestination(String diffusionDestination){
		this.diffusionDestination = diffusionDestination;
	}
	
	public String toString(){
		String tmp = "";
		for(Property p:properties.values()){
			tmp += p.getName()+"="+p.toString()+", ";
		}
		for(PropertyModifier m:propMods.values()){
			tmp += m.toString()+", ";
		}
		tmp=tmp.substring(0, (tmp.length()-2>0)?tmp.length()-2:0);
		switch(this.type){
			case Remove: return "-{"+identifier+"}";
			case Diffuse: return "{"+identifier+"->"+this.diffusionDestination+"}";
			default: return "{"+identifier+"}"+((name == null)?"":name)+(tmp.equals("")?tmp:"["+tmp+"]");
		
		}
	}	

	public SpaceOperation getOperation() {
		//Returns the concrete operation to be executed
		//IF UPDATE, MODIFY SOURCE LSA WITH modify(), THEN UPDATE SPACE CONTENT
		String id;
		Content c;
		Lsa lsa;
		switch(this.type){
			case Remove:
				return new SpaceOperation(SpaceOperationType.Remove,parentReaction.getCandidateLsa(identifier).getId(),null,null);
			case New:
				id = Space.getInstance().getFreshId();
				parentReaction.setMapping(this.identifier, id);
				lsa = LsaFactory.getInstance().createLsa(id,this.name,this.properties.values().toArray(new Property[0]));
				parentReaction.setCandidateLsa(identifier, lsa.getCopy());
				parentReaction.setCandidateLsaScore(identifier, 1);
				return new SpaceOperation(SpaceOperationType.Inject,null,ground(lsa),null);
			case Copy:
				id = Space.getInstance().getFreshId();
				parentReaction.setMapping(this.copyToIdentifier, id);
				lsa = new Lsa(id,parentReaction.getCandidateLsa(identifier).getContent().getCopy());
				return new SpaceOperation(SpaceOperationType.Inject,null,modify(lsa),null);
			case Update:
				c = modify(parentReaction.getCandidateLsa(identifier).getCopy()).getContent();
				parentReaction.getCandidateLsa(identifier).setContent(c);
				return new SpaceOperation(SpaceOperationType.Update,parentReaction.getCandidateLsa(identifier).getId(),null,c);
			case Diffuse:
				return new SpaceOperation(SpaceOperationType.Diffuse,parentReaction.getCandidateLsa(identifier).getId(),null,null,diffusionDestination);
			default://LeaveUntouched
				return null;
		}
	}
	
	public Lsa modify(Lsa lsa){
		//FOREACH PROPERTYMODIFIER 
		for(PropertyModifier pm:propMods.values()){
			PropertyValue pv;
			//MODIFY REAGENT PROPERTY
			switch(pm.getType()){
				case AddProperty:
					lsa.getContent().addProperty(new Property(pm.getModifiedPropertyName(),pm.getModification(),null));
					break;
				case RemoveProperty:
					lsa.getContent().deleteProperty(pm.getModifiedPropertyName());
					break;
				case UpdateProperty:
					lsa.getContent().setProperty(new Property(pm.getModifiedPropertyName(),pm.getModification(),null));
					break;
				case AddValue:
					if(!lsa.getContent().hasProperty(pm.getModifiedPropertyName()))
						lsa.getContent().addProperty(new Property(pm.getModifiedPropertyName(),new SetPropertyValue()));
					
					pv = lsa.getContent().getProperty(pm.getModifiedPropertyName()).getValue();
					
					if(pv instanceof SinglePropertyValue){
						SetPropertyValue spv = new SetPropertyValue();
						spv.addValue(((SinglePropertyValue) pv).getValue());
						spv.addValue(((SinglePropertyValue) pm.getModification()).getValue());
						lsa.getContent().setProperty(new Property(pm.getModifiedPropertyName(),spv,null));
					}else if(pv instanceof SetPropertyValue){
						((SetPropertyValue)pv).addValue(pm.getModification());
					}
					break;
				case RemoveValue:
					pv = lsa.getContent().getProperty(pm.getModifiedPropertyName()).getValue();
					if(pv instanceof SinglePropertyValue){
						SetPropertyValue spv = new SetPropertyValue();
						spv.addValue(((SinglePropertyValue) pv).getValue());
						spv.removeValue(((SinglePropertyValue) pm.getModification()).getValue());
						lsa.getContent().setProperty(new Property(pm.getModifiedPropertyName(),spv,null));
					}else if(pv instanceof SetPropertyValue){
						((SetPropertyValue)pv).removeValue(((SinglePropertyValue) pm.getModification()).getValue());
					}
					break;
				case AddValues:
					pv = lsa.getContent().getProperty(pm.getModifiedPropertyName()).getValue();
					if(pv instanceof SinglePropertyValue){
						SetPropertyValue spv = new SetPropertyValue();
						spv.addValue(((SinglePropertyValue) pv).getValue());
						spv.addValues(((SetPropertyValue) pm.getModification()).getValues());
						lsa.getContent().setProperty(new Property(pm.getModifiedPropertyName(),spv,null));
					}else if(pv instanceof SetPropertyValue){
						((SetPropertyValue)pv).addValues(((SetPropertyValue) pm.getModification()).getValues());
					}
					break;
				case RemoveValues:
					pv = lsa.getContent().getProperty(pm.getModifiedPropertyName()).getValue();
					if(pv instanceof SinglePropertyValue){
						SetPropertyValue spv = new SetPropertyValue();
						spv.addValue(((SinglePropertyValue) pv).getValue());
						spv.removeValues(((SetPropertyValue) pm.getModification()).getValues());
						lsa.getContent().setProperty(new Property(pm.getModifiedPropertyName(),spv,null));
					}else if(pv instanceof SetPropertyValue){
						((SetPropertyValue)pv).removeValues(((SetPropertyValue) pm.getModification()).getValues());
					}
					break;
			}
		}
		return ground(lsa);
	}
	
	private Lsa ground(Lsa lsa){
		for(Property p:lsa.getContent().getProperties()){
			p.setValue(ground(p.getValue()));
		}
		return lsa;
	}

	private PropertyValue ground(PropertyValue value) {
		PropertyValue newval = value;
		if(value instanceof VarPropertyValue)
			newval = ((VarPropertyValue)value).getValue();
		if(value instanceof LsaPropertyValue)
			newval = new SinglePropertyValue(parentReaction.getMapping(((LsaPropertyValue)value).getReagentIdentifier()));
		if(value instanceof SetPropertyValue){
			PropertyValue[] vs = ((SetPropertyValue)value).getValues();
			SetPropertyValue spv = new SetPropertyValue();
			for(PropertyValue pv:vs){
				PropertyValue pv1 = ground(pv);
				if(!spv.containsValue(pv1))
					spv.addValue(pv1);
			}
			newval=spv;
		}
		return newval;
	}

	public Reaction getParentReaction() {
		return parentReaction;
	}
	
	public void setParentReaction(Reaction parentReaction) {
		this.parentReaction = parentReaction;
	}
	

}
