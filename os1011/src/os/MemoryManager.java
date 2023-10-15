package os;

public class MemoryManager extends Thread{
	private CircularQueue<Interrupt> interruptQueue;

public MemoryManager(CircularQueue<Interrupt> interruptQueue) {
		this.interruptQueue = interruptQueue;
	}
	@Override
	public void run() {
		
	}

}
