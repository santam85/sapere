package sapere.controller.reactionmanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sapere.controller.space.Space;
import sapere.model.LogicLsaContent;
import sapere.model.Lsa;
import sapere.model.LsaFactory;
import sapere.model.SpaceOperation;
import sapere.model.SpaceOperationType;
import sapere.model.Transaction;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

public class PrologReactionManager extends ReactionManager {
	
	private Map<String,String> lsamap;
	private Map<String,String> lsamapinverted;
	
	private LsaFactory factory = LsaFactory.getInstance(LogicLsaContent.class);
 
	private static Prolog engine;
	private static final String theory = "" +
			"%first(+Type,+Rate,-Time): computes the duration T of a new event\n"+
			"first(markovian,R,T):-rand_float(X),T is log(1.0/X)/R.\n"+
			"first(fixed,R,T):-T is 1.0/R.\n"+
			"first(now,_,0).\n"+
			"\n"+
			"%ecolaw(Name,ListOfReagents,rate(Type,Expression),ListOfProducts,Conditions)\n"+
			"%4 examples of ecolaw below\n"+
			"ecolaw(io,[na(conc(NA)),cl(conc(CL)),nap(conc(NAP)),clm(conc(CLM))],\n"+
			"	rate(markovian,0.1*NA*CL),\n"+
			"	[na(conc(NA2)),cl(conc(CL2)),nap(conc(NAP2)),clm(conc(CLM2))],\n"+
			"	(NA2 is NA-1,CL2 is CL-1, NAP2 is NAP+1, CLM2 is CLM+1, NA2>=0, CL2>=0, NAP2 >=0, CLM2 >=0)).\n"+
			"ecolaw(deio,[na(conc(NA2)),cl(conc(CL2)),nap(conc(NAP2)),clm(conc(CLM2))],\n"+
			"	rate(markovian,0.9*NA2*CL2),\n"+
			"	[na(conc(NA)),cl(conc(CL)),nap(conc(NAP)),clm(conc(CLM))],\n"+
			"	(NA is NA2+1,CL is CL2+1, NAP is NAP2-1, CLM is CLM2-1, NA2>=0, CL2>=0, NAP2 >=0, CLM2 >=0)).\n"+
			"ecolaw(fix, [p(X,Y),q(Y)],rate(fixed,10),[r(X,Y)],true).\n"+
			"ecolaw(now, [r(X)],rate(now,1),[q(X)],true).\n"+
			"\n"+
			"%action(-A): returns an action that can be executed, which is basically an instantiated eco-law\n"+
			"action(act(Name,LPre,rate(Type,Rate),O)):-\n"+
			"    ecolaw(Name,LPre,rate(Type,RateExp),O,C),\n"+
			"    pick(LPre),\n"+
			"    call(C),\n"+
			"    Rate is RateExp.\n"+
			"pick([]).\n"+
			"pick([H|T]):-call(lsa(H)),pick(T).\n"+
			"\n"+
			"% try ?-action(X).\n"+
			"\n"+
			"\n"+
			"% event(act(Name,LSAtoDrop,rate(Type,Rate),LSAtoAdd),TimeItWasScheduled,TimeToWakeup).\n"+
			"\n"+
			"% newqueue(+CurrentTime,-SortedListOfEvents).\n"+
			"newqueue(T0,LO):-findall(event(act(Name,LPre,rate(Type,Exp2),O),T0,T),\n"+
			"		 (action(act(Name,LPre,rate(Type,Rate),O)),first(Type,Rate,X),T is T0+X),L),\n"+
			"	         quicksort(L,compareaction,LO).\n"+
			"\n"+
			"compareaction(event(_,_,T),event(_,_,T2)):-T<T2.\n"+
			"\n"+
			"%dependent(CauseAction,DependentAction).\n"+
			"dependent(act(_,P,_,E),act(_,P2,_,_)):-chmjoin(P,E,L),member(Y,L),member(Y,P2),!.\n"+
			"chmjoin([],P,P):-!.\n"+
			"chmjoin(P,[],P):-!.\n"+
			"chmjoin([H|T],P,L):-member2(P,H,P2),!,chmjoin(T,P2,L).\n"+
			"chmjoin([H|T],P,[H|L]):-chmjoin(T,P,L).\n"+
			"member2([H|T],H,T).\n"+
			"member2([H|T],H2,[H|T2]):-member(T,H2,T2).";

	public static ReactionManager getInstance(){
		if(instance == null)
			return instance = new PrologReactionManager(true);
		else
			return instance;
	}
	
	protected PrologReactionManager(boolean showMonitor){
		super(showMonitor);
		
		lsamap = new HashMap<String,String>();
		lsamapinverted = new HashMap<String,String>();
		
		engine = new Prolog();
		try {
			engine.setTheory(new Theory(theory));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void manageEventQueue(SapereEvent<?> ev) {
		List<SapereEvent<?>> keeplist = new LinkedList<SapereEvent<?>>();
		for(SapereEvent<?> e:events)
			if (e.getSource()==SapereEvent.EXTERNAL)
				keeplist.add(e);
		
		String lsas = "";
		lsamap.clear();
		lsamapinverted.clear();

		for(Lsa lsa1:Space.getInstance().getAllLsa()){
			Lsa lsa = new Lsa(lsa1.getId(),new LogicLsaContent(lsa1.getContent()));
			lsamap.put(lsa.getId(), lsa.getContent().toString());
			lsamapinverted.put(lsa.getContent().toString(),lsa.getId());
			lsas+="lsa("+lsa.getContent()+").\n";
		}

		try {
			engine.setTheory(new Theory(theory+"\n\n"+lsas));
			SolveInfo si = engine.solve("newqueue("+this.currentTime+",Q).");
			if(si.isSuccess()){
				events.clear();
				events.addAll(generateEvents(si.getVarValue("Q")));
			}
				
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		} catch (MalformedGoalException e) {
			e.printStackTrace();
		} catch (NoSolutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		events.addAll(keeplist);	
	}
	
	private List<String> termToList(String term){
		List<String> tmp = new LinkedList<String>();
		try {
			SolveInfo si = engine.solve("member(X,"+term+").");
			if(si.isSuccess()){
				tmp.add(si.getVarValue("X").toString());
				while(si.hasOpenAlternatives()){
					si=engine.solveNext();
					if(si.isSuccess())
						tmp.add(si.getVarValue("X").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp;
	}

	@SuppressWarnings("unused")
	private List<SapereEvent<?>> generateEvents(Term varValue) {
		
		List<SapereEvent<?>> l = new LinkedList<SapereEvent<?>>();
		
		if(varValue.toString().equals("[]"))
			return l;
		
		String[] terms = varValue.toString().split("^\\[|event\\(|\\),event\\(|\\)\\]$");
		for(String term:terms){
			if(term.length()==0)
				continue;
			long tex = Math.round(Double.parseDouble(term.substring(term.lastIndexOf(',')+1, term.length())));
			String tmp = term.substring(0, term.lastIndexOf(','));
			long tsc = Math.round(Double.parseDouble((tmp.substring(tmp.lastIndexOf(',')+1, tmp.length()))));
			String act=tmp.substring(0, tmp.lastIndexOf(','));
			List<String> lsastoadd = termToList(act.substring(act.lastIndexOf('['),act.lastIndexOf(']')+1));
			tmp = act.substring(0,act.lastIndexOf('[')-1);
			String rate = tmp.substring(tmp.lastIndexOf(",rate")+1);
			tmp = tmp.substring(0,tmp.lastIndexOf(",rate"));
			List<String> lsastorem = termToList(tmp.substring(tmp.lastIndexOf('['),tmp.length()));
			tmp = tmp.substring(0,tmp.lastIndexOf('[')-1);
			String name = tmp.substring(tmp.indexOf('(')+1);
			
			List<SpaceOperation> operations = new LinkedList<SpaceOperation>();
			
			for(String lsa:lsastorem){
				boolean isupdate = false;
				String updateContent = "";
				for(String lsa1:lsastoadd){
					if(lsa.substring(0, lsa.indexOf('(')).equals(lsa1.substring(0, lsa1.indexOf('(')))){
						isupdate = true;
						updateContent = lsa1;
						lsastoadd.remove(lsa1);
						break;
					}
				}
				if(isupdate)
					operations.add(new SpaceOperation(SpaceOperationType.Update,lsamapinverted.get(lsa),null,factory.createContentFromProlog(updateContent)));
				else
					operations.add(new SpaceOperation(SpaceOperationType.Remove,lsamapinverted.get(lsa),null,null));
			}
			
			for(String lsa:lsastoadd)
				operations.add(new SpaceOperation(SpaceOperationType.Inject,null,factory.createLsaFromProlog(lsa),null));
			
			Transaction t = new Transaction(operations);

			l.add( new SapereEvent<Transaction>(tex, t, SapereEvent.INTERNAL) );
		}
		return l;
	}

	
}
