package os;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import os.Interrupt.EInterrupt;

public class ProcessManager extends Thread{
	private enum EState{eRunning,eStop}
	private EState eState;
	//association
	private CircularQueue<Interrupt> interruptQueue;
	private CircularQueue<Interrupt> ioInterruptQueue;
	private CircularQueue<Interrupt> fileInterruptQueue;
	private CircularQueue<Interrupt> timerInterruptQueue;

	//component
	private CircularQueue<Process> readyQueue;
	private LinkedList suspendQueue;

	InterruptHandler interruptHandler;
	Process runningProcess;
	private HashMap<EInterrupt, Method> interruptVectorTable;
public ProcessManager(CircularQueue<Interrupt> interruptQueue, CircularQueue<Interrupt> ioInterruptQueue, CircularQueue<Interrupt> fileInterruptQueue, CircularQueue<Interrupt> timerInterruptQueue) {
	this.readyQueue = new CircularQueue<Process>();
	this.suspendQueue = new LinkedList<Process>();
	this.interruptVectorTable = new HashMap<EInterrupt, Method>();
	this.interruptQueue = interruptQueue;
	this.fileInterruptQueue = fileInterruptQueue;
	this.ioInterruptQueue = ioInterruptQueue;
	this.timerInterruptQueue = timerInterruptQueue;
	try {
		this.interruptVectorTable.put(EInterrupt.eIOStart, this.getClass().getDeclaredMethod("IOStart",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.etimeOut, this.getClass().getDeclaredMethod("timeOut",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eSystemOut, this.getClass().getDeclaredMethod("systemOut",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eIOend, this.getClass().getDeclaredMethod("IOend",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eProcessStart, this.getClass().getDeclaredMethod("processStart",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eProcessEnd, this.getClass().getDeclaredMethod("processEnd",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eTimerReset, this.getClass().getDeclaredMethod("timerReset",Interrupt.class));

	} catch (NoSuchMethodException | SecurityException e) {e.printStackTrace();}
	}

	private void checkInterrupt() {
		while(this.interruptQueue.peekQueue()!=null) {this.handleInterrupt(this.interruptQueue.deQueue());}
	}
	private void handleInterrupt(Interrupt interrupt) {
		Method method = this.interruptVectorTable.get(interrupt.getInterruptState());
		try {
			System.out.println("handle interrupt: "+interrupt.getInterruptState().toString());
			method.invoke(this, interrupt);}
		catch (IllegalAccessException e) {e.printStackTrace();}
		catch (IllegalArgumentException e) {e.printStackTrace();}
		catch (InvocationTargetException e) {e.printStackTrace();}
	}
	private void timeOut(Interrupt interrupt) {//time out 인터럽트를 받았을 때 인터럽트 안의 process와 running process가 같으면 running process 변경 
		//System.out.println(interrupt.getInterruptState().ordinal()+"번 "+"인터럽트 발생!");
		Process process = (Process) interrupt.getObject();
		if(process==this.runningProcess) {
			this.readyQueue.enQueue(this.runningProcess);
			this.runningProcess = this.readyQueue.deQueue();
			System.out.println("change "+this.runningProcess.getHeader());
		}
	}
	private void systemOut(Interrupt interrupt) {
		//System.out.println(interruptNum+"번 "+"인터럽트 발생!");
	}
	private void timerReset(Interrupt interrupt) {
		//System.out.println(interruptNum+"번 "+"인터럽트 발생!");
		this.timerInterruptQueue.enQueue(interrupt);
	}
	private void IOStart(Interrupt interrupt) {
		//System.out.println(interruptNum+"번 "+"인터럽트 발생!");
		this.suspendQueue.add(this.runningProcess);
		this.runningProcess = this.readyQueue.deQueue();
		this.ioInterruptQueue.enQueue(interrupt);
		Interrupt secondInterrupt = new Interrupt(EInterrupt.eTimerReset);
		secondInterrupt.setObject(this.runningProcess);
		this.interruptQueue.enQueue(secondInterrupt);
	}
	private void IOend(Interrupt interrupt) {
		//System.out.println(interruptNum+"번 "+"인터럽트 발생!");
			this.suspendQueue.remove((Process)interrupt.getObject());
			this.readyQueue.enQueue((Process)interrupt.getObject());
	}
	private void processStart(Interrupt interrupt) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		//System.out.println(interruptNum+"번 "+"인터럽트 발생!");
		Object obj=interrupt.getObject();
			os.FileManager.ExecutableFile process=(os.FileManager.ExecutableFile)obj;
			Process realProcess = new Process();
			realProcess.header = process.getHeader();
			realProcess.pc =process.getPc();
			for(String code:process.getInstructions()) {
				Instruction instruction = new Instruction(code);
				realProcess.addInstruction(instruction);
			}
			if(this.runningProcess==null) {this.runningProcess = realProcess;}
			else {
				this.readyQueue.enQueue(this.runningProcess);
				this.runningProcess = realProcess;
			}
			Interrupt secondInterrupt = new Interrupt(EInterrupt.eTimerReset);
			secondInterrupt.setObject(this.runningProcess);
			this.interruptQueue.enQueue(secondInterrupt);
	}
	private void processEnd(Interrupt interrupt) {
		//System.out.println(interruptNum+"번 "+"인터럽트 발생!");
	}
	@Override
	public void run() {//null도 가능하다
		this.eState = EState.eRunning;
	//	this.runningProcess = new Process();
		while(this.eState == EState.eRunning) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.runningProcess!=null) {executeInstruction();}
			else {this.runningProcess = this.readyQueue.deQueue();}
			this.checkInterrupt();
			System.out.println("--------"+this.runningProcess.getHeader()+"------------");
			System.out.println();
		}
	}
	private void executeInstruction() {
		System.out.println("--------"+this.runningProcess.getHeader()+"------------");
		System.out.println("readyQueue's length: "+this.readyQueue.length);
		System.out.println("suspendQueue's length: "+this.suspendQueue.size());
		Instruction instruction = this.runningProcess.fetch();
		if(instruction.getCommand().equals("compute")) {
			System.out.println("ProcessManager::run->compute");
		}else if(instruction.getCommand().equals("jump")) {
			int labelNum = Integer.parseInt(instruction.getArg());
			System.out.println("ProcessManager::run->jump"+labelNum);
			this.runningProcess.setPC(labelNum);
		}else if(instruction.getCommand().equals("interrupt")) {
			int interuptNum = Integer.parseInt(instruction.getArg());
			Interrupt interrupt = new Interrupt(EInterrupt.eIOStart);
			interrupt.setObject(this.runningProcess);
			this.interruptQueue.enQueue(interrupt);
			//enqueue
			System.out.println("ProcessManager::run->interrupt"+interuptNum);
		}
	}
	
	public class Process {
		private int pc;
		private Vector<Instruction> instructions;
		private String header;
		public String getHeader() {return header;}
		public void setHeader(String header) {this.header = header;}
		public Process() {
			this.pc=0;
			this.instructions = new Vector<Instruction>();
		}
		public Instruction fetch() {
			Instruction instruction = null;
			if(this.instructions.size()>this.pc) {instruction = this.instructions.get(pc++);}
			return instruction;
		}
		public void setPC(int pc) {this.pc = pc;}
		public void addInstruction(Instruction instruction) {this.instructions.add(instruction);}
	}
	public class Instruction{
		private String command;
		private String arg;
		public Instruction(String instruction) {
			String[] tokens = instruction.split(" ");
			this.command = tokens[0];
			if(tokens.length>1) {this.arg = tokens[1];}
		}
		public String getCommand() {return this.command;}
		public String getArg() {return this.arg;}
	}
	public class InterruptHandler{
		public void proccess(Interrupt interrupt) {
			// TODO Auto-generated method stub		
		}
	}	
}
