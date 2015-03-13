package sapere.controller.space;

import java.util.HashMap;

import sapere.misc.Logger;
import sapere.model.Content;
import sapere.model.ISpace;
import sapere.model.Lsa;

public class Space implements ISpace{
	private static ISpace instance;
	
	private static long nextId;
	private HashMap<String, Lsa> map;
	private HashMap<String, Lsa> mapclone;
	
	public static ISpace getInstance() {
		if(instance == null)
			instance = new Space();
		return instance;
	}
	
	private Space(){
		map = new HashMap<String, Lsa>();
	}
	
	public String getFreshId(){
		while(map.containsKey(""+nextId)){
			nextId++;
		}
		return ""+nextId++;
	}

	@Override
	public String[] getAllLsaId(){
		String[] array = new String[map.keySet().size()];
		map.keySet().toArray(array);
		
		return array;
	}

	@Override
	public void remove(String id) throws SpaceException {
		Lsa lsa = map.remove(id);
		spy("removed "+lsa);
	}

	@Override
	public void beginTransaction() {
		mapclone = new HashMap<String, Lsa>();
		for(String id:map.keySet())
			mapclone.put(id, map.get(id).getCopy());
	}

	@Override
	public void finalizeTransaction() {
		mapclone = null;
	}
	
	@Override
	public void rollbackTransaction() {
		map = mapclone;
		mapclone = null;
	}

	@Override
	public void update(String id, Content c){
		Lsa lsa = map.get(id);
		if (lsa != null){
			lsa.setContent(c.getCopy());
			spy("updated "+lsa);
		}
	}

	@Override
	public String inject(Lsa a) {
		if(a.getId()==null){
			String id = getFreshId();
			a.setId(id);	
		}
		
		Lsa copy = a.getCopy();
		map.put(copy.getId(), copy);
		
		spy("injected "+copy);
		
		return copy.getId();
	}

	@Override
	public Lsa read(String id) throws SpaceException{
		Lsa res = map.get(id);
		if (res!=null){			
			return res.getCopy();
		}
		
		throw new SpaceException("read by id error"); 
	}
	
	public static void spy(String m){
		Logger.getInstance().log("["+System.currentTimeMillis()+"] space - "+m);
	}

	@Override
	public Lsa[] getAllLsa() {
		Lsa[] array = new Lsa[map.values().size()];
		//int i = 0;
		//for(Lsa lsa: map.values())
			//array[i++] = lsa.getCopy();
		
		return map.values().toArray(array);
	}
	
	@Override
	public String toString(){
		return map.values().toString();
	}
}
