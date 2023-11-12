package os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

import javax.swing.JOptionPane;

import os.Constants.EIODevice;

public abstract class IOManager extends Thread{
	private final EIODevice eDevice;
	protected CircularQueue<Interrupt> interruptQueue;
	protected CircularQueue<Interrupt> callBackInterruptQueue;



public IOManager(EIODevice eDevice) {
	this.eDevice = eDevice;
	this.callBackInterruptQueue = null;
	this.interruptQueue = new CircularQueue<Interrupt>();
}
public EIODevice getType() {
	return this.eDevice;
}
protected void setCallBackInterruptQueue(CircularQueue<Interrupt> callBackInterruptQueue) {
	this.callBackInterruptQueue = callBackInterruptQueue;
}
public CircularQueue<Interrupt> getInterruptQueue() {
	return interruptQueue;
}
public void setInterrupt(Interrupt interrupt) {
	this.interruptQueue.enQueue(interrupt);
}

public void setInterruptQueue(CircularQueue<Interrupt> interruptQueue) {
	this.interruptQueue = interruptQueue;
}
@Override
public void run() {
	while(true) {
		if(this.interruptQueue.peekQueue()!=null) {
			Interrupt interrupt = this.interruptQueue.deQueue();
			this.process(interrupt);
		}
	}
}
abstract protected void process(Interrupt interrupt);
}
