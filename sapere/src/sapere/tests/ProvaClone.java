package sapere.tests;

import sapere.model.Lsa;
import sapere.model.LsaFactory;
import sapere.model.reaction.Property;
import sapere.model.reaction.SinglePropertyValue;

public class ProvaClone {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Lsa lsa1 = LsaFactory.getInstance().createLsaFromProlog("0","name(prop(10))");
		Property p = lsa1.getContent().getProperty("prop");
		Lsa lsa2 = lsa1.getCopy();
		p.setValue(new SinglePropertyValue("20"));
		System.out.println("lsa1 = "+lsa1+"\nlsa2 = "+lsa2);
	}

}
