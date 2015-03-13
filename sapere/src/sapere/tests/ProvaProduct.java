package sapere.tests;

import sapere.model.Lsa;
import sapere.model.IReaction;
import sapere.model.LogicLsaContent;
import sapere.model.reaction.MarkovianRate;
import sapere.model.reaction.MultiplicativeScoreFunction;
import sapere.model.reaction.Product;
import sapere.model.reaction.ProductType;
import sapere.model.reaction.PropertyModifier;
import sapere.model.reaction.PropertyModifierType;
import sapere.model.reaction.Reaction;
import sapere.model.reaction.SinglePropertyValue;

public class ProvaProduct {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IReaction r1 = new Reaction(new MarkovianRate(0.8),new MultiplicativeScoreFunction());
		Product pr1 = new Product(ProductType.Update,"d","","","",null);
		r1.addProduct(pr1);
		
		PropertyModifier p4 = new PropertyModifier(PropertyModifierType.UpdateProperty, "showing", new SinglePropertyValue("si"), pr1);
		PropertyModifier p5 = new PropertyModifier(PropertyModifierType.AddProperty, "instream", new SinglePropertyValue("channel1"), pr1);
		pr1.addPropertyMod(p4);
		pr1.addPropertyMod(p5);
		
		Lsa lsa = new Lsa("0", new LogicLsaContent("display(showing(no))"));
		Lsa lsaout = pr1.modify(lsa.getCopy());
		
		System.out.println(""+lsa+"\n"+lsaout);
	}
}
