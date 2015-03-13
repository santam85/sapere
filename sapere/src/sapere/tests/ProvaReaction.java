package sapere.tests;

import sapere.model.LogicLsaContent;
import sapere.model.Lsa;
import sapere.model.reaction.ChannelPropertyValue;
import sapere.model.reaction.ChannelPropertyValue.ChannelType;
import sapere.model.reaction.MarkovianRate;
import sapere.model.reaction.MatchPropertyValue;
import sapere.model.reaction.MultiplicativeScoreFunction;
import sapere.model.reaction.Product;
import sapere.model.reaction.Property;
import sapere.model.reaction.PropertyModifier;
import sapere.model.reaction.PropertyModifierType;
import sapere.model.reaction.PropertyValue;
import sapere.model.reaction.Reaction;
import sapere.model.reaction.Reagent;
import sapere.model.reaction.LsaPropertyValue;
import sapere.model.reaction.SinglePropertyValue;
import sapere.model.reaction.VarPropertyValue;

public class ProvaReaction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*LogicContentFilter cf = new LogicContentFilter("test(X)");
		System.out.print(""+cf.filters(new LogicLsaContent("test(test)")));
		List<String> l1 = new LinkedList<String>();
		l1.add("a"); l1.add("b");
		Property p1 = new Property("field1", l1);
		System.out.println(p1.toString());
		*/
		
		/*--------------------------------------------------
		{d}:Display[preferences = {P}, showing = no] + 
		{s}:Service[content = {c: c matches P}] 
		--> 
		{d}[showing = {s}, instream = #chan(0).in] + 
		{s}[target = {d}, outstream = #chan(0).out]
		----------------------------------------------------*/
		Reaction r1 = new Reaction(new MarkovianRate(0.8),new MultiplicativeScoreFunction());
			Reagent re1 = new Reagent("d","Display",r1);
				Property p1 = new Property("preferences",re1);
				PropertyValue p1v = new VarPropertyValue("P",p1);
				p1.setValue(p1v);
				
				Property p2 = new Property("showing",re1);
				PropertyValue p2v = new SinglePropertyValue("no");
				p2.setValue(p2v);
			re1.addProperty(p1);
			re1.addProperty(p2);
			
			Reagent re2 = new Reagent("s","Service",r1);
				Property p3 = new Property("content",re2);
				PropertyValue p3v = new MatchPropertyValue("c",new VarPropertyValue("P",p3),"matches");
				p3.setValue(p3v);
			re2.addProperty(p3);
			
			Product pr1 = new Product("d",r1);
				PropertyModifier p4 = new PropertyModifier(PropertyModifierType.UpdateProperty, "showing", new LsaPropertyValue("s"),pr1);
				PropertyModifier p5 = new PropertyModifier(PropertyModifierType.AddProperty, "instream", new ChannelPropertyValue("0",null,ChannelType.in),pr1);
			pr1.addPropertyMod(p4);
			pr1.addPropertyMod(p5);
		r1.addReagent(re1);
		r1.addReagent(re2);
		r1.addProduct(pr1);
		
		System.out.println(r1.toString());
		
		/*----------------------------------
		 ecolaw(io,[na(conc(NA)),cl(conc(CL)),nap(conc(NAP)),clm(conc(CLM))],
			rate(markovian,0.1*NA*CL),\n"+
			[na(conc(NA2)),cl(conc(CL2)),nap(conc(NAP2)),clm(conc(CLM2))],
			(NA2 is NA-1,CL2 is CL-1, NAP2 is NAP+1, CLM2 is CLM+1, NA2>=0, CL2>=0, NAP2 >=0, CLM2 >=0)).
		ecolaw(deio,[na(conc(NA2)),cl(conc(CL2)),nap(conc(NAP2)),clm(conc(CLM2))],
			rate(markovian,0.9*NA2*CL2),
			[na(conc(NA)),cl(conc(CL)),nap(conc(NAP)),clm(conc(CLM))],
			(NA is NA2+1,CL is CL2+1, NAP is NAP2-1, CLM is CLM2-1, NA2>=0, CL2>=0, NAP2 >=0, CLM2 >=0)).
		------------------------------------ */
		Reaction r2 = new Reaction(new MarkovianRate(0.1),new MultiplicativeScoreFunction());
			Reagent re21 = new Reagent("na","na",r2);
			Property p21 = new Property("conc",re21);
			PropertyValue p21v = new VarPropertyValue("NA",p21);
			p21.setValue(p21v);
			re21.addProperty(p21);
			
			Reagent re22 = new Reagent("cl","cl",r2);
			Property p22 = new Property("conc",re22);
			PropertyValue p22v = new VarPropertyValue("CL",p22);
			p22.setValue(p22v);
			re22.addProperty(p22);
			
			Reagent re23 = new Reagent("nap","nap",r2);
			Property p23 = new Property("conc",re23);
			PropertyValue p23v = new VarPropertyValue("NAP",p23);
			p23.setValue(p23v);
			re23.addProperty(p23);
			
			Reagent re24 = new Reagent("clm","clm",r2);
			Property p24 = new Property("conc",re24);
			PropertyValue p24v = new VarPropertyValue("CLM",p24);
			p24.setValue(p24v);
			re24.addProperty(p24);

		r2.addReagent(re21);
		r2.addReagent(re22);
		r2.addReagent(re23);
		r2.addReagent(re24);
		
		Product pr21 = new Product("na",r2);
		PropertyModifier pm21 = new PropertyModifier(PropertyModifierType.UpdateProperty, "conc",null, pr21){
			public PropertyValue modify(PropertyValue v){
				VarPropertyValue v1 = new VarPropertyValue("NA", parentReaction);
				int na = Integer.parseInt(((SinglePropertyValue)v1.getValue()).getValue())-1;
				return new SinglePropertyValue(""+na);
			}
		};
		pr21.addPropertyMod(pm21);
		
		Product pr22 = new Product("cl",r2);
		PropertyModifier pm22 = new PropertyModifier(PropertyModifierType.UpdateProperty, "conc",null, pr22){
			public PropertyValue modify(PropertyValue v){
				VarPropertyValue v1 = new VarPropertyValue("CL", parentReaction);
				int cl = Integer.parseInt(((SinglePropertyValue)v1.getValue()).getValue())-1;
				return new SinglePropertyValue(""+cl);
			}
		};
		pr22.addPropertyMod(pm22);
		
		Product pr23 = new Product("nap",r2);
		PropertyModifier pm23 = new PropertyModifier(PropertyModifierType.UpdateProperty, "conc",null, pr23){
			public PropertyValue modify(PropertyValue v){
				VarPropertyValue v1 = new VarPropertyValue("NAP", parentReaction);
				int nap = Integer.parseInt(((SinglePropertyValue)v1.getValue()).getValue())+1;
				return new SinglePropertyValue(""+nap);
			}
		};
		pr23.addPropertyMod(pm23);
		
		Product pr24 = new Product("clm",r2);
		PropertyModifier pm24 = new PropertyModifier(PropertyModifierType.UpdateProperty, "conc",null, pr24){
			public PropertyValue modify(PropertyValue v){
				VarPropertyValue v1 = new VarPropertyValue("CLM", parentReaction);
				int clm = Integer.parseInt(((SinglePropertyValue)v1.getValue()).getValue())+1;
				return new SinglePropertyValue(""+clm);
			}
		};
		pr24.addPropertyMod(pm24);
		
		r2.addProduct(pr21);
		r2.addProduct(pr22);
		r2.addProduct(pr23);
		r2.addProduct(pr24);
		
		r2.setCandidateLsa(re21.getIdentifier(),new Lsa("0", new LogicLsaContent("na(conc(20))")));
		r2.setCandidateLsa(re22.getIdentifier(),new Lsa("1", new LogicLsaContent("cl(conc(20))")));
		r2.setCandidateLsa(re23.getIdentifier(),new Lsa("2", new LogicLsaContent("nap(conc(20))")));
		r2.setCandidateLsa(re24.getIdentifier(),new Lsa("3", new LogicLsaContent("clm(conc(20))")));
		r2.setCandidateLsaScore(re21.getIdentifier(),1.0);
		r2.setCandidateLsaScore(re22.getIdentifier(),1.0);
		r2.setCandidateLsaScore(re23.getIdentifier(),1.0);
		r2.setCandidateLsaScore(re24.getIdentifier(),1.0);
		
		Lsa lsa = new Lsa("0", new LogicLsaContent("na(conc(20))"));
		Lsa lsaout = pr21.modify(lsa);
		
		System.out.println(""+lsa+"\n"+lsaout);
		
		//System.out.println(r2.toString());	
	}

}
