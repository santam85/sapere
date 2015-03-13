package sapere.controller.reactionmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import sapere.controller.InternalAgent;
import sapere.controller.n2n.DiffusionWorker;
import sapere.controller.notifier.Notifier;
import sapere.controller.space.Space;
import sapere.model.IReaction;
import sapere.model.ISpace;
import sapere.model.ISpaceManager;
import sapere.model.Lsa;
import sapere.model.SortedLinkedBlockingQueue;
import sapere.model.SpaceOperation;
import sapere.model.SpaceOperationType;
import sapere.model.Transaction;
import sapere.model.communication.Notification;
import sapere.model.communication.SubscriptionRequest;
import sapere.model.reaction.Reaction;

public abstract class ReactionManager extends InternalAgent implements ISpaceManager {
	
	private ReentrantLock lock;
	private Condition cond;
	private SpaceMonitor spaceMonitor;
	
	protected ExecutorService executor;
	protected SortedLinkedBlockingQueue<SapereEvent<?>> events;

	protected ISpace space;
	
	protected ArrayList<IReaction> reactions;
	protected long currentTime;
	
	protected static ReactionManager instance;
	
	public static ReactionManager getInstance(){
		return instance;
	}
	
	protected ReactionManager(boolean showSpaceMonitor) {
		super("reaction_manager");
		
		executor = Executors.newCachedThreadPool();
		
		events = new SortedLinkedBlockingQueue<SapereEvent<?>>();
		reactions = new ArrayList<IReaction>();
		lock = new ReentrantLock();
		cond = lock.newCondition();
		
		try {
			space = Space.getInstance();
		}
		catch(Exception e){
			 e.printStackTrace();
		}
		
		if(showSpaceMonitor)
			spaceMonitor = new SpaceMonitor();
		
		start();
	}
	
	public void addReaction(IReaction r){
		reactions.add(r);
		if(spaceMonitor!=null)
			spaceMonitor.update(reactions.toArray(new Reaction[reactions.size()]));
	}
	public void removeReaction(IReaction r){
		reactions.remove(r);
		if(spaceMonitor!=null)
			spaceMonitor.update(reactions.toArray(new Reaction[reactions.size()]));
	}
	
	public void run(){
		spy("activated");
		while(true){
			
			SapereEvent<?> ev=null;
			try {
				boolean signaled = false;
				ev = events.peekAndWait();
				currentTime = System.currentTimeMillis();
				
				lock.lock();
				if(ev.getTime()>currentTime)
					signaled = cond.await(ev.getTime()-currentTime, TimeUnit.MILLISECONDS);
				lock.unlock();
				
				if(signaled){
					manageEventQueue(events.peek());
					continue;
				}
				
				events.remove(ev);
				spy(ev.toString());
				Object op = ev.getEvent();
				if(op instanceof SpaceOperation)
					this.executeOperation((SpaceOperation)op, ev.getSubscription());
				else if(op instanceof Transaction)
					this.executeTransaction((Transaction)op);
				
				manageEventQueue(ev);
				
				continue;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}

	protected abstract void manageEventQueue(SapereEvent<?> ev);
	
	@Override
	public synchronized void queueOperation(SpaceOperation iso, SubscriptionRequest sub){
		events.add(new SapereEvent<SpaceOperation>(System.currentTimeMillis(),iso,sub,SapereEvent.EXTERNAL));
		lock.lock();
		cond.signal();
		lock.unlock();
	}
	
	@Override
	public synchronized void queueTransaction(Transaction iso){
		events.add(new SapereEvent<Transaction>(System.currentTimeMillis(),iso,SapereEvent.EXTERNAL));
		lock.lock();
		cond.signal();
		lock.unlock();
	}

	private void executeOperation(SpaceOperation op, SubscriptionRequest sub) throws Exception{
		Notification res = null;
		ISpace space = Space.getInstance();

		if (op.getType() == SpaceOperationType.Remove){
			space.remove(op.getLsaId());
			res = new Notification(SpaceOperationType.Remove, op.getLsaId(), null);
		}
		else if (op.getType() == SpaceOperationType.Update){
			space.update(op.getLsaId(), op.getNewContent());
			res = new Notification(SpaceOperationType.Update, op.getLsaId(), op.getNewContent());
		}
		else if (op.getType() == SpaceOperationType.Inject){
			String id = space.inject(op.getLsa());
			res = new Notification(SpaceOperationType.Inject, id, null);
		}
		
		notify(res);
		
		if (sub!=null){
			try {
				SubscriptionRequest s = new SubscriptionRequest(res.getLsaSubjectId(), sub.getSubscriber(), sub.getContentFilter());
				Notifier.getInstance().sendMessage(s);
			} catch (Exception e) {
				throw e;
			}
		}
		
		if(spaceMonitor!=null)
			spaceMonitor.update(space.getAllLsa());
			
	}

	private void executeTransaction(Transaction op) throws Exception {
		List<SpaceOperation> ops = op.getOperations();
		//space.beginTransaction();
		ArrayList<Notification> list = new ArrayList<Notification>();
		ArrayList<String> injected = new ArrayList<String>();
		ArrayList<String> removed = new ArrayList<String>();
		ArrayList<Lsa> changed = new ArrayList<Lsa>();
		space.beginTransaction();

		for (SpaceOperation iso : ops){
			if (iso.getType() == SpaceOperationType.Remove){
				space.remove(iso.getLsaId());
				removed.add(iso.getLsaId());
			}
			else if (iso.getType() == SpaceOperationType.Update){
				space.update(iso.getLsaId(), iso.getNewContent());
				Lsa lsa = new Lsa(iso.getLsaId(),iso.getNewContent());
				changed.add(lsa);
			}
			else if (iso.getType() == SpaceOperationType.Inject){
				String id = space.inject(iso.getLsa());
				injected.add(id);
			}
			else if (iso.getType() == SpaceOperationType.Diffuse){
				Lsa lsa = space.read(iso.getLsaId());
				executor.execute(new DiffusionWorker(lsa,iso.getDestIp()));
			}
		}
		//space.finalizeTransaction();
		for (Lsa s : changed){
			list.add(new Notification(SpaceOperationType.Update, s.getId(), s.getContent()));
		}
		for (String s : injected){
			list.add(new Notification(SpaceOperationType.Inject, s, null));
		}
		for (String s : removed){
			list.add(new Notification(SpaceOperationType.Remove, s, null));
		}
		notify(list);
		
		if(spaceMonitor!=null)
			spaceMonitor.update(space.getAllLsa());
	}
	
	private void notify(Notification note){
		try{
			Notifier.getInstance().sendMessage(note);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void notify(List<Notification> notes){
		for (Notification n : notes){
			try{
				Notifier.getInstance().sendMessage(n);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
