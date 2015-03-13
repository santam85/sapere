package sapere.controller.stub;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sapere.controller.InternalAgent;

public class SapereMiddleware{
	private static Executor executor =
		Executors.newCachedThreadPool();
	
	public static void executeOperation(SapereOperation op, InternalAgent service, int port){
		executor.execute(new StubAgent(op,service,port));
	}
}
