package sapere.controller;

import java.util.prefs.Preferences;

import sapere.controller.n2n.DiffusionServer;
import sapere.controller.notifier.Notifier;
import sapere.controller.reactionmanager.ReactionManager;
import sapere.controller.reactionmanager.SimpleReactionManager;
import sapere.controller.skel.SkelServer;
import sapere.controller.space.Space;
import sapere.model.Lsa;
import sapere.model.reaction.ASAPRate;
import sapere.model.reaction.AbsentPropertyValue;
import sapere.model.reaction.LsaPropertyValue;
import sapere.model.reaction.MarkovianRate;
import sapere.model.reaction.MatchPropertyValue;
import sapere.model.reaction.MultiplicativeScoreFunction;
import sapere.model.reaction.NotPropertyValue;
import sapere.model.reaction.Product;
import sapere.model.reaction.ProductType;
import sapere.model.reaction.Property;
import sapere.model.reaction.PropertyModifier;
import sapere.model.reaction.PropertyModifierType;
import sapere.model.reaction.PropertyValue;
import sapere.model.reaction.Reaction;
import sapere.model.reaction.Reagent;
import sapere.model.reaction.SinglePropertyValue;
import sapere.model.reaction.VarPropertyValue;

public class Node {
	
	private static int DIFF_PORT_IN;
	private static int SERVICES_PORT;
	
	public Node(int diffportin,int servicesPort){
		DIFF_PORT_IN = diffportin;
		SERVICES_PORT = servicesPort;
		Space.getInstance();
		ReactionManager manager = SimpleReactionManager.getInstance();
		DiffusionServer.getInstance();
		Notifier.getInstance();
		SkelServer.getInstance();
		
		Preferences prefs = Preferences.systemRoot();
		prefs.putBoolean("VERBOSE", true);
		prefs.putInt("LOG_LEVEL", 1);
			
		//	{d}:display[diffuse = yes, range = {r}, showfrom{s}] + {s}:service[content={c}] -->[PUMP] {d}+{s}+{f}:field[display={d},content={c},range={r},distance=0,diffuse=yes]
		Reaction pump = new Reaction(new MarkovianRate(0.8), new MultiplicativeScoreFunction());
			Reagent re11 = new Reagent("s", "service", pump, new Property[]{
				new Property("content", new VarPropertyValue("c",pump)),
			});	
			Reagent re12 = new Reagent("d", "display", pump, new Property[]{
				new Property("diffuse", new SinglePropertyValue("yes")),
				new Property("range", new VarPropertyValue("r",pump)),
				new Property("showfrom", new LsaPropertyValue("s")),
			});
			
			
			Product pr11 = new Product(ProductType.New, "f", "field", "", "", pump);
				Property prop11 = new Property("display", new LsaPropertyValue("d"));
				Property prop12 = new Property("content", new VarPropertyValue("c",pump));
				Property prop13 = new Property("range", new VarPropertyValue("r",pump));
				Property prop14 = new Property("distance", new SinglePropertyValue("0"));
				Property prop15 = new Property("diffuse", new SinglePropertyValue("yes"));
				pr11.addProperty(prop11);
				pr11.addProperty(prop12);
				pr11.addProperty(prop13);
				pr11.addProperty(prop14);
				pr11.addProperty(prop15);
			Product pr12 = new Product(ProductType.Update, "d", "", "", "", pump);
				PropertyModifier pm11 = new PropertyModifier(PropertyModifierType.UpdateProperty, "diffuse", new SinglePropertyValue("no"),pr12);
				pr12.addPropertyMod(pm11);
			Product pr13 = new Product(ProductType.LeaveUntouched, "s", "", "", "", pump);
				
		pump.addReagent(re11);
		pump.addReagent(re12);
		pump.addProduct(pr11);
		pump.addProduct(pr12);
		pump.addProduct(pr13);
		
		manager.addReaction(pump);
		
		//{f}:field[diffuse=yes,display={d},content={c},distance={rÕ},range={r:r greater-than rÕ}] -->[DIFF] remote {fÕ}:field[display={d},content={c},distance={mtÕ: mtÕ is-sum-of mt,#dist},range={r}]
		Reaction diff = new Reaction(new MarkovianRate(0.8), new MultiplicativeScoreFunction());
			Reagent re21 = new Reagent("f", "field", diff, new Property[]{
				new Property("diffuse", new SinglePropertyValue("yes")),
				new Property("display", new VarPropertyValue("d",diff)),
				new Property("content", new VarPropertyValue("c",diff)),
				new Property("distance", new VarPropertyValue("r'",diff)),
				new Property("range", new MatchPropertyValue("r",new VarPropertyValue("r'",diff),">")),
			});
			
			
			Product pr20 = new Product(ProductType.Update, "f", "", "", "", diff);
				PropertyModifier pm20 = new PropertyModifier(PropertyModifierType.UpdateProperty, "diffuse", new SinglePropertyValue("no"), pr20);
				pr20.addPropertyMod(pm20);
				
			Product pr21 = new Product(ProductType.New, "f'", "field", "", "", diff);
				Property prop20 = new Property("diffuse", new SinglePropertyValue("yes"));
				Property prop21 = new Property("display", new VarPropertyValue("d",diff));
				Property prop22 = new Property("content", new VarPropertyValue("c",diff));
				Property prop23 = new Property("range", new VarPropertyValue("r",diff));
				
				pr21.addProperty(prop20);
				pr21.addProperty(prop21);
				pr21.addProperty(prop22);
				pr21.addProperty(prop23);
				
			Product pr22 = new Product(ProductType.Update, "f'", "", "", "", diff);
				PropertyModifier pm21 = new PropertyModifier(PropertyModifierType.AddProperty, "distance", null, pr22){
					public PropertyValue getModification(){
						Lsa lsa = parentReaction.getCandidateLsa("f");
						int num = Integer.parseInt(lsa.getContent().getProperty(this.getModifiedPropertyName()).getValue().toString());
						return new SinglePropertyValue(num+1+"");
					}
					
					public String toString(){
						return this.getModifiedPropertyName()+"=r'+1";
					}
				};
				pr22.addPropertyMod(pm21);
			Product pr23 = new Product(ProductType.Diffuse, "f'", "", "", "localhost:10002", diff);
			Product pr24 = new Product(ProductType.Diffuse, "f'", "", "", "localhost:10004", diff);
			Product pr25 = new Product(ProductType.Diffuse, "f'", "", "", "localhost:10010", diff);
			Product pr26 = new Product(ProductType.Remove, "f'", "", "", "", diff);
			
		diff.addReagent(re21);
		diff.addProduct(pr20);
		diff.addProduct(pr21);
		diff.addProduct(pr22);
		diff.addProduct(pr23);
		diff.addProduct(pr24);
		diff.addProduct(pr25);
		diff.addProduct(pr26);
		
		manager.addReaction(diff);
		
		//{f}:field[display={d},distance{mt}] +
		//{f'}:field[display={d},distance={mt': mt' > mt}
		// -->[MIN] {f}
		Reaction min = new Reaction(new MarkovianRate(0.8), new MultiplicativeScoreFunction());
			Reagent re31 = new Reagent("f", "field", min, new Property[]{
				new Property("display", new VarPropertyValue("d",min)),
				new Property("distance", new VarPropertyValue("mt",min))
			});
			
			Reagent re32 = new Reagent("f'", "field", min, new Property[]{
					new Property("display", new VarPropertyValue("d",min)),
					new Property("distance", new MatchPropertyValue("mt'",new VarPropertyValue("mt",min),">="))
			});
			
			Product pr31 = new Product(ProductType.Remove, "f'", "", "", "", min);
				
				
		min.addReagent(re31);
		min.addReagent(re32);
		min.addProduct(pr31);
		
		manager.addReaction(min);
		
		//{u}:user[profile={p},ask=yes] +
		//{s}:service[content={c: c not-matches p}]
		//{d}:display[showfrom={s},context={ctx},context has {u}] + 
		// -->[STE-ASK] {d}+{s}+{u}+{r}:recommendation[subject={u},content={c},question=display]
		Reaction steask = new Reaction(new MarkovianRate(0.8), new MultiplicativeScoreFunction());
			Reagent re41 = new Reagent("u", "user", steask, new Property[]{
				new Property("profile", new VarPropertyValue("p",steask)),
				new Property("ask", new SinglePropertyValue("yes"))
			});
			
			Reagent re42 = new Reagent("s", "service", steask, new Property[]{
					new Property("content", new NotPropertyValue(new MatchPropertyValue("c",new VarPropertyValue("p",steask),"matches")))
			});
			
			Reagent re43 = new Reagent("d", "display", steask, new Property[]{
				new Property("showfrom", new LsaPropertyValue("s")),
				new Property("context", new VarPropertyValue("ctx",steask))
			});
			
			Product pr41 = new Product(ProductType.New, "r", "recommendation", "", "", steask);
				Property prop41 = new Property("subject", new LsaPropertyValue("u"));
				Property prop42 = new Property("content", new VarPropertyValue("p",steask));
				Property prop43 = new Property("question", new SinglePropertyValue("display"));
				pr41.addProperty(prop41);
				pr41.addProperty(prop42);
				pr41.addProperty(prop43);
			Product pr42 = new Product(ProductType.Update, "u", "", "", "", steask);
				PropertyModifier pm41 = new PropertyModifier(PropertyModifierType.UpdateProperty, "ask", new SinglePropertyValue("no"),pr42);
				pr42.addPropertyMod(pm41);
				
		steask.addReagent(re41);
		steask.addReagent(re42);
		steask.addReagent(re43);
		steask.addProduct(pr41);
		steask.addProduct(pr42);
		
		manager.addReaction(steask);
		
		//{u}:user[profile={p}] +
		//{r}:recommendation[subject={u},question=display] +
		//{f}:field[source={d},type=display,content={c:c matches p}]
		// -->[STE-ASK] {f}+{u}+{r}:recommendation[answer={d}]
		Reaction stematch = new Reaction(new MarkovianRate(0.8), new MultiplicativeScoreFunction());
			Reagent re51 = new Reagent("u", "user", stematch, new Property[]{
				new Property("profile", new VarPropertyValue("p",stematch))
			});
		
			Reagent re52 = new Reagent("r", "recommendation", stematch, new Property[]{
				new Property("subject", new LsaPropertyValue("u")),
				new Property("question", new SinglePropertyValue("display")),
				new Property("answer", new AbsentPropertyValue())
			});
		
			Reagent re53 = new Reagent("f", "field", stematch, new Property[]{
				new Property("display", new VarPropertyValue("d",stematch)),
				new Property("content", new MatchPropertyValue("c",new VarPropertyValue("p",stematch),"matches"))
			});
			
			Product pr51 = new Product(ProductType.Update, "r", "recommendation", "", "", stematch);
				PropertyModifier pm51 = new PropertyModifier(PropertyModifierType.AddProperty, "answer", new VarPropertyValue("d",stematch),pr51);
				pr51.addPropertyMod(pm51);
				
		stematch.addReagent(re51);
		stematch.addReagent(re52);
		stematch.addReagent(re53);
		stematch.addProduct(pr51);
		
		manager.addReaction(stematch);
		
		//{u}:user + {r}:recommendation[subject={u}, answer={d}] --> [STE-REP] {u}:user[suggestion={d}]
		Reaction sterep = new Reaction(new MarkovianRate(0.8), new MultiplicativeScoreFunction());
			Reagent re61 = new Reagent("u", "user", sterep, new Property[]{
					new Property("suggestion", new AbsentPropertyValue())
			});
		
			Reagent re62 = new Reagent("r", "recommendation", sterep, new Property[]{
				new Property("subject", new LsaPropertyValue("u")),
				new Property("answer", new VarPropertyValue("d",sterep))
			});
			
			Product pr61 = new Product(ProductType.Update, "u", "", "", "", sterep);
				PropertyModifier pm61 = new PropertyModifier(PropertyModifierType.AddProperty, "suggestion", new VarPropertyValue("d",sterep),pr61);
				pr61.addPropertyMod(pm61);
				
		sterep.addReagent(re61);
		sterep.addReagent(re62);
		sterep.addProduct(pr61);
		
		manager.addReaction(sterep);
	}
	
	public static int getDiffPortIn(){
		return DIFF_PORT_IN;
	}

	public static int getServicesPort(){
		return SERVICES_PORT;
	}
	
	public static void main(String[] args) {
		new Node(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
	}
}
