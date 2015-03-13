package sapere.tests;

import sapere.model.SortedLinkedBlockingQueue;

public class ProvaSortedQueue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final SortedLinkedBlockingQueue<Integer> q = new SortedLinkedBlockingQueue<Integer>(); 
		new Thread(){
			public void run(){
				System.out.println("Waiting for a peek...");
				try {
					q.peekAndWait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getId()+" "+q.toString());
			}
		}.start();
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		Integer zero = new Integer(0);
		q.add(new Integer(1));
		q.add(new Integer(2));
		q.add(new Integer(3));
		q.add(zero);
		q.add(new Integer(12));
		q.add(new Integer(-10));
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		q.remove(zero);
		q.poll();
		
		System.out.println(Thread.currentThread().getId()+" "+q.toString());
	}
}
