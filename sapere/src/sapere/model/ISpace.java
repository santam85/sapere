package sapere.model;

import sapere.controller.space.SpaceException;

public interface ISpace {
	String inject(Lsa a) throws SpaceException;
	void remove(String id) throws SpaceException;
	Lsa read(String id) throws SpaceException;
	String[] getAllLsaId() throws SpaceException;
	Lsa[] getAllLsa();
	void update(String id, Content c) throws SpaceException; 
	void beginTransaction() throws SpaceException;
	void finalizeTransaction() throws SpaceException;
	void rollbackTransaction();
	String getFreshId();
}
