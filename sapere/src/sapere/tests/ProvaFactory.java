package sapere.tests;

import sapere.model.Content;
import sapere.model.JavaLsaContent;
import sapere.model.LogicLsaContent;
import sapere.model.Lsa;
import sapere.model.LsaFactory;

public class ProvaFactory {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LsaFactory f = LsaFactory.getInstance(JavaLsaContent.class);
		Lsa lsa1 = f.createLsaFromProlog("test(content(20))");
		Lsa lsa2 = f.createLsaFromProlog("20","test(content(20))");
		Content c = f.createContentFromProlog(LogicLsaContent.class,"test(content(20))");
		
		
		System.out.println(lsa1.getContent().getClass()+" "+lsa1.toString());
		System.out.println(lsa2.getContent().getClass()+" "+lsa2.toString());
		System.out.println(c.getClass()+" "+c.toString());
	}

}
