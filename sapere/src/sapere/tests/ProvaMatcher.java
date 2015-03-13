package sapere.tests;

import sapere.controller.reactionmanager.SimpleReagentsMatcher;
import sapere.controller.space.Space;
import sapere.controller.space.SpaceException;
import sapere.model.JavaLsaContent;
import sapere.model.Lsa;
import sapere.model.reaction.LsaPropertyValue;
import sapere.model.reaction.MarkovianRate;
import sapere.model.reaction.MeanScoreFunction;
import sapere.model.reaction.Product;
import sapere.model.reaction.ProductType;
import sapere.model.reaction.Property;
import sapere.model.reaction.PropertyModifier;
import sapere.model.reaction.PropertyModifierType;
import sapere.model.reaction.PropertyValue;
import sapere.model.reaction.Reaction;
import sapere.model.reaction.Reagent;
import sapere.model.reaction.SinglePropertyValue;
import sapere.model.reaction.TotalScore;
import sapere.model.reaction.VarPropertyValue;

public class ProvaMatcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SimpleReagentsMatcher m = new SimpleReagentsMatcher();
		
		Reaction r1 = new Reaction(new MarkovianRate(0.8),new MeanScoreFunction());
		Reagent re1 = new Reagent("d","display",r1);
			Property p1 = new Property("size",re1);
			PropertyValue p1v = new SinglePropertyValue("22");
			p1.setValue(p1v);
			Property p2 = new Property("showing",re1);
			PropertyValue p2v = new SinglePropertyValue("no");
			p2.setValue(p2v);
		re1.addProperty(p1);
		re1.addProperty(p2);
		Reagent re2 = new Reagent("s","service",r1);
			Property p3 = new Property("content",re2);
			PropertyValue p3v = new VarPropertyValue("A",r1);
			p3.setValue(p3v);
			Property p4 = new Property("length",re2);
			PropertyValue p4v = new VarPropertyValue("B",r1);
			p4.setValue(p4v);
		re2.addProperty(p3);
		re2.addProperty(p4);
		
		r1.addReagent(re1);
		r1.addReagent(re2);
		
		Product pr1 = new Product(ProductType.Update,"d","","","",r1);
		
			PropertyModifier pm1 = new PropertyModifier(PropertyModifierType.UpdateProperty, "showing", new VarPropertyValue("A",r1), pr1);
			PropertyModifier pm2 = new PropertyModifier(PropertyModifierType.AddProperty, "duration", new VarPropertyValue("B",r1), pr1);
			pr1.addPropertyMod(pm1);
			pr1.addPropertyMod(pm2);
		
		Product pr2 = new Product("d","d'",r1);
			PropertyModifier pm3 = new PropertyModifier(PropertyModifierType.AddProperty, "copyof", new LsaPropertyValue("d"), pr1);
			pr2.addPropertyMod(pm3);
		
		r1.addProduct(pr1);
		r1.addProduct(pr2);
		
		Lsa lsa1 = new Lsa(null, new JavaLsaContent("display", new Property[]{new Property("size",new SinglePropertyValue("22")),new Property("showing",new SinglePropertyValue("no"))}));
		Lsa lsa2 = new Lsa(null, new JavaLsaContent("service", new Property[]{new Property("content",new SinglePropertyValue("basket")),new Property("length",new SinglePropertyValue("123"))}));
		
		try {
			Space.getInstance().inject(lsa1);
			Space.getInstance().inject(lsa2);
		} catch (SpaceException e) {
			e.printStackTrace();
		}
			
		System.out.println("space: "+Space.getInstance());
		
		//new LogicLsaContent("display(size(22),showing(no))"));
		//new LogicLsaContent("service(content(basket),length(123))");
		
		Lsa[] l = new Lsa[]{lsa1,lsa2};
		
		for(TotalScore r:m.score(l, r1, 0.1)){
			System.out.println("score: "+r.score);
			System.out.println("trans: "+r.transaction);
		}
		
		System.out.println("fuzzy: " + m.fuzzyMatch(p1v, p2v, ">"));
	}
}
