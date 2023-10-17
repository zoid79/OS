package os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

import javax.swing.JOptionPane;

import os.Interrupt.EInterrupt;

public class IOManager extends Thread{
	ProcessQueue suspendQueue;
	ProcessQueue readyQueue;
	Object runningProcess;
	private CircularQueue<Interrupt> interruptQueue;
	private CircularQueue<Interrupt> ioInterruptQueue;


public IOManager(CircularQueue<Interrupt> interruptQueue,CircularQueue<Interrupt> ioInterruptQueue) {
	this.interruptQueue = interruptQueue;
	this.ioInterruptQueue = ioInterruptQueue;
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
			this.runningProcess = null;
			if(this.ioInterruptQueue.peekQueue() !=null) {
				this.runningProcess = this.ioInterruptQueue.deQueue().getObject();
				System.out.println("---------IO Device----------");
				System.out.println("|");
				System.out.println(this.runningProcess.toString()+": using now");
				System.out.println("|");
				System.out.println("---------IO Device----------");

				Interrupt interrupt = new Interrupt(EInterrupt.eIOend);
				interrupt.setObject(this.runningProcess);
				this.interruptQueue.enQueue(interrupt);
			}
			
			}
		
		}
	}

