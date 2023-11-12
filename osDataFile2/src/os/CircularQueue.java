package os;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class CircularQueue<T> {
	private final static int MAX_CAPACITY = 20;
	public Vector<T> circularQueue;
	public int front,rear,length;
	private Semaphore semaphore;
	
	public CircularQueue() {
		this.circularQueue = new Vector<T>(MAX_CAPACITY);
		this.circularQueue.setSize(MAX_CAPACITY);
		this.length = 0;
		this.front = 0;
		this.rear = 0;
		this.semaphore = new Semaphore(1);
	}
	public void enQueue(T element) {
		try {
			this.semaphore.acquire();
			if(this.length<this.MAX_CAPACITY) {
				this.circularQueue.set(this.rear++, element);
				this.rear=this.rear%MAX_CAPACITY;
				this.length++;}
			this.semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//else 상황 발생 시 크기를 늘려줌
	}
	public T deQueue() {
		T element = null;

		try {
			this.semaphore.acquire();
		if(this.length>0) {
			element = this.circularQueue.get(this.front);
			this.circularQueue.set(this.front++, null);
			this.front=this.front%MAX_CAPACITY;
			this.length--;
		}
		this.semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//else 상황 발생 시 크기를 늘려줌
		return element;

	}
	public T peekQueue() {
		T element = null;
		try {
			this.semaphore.acquire();
			element= this.circularQueue.get(front);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.semaphore.release();
		return element;

		

	}
}
