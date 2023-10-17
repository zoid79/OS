package os;

import java.util.Timer;

import os.Interrupt.EInterrupt;

public class MTimer extends Thread{
	private Timer timer;
	CircularQueue<Interrupt> interruptQueue;
	CircularQueue<Interrupt> timerInterruptQueue;
	Object object;

	public MTimer(CircularQueue<Interrupt> interruptQueue, CircularQueue<Interrupt> timerInterruptQueue) {
		this.interruptQueue = interruptQueue;
		this.timerInterruptQueue = timerInterruptQueue;

	}
	@Override
	public void run() {
		while(true) {
			while(this.timerInterruptQueue.peekQueue()!=null) this.object=this.timerInterruptQueue.deQueue().getObject();
			try {Thread.sleep(150);}
			catch (InterruptedException e) {e.printStackTrace();}
		Interrupt interrupt = new Interrupt(EInterrupt.etimeOut);
		interrupt.setObject(this.object);
		this.interruptQueue.enQueue(interrupt);
		}
		}

}
