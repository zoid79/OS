package os;

import java.util.Timer;

import os.Interrupt.EInterrupt;

public class MTimer extends Thread{
	private Timer timer;
	CircularQueue<Interrupt> interruptQueue;
	public MTimer(CircularQueue<Interrupt> interruptQueue) {
		this.interruptQueue = interruptQueue;
	}
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Interrupt interrupt = new Interrupt(EInterrupt.etimeOut);
			this.interruptQueue.enQueue(interrupt);
		}
		
	}

}
