package es.unex.pcd.leorossi.cruze_de_trenes;

import java.util.LinkedList;
import java.util.Queue;

public class Semaphore {

	private Queue<Tren> queue = new LinkedList<Tren>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8695138031008841070L;

	public Semaphore() {
		// TODO Auto-generated constructor stub
	}

	
	
	synchronized public void addQueue(Tren tren) {
		queue.add(tren);
	}

	synchronized public Tren getFirst(){
		return queue.poll();
	}
	
	synchronized public int size(){
		return queue.size();
	}
	
}
