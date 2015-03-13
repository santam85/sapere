package sapere.controller.reactionmanager;

import sapere.model.Lsa;
import sapere.model.reaction.AbsentPropertyValue;
import sapere.model.reaction.MatchPropertyValue;
import sapere.model.reaction.Property;
import sapere.model.reaction.PropertyValue;
import sapere.model.reaction.Reagent;
import sapere.model.reaction.SinglePropertyValue;
import sapere.model.reaction.LsaPropertyValue;
import sapere.model.reaction.SetPropertyValue;
import sapere.model.reaction.NotPropertyValue;
import sapere.model.reaction.VarPropertyValue;

public class SimpleReagentsMatcher extends ReagentsMatcher {

	@Override
	protected double match(Reagent re, Lsa lsa) {
		return matchName(re,lsa)*matchProperties(re,lsa);
	}

	protected double matchName(Reagent re, Lsa lsa) {
		if(lsa.getContent().getName().equals(re.getName()))
			return 1.0;
		else
			return 0.0;
	}
	
	protected double matchProperties(Reagent re, Lsa lsa) {
		double tmp = 0;
		Property[] reap = re.getProperties();
		
		if(reap.length==0)
			return 1;
		
		for(Property p:re.getProperties()){
			if(p.getValue() instanceof VarPropertyValue){
				if(!lsa.getContent().hasProperty(p.getName()))
					return 0;
				
				VarPropertyValue v = (VarPropertyValue)p.getValue();
				if(v.isBinded()){
					PropertyValue varValue = v.getValue();
					if(varValue.equals(lsa.getContent().getProperty(p.getName()).getValue()))
						 tmp+=1;
				}
				else
					tmp+=1;
			}
			else if(p.getValue() instanceof LsaPropertyValue){
				String id = re.getParentReaction().getMapping(((LsaPropertyValue)p.getValue()).getReagentIdentifier());
				if(id != null){
					SinglePropertyValue npv = new SinglePropertyValue(id);
					Property lsap = lsa.getContent().getProperty(p.getName());
					if(lsap!=null){
						if(lsap.getValue() instanceof SinglePropertyValue){
							SinglePropertyValue lsapv = (SinglePropertyValue)lsap.getValue();
							if(lsapv.equals(npv))
								tmp+=1;
							else
								return 0;
						}
						if(lsap.getValue() instanceof SetPropertyValue){
							PropertyValue[] lsapvs =((SetPropertyValue)lsap.getValue()).getValues();
							for(PropertyValue pv:lsapvs)
								if(pv.equals(npv))
									tmp+=1;
							
							return 0;
						}
					}
				}
			}
			else if(p.getValue() instanceof AbsentPropertyValue){
				if(!lsa.getContent().hasProperty(p.getName()))
					tmp+=1;
				else
					return 0;
			}
			else if(p.getValue() instanceof NotPropertyValue){
				if(!lsa.getContent().hasProperty(p.getName()))
					return 1;
				
				PropertyValue v = ((NotPropertyValue)p.getValue()).getValue();
				if(v instanceof MatchPropertyValue){
					MatchPropertyValue mpv = (MatchPropertyValue)v;
					Property sp = lsa.getContent().getProperty(p.getName());
					
					if(sp==null)
						return 1;
					
					PropertyValue lsaProp = sp.getValue();
					PropertyValue rProp = mpv.getMatchTarget();
					
					if(rProp instanceof VarPropertyValue){
						if(!((VarPropertyValue) rProp).isBinded()){
							tmp+=1;
							continue;
						}else{
							rProp=((VarPropertyValue) rProp).getValue();
						}
					}

					double fmr = this.fuzzyMatch(lsaProp,rProp,mpv.getMatchType());
					if(fmr == 0)
						return 1;
					
					tmp+=1-fmr;
				}
				if(v instanceof LsaPropertyValue){
					String id = re.getParentReaction().getMapping(((LsaPropertyValue)v).getReagentIdentifier());
					if(id != null){
						SinglePropertyValue npv = new SinglePropertyValue(id);
						Property lsap = lsa.getContent().getProperty(p.getName());
						if(lsap!=null){
							if(lsap.getValue() instanceof SinglePropertyValue){
								SinglePropertyValue lsapv = (SinglePropertyValue)lsap.getValue();
								if(lsapv.equals(npv))
									return 0;
								else
									tmp+=1;
							}
							if(lsap.getValue() instanceof SetPropertyValue){
								PropertyValue[] lsapvs =((SetPropertyValue)lsap.getValue()).getValues();
								for(PropertyValue pv:lsapvs)
									if(pv.equals(npv))
										return 0;
								
								tmp+=1;
							}
						}
					}
					else
						tmp+=1;
				}
				if(v instanceof SinglePropertyValue){
					SinglePropertyValue npv = (SinglePropertyValue)v;
					Property lsap = lsa.getContent().getProperty(p.getName());
					if(lsap!=null){
						if(lsap.getValue() instanceof SinglePropertyValue){
							SinglePropertyValue lsapv = (SinglePropertyValue)lsap.getValue();
							if(lsapv.equals(npv))
								return 0;
							else
								tmp+=1;
						}
						if(lsap.getValue() instanceof SetPropertyValue){
							PropertyValue[] lsapvs =((SetPropertyValue)lsap.getValue()).getValues();
							for(PropertyValue pv:lsapvs)
								if(pv.equals(npv))
									return 0;
								else
									tmp+=1;
						}
					}
				}
			}
			else if(p.getValue() instanceof MatchPropertyValue){
				MatchPropertyValue mpv = (MatchPropertyValue)p.getValue();
				Property sp = lsa.getContent().getProperty(p.getName());
				
				if(sp==null)
					return 0;
				
				PropertyValue lsaProp = sp.getValue();
				PropertyValue rProp = mpv.getMatchTarget();
				
				if(rProp instanceof VarPropertyValue){
					if(!((VarPropertyValue) rProp).isBinded()){
						tmp+=1;
						continue;
					}else{
						rProp=((VarPropertyValue) rProp).getValue();
					}
				}

				double fmr = this.fuzzyMatch(lsaProp,rProp,mpv.getMatchType());
				if(fmr == 0)
					return 0;
				
				tmp+=fmr;
				
			}
			else if(p.getValue() instanceof SinglePropertyValue)
			{
				Property lsap = lsa.getContent().getProperty(p.getName());
				if(lsap==null )
					return 0;
				
				if(p.getValue().equals(lsap.getValue()))
					tmp+=1;
				else return 0;
			}
		}
		
		return tmp/reap.length;
	}

	@Override
	public double fuzzyMatch(PropertyValue lsaProp, PropertyValue rProp, String op) {
		if(op.equals("matches")){
			if(rProp instanceof VarPropertyValue){
				VarPropertyValue vpv = (VarPropertyValue)rProp;
				
				if(!vpv.isBinded())
					return 1.0;
				
				PropertyValue pv = vpv.getValue();
				return fuzzyMatch(lsaProp,pv,op);
			}
			if(rProp instanceof SetPropertyValue){
				SetPropertyValue vpv = (SetPropertyValue)rProp;
				
				if(vpv.containsValue(lsaProp))
					return 1;
			}
			if(lsaProp.equals(rProp))
				return 1.0;
		}
		
		if(op.equals(">")){
			if((lsaProp instanceof SinglePropertyValue) && (rProp instanceof SinglePropertyValue))
				try{
					double d1 = Double.parseDouble(((SinglePropertyValue)lsaProp).getValue());
					double d2 = Double.parseDouble(((SinglePropertyValue)rProp).getValue());
					if(d1>d2)
						return 1.0;
				}catch(Exception x){}
				
				return 0.0;
		}
		if(op.equals(">=")){
			if((lsaProp instanceof SinglePropertyValue) && (rProp instanceof SinglePropertyValue))
				try{
					double d1 = Double.parseDouble(((SinglePropertyValue)lsaProp).getValue());
					double d2 = Double.parseDouble(((SinglePropertyValue)rProp).getValue());
					if(d1>=d2)
						return 1.0;
				}catch(Exception x){}
				
				return 0.0;
		}
		if(op.equals("<")){
			if((lsaProp instanceof SinglePropertyValue) && (rProp instanceof SinglePropertyValue))
				try{
					double d1 = Double.parseDouble(((SinglePropertyValue)lsaProp).getValue());
					double d2 = Double.parseDouble(((SinglePropertyValue)rProp).getValue());
					if(d1<d2)
						return 1.0;
				}catch(Exception x){}
				
				return 0.0;
		}
		if(op.equals("=")){
			if((lsaProp instanceof SinglePropertyValue) && (rProp instanceof SinglePropertyValue))
				try{
					double d1 = Double.parseDouble(((SinglePropertyValue)lsaProp).getValue());
					double d2 = Double.parseDouble(((SinglePropertyValue)rProp).getValue());
					if(d1==d2)
						return 1.0;
				}catch(Exception x){}
				
				return 0.0;
		}
		return 0.0;
	}


}
