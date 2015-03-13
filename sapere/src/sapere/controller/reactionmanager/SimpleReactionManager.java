package sapere.controller.reactionmanager;

import java.util.LinkedList;
import java.util.List;

import sapere.model.IReaction;
import sapere.model.Lsa;
import sapere.model.Transaction;
import sapere.model.reaction.TotalScore;

public class SimpleReactionManager extends ReactionManager {
	
	protected ReagentsMatcher matcher;
	
	public static ReactionManager getInstance(){
		if(instance == null)
			return instance = new SimpleReactionManager(true);
		else
			return instance;
	}
	
	protected SimpleReactionManager(boolean showMonitor){
		super(showMonitor);
		
		matcher = new SimpleReagentsMatcher();
	}

	@Override
	protected void manageEventQueue(SapereEvent<?> ev) {
		List<SapereEvent<?>> keeplist = new LinkedList<SapereEvent<?>>();
		for(SapereEvent<?> e:events)
			if (e.getSource()==SapereEvent.EXTERNAL)
				keeplist.add(e);
			
		events.clear();
		events.addAll(keeplist);
		
		Lsa[] lsas = space.getAllLsa();
		for(IReaction r:reactions){
			List<TotalScore> scores = matcher.score(lsas, r, 0.0);
			for(TotalScore sc:scores){
				if(sc.score>0){
					SapereEvent<Transaction> event = new SapereEvent<Transaction>(r.getRate().getNextOccurrence(sc.score, currentTime), sc.transaction, SapereEvent.INTERNAL);
					events.add(event);
				}
			}
		}	
	}
}
