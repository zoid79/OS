package os;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

import os.Constants.EIODevice;
import os.FileManager.ExecutableFile;
import os.Interrupt.EInterrupt;

public class ProcessManager extends Thread{
	private enum EState{eRunning,eStop}
	private EState eState;
	//association
	private FileManager fileManager;
	private MemoryManager memoryManager;
	//component
	private LinkedList<Integer> readyQueue;
	private LinkedList<Integer> suspendQueue;
	private CircularQueue<Interrupt> interruptQueue;
	private int runningProcess = -1;
	private HashMap<EInterrupt, Method> interruptVectorTable;
	private HashMap<EIODevice, IOManager> deviceMap;
public ProcessManager() {
	this.deviceMap = new HashMap<EIODevice, IOManager>();
	this.interruptQueue = new CircularQueue<Interrupt>();
	this.readyQueue = new LinkedList<Integer>();
	this.suspendQueue = new LinkedList<Integer>();
	this.interruptVectorTable = new HashMap<EInterrupt, Method>();
	Scanner sc = new Scanner(System.in);
	System.out.println("페이징 매니저를 원하면 1번, 세그먼트 매니저를 원하시면 2번을 누르세요 default = 세그먼트");
	if(sc.nextInt()==1) this.memoryManager = new PagingManager();
	else this.memoryManager = new SegmentManager();
	sc.close();
	try {//readyQueue에 프로세스가 없으면 타이머 슬립
		this.interruptVectorTable.put(EInterrupt.eOStart, this.getClass().getDeclaredMethod("OStart",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.etimeOut, this.getClass().getDeclaredMethod("timeOut",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eSystemOut, this.getClass().getDeclaredMethod("systemOut",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eOend, this.getClass().getDeclaredMethod("Oend",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eLoad, this.getClass().getDeclaredMethod("load",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eProcessEnd, this.getClass().getDeclaredMethod("processEnd",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eTimerReset, this.getClass().getDeclaredMethod("timerReset",Interrupt.class));
		this.interruptVectorTable.put(EInterrupt.eFileOpen, this.getClass().getDeclaredMethod("fileOpen",Interrupt.class));
	} catch (NoSuchMethodException | SecurityException e) {
		e.printStackTrace();
	}
	}
private void checkInterrupt() {
	while(this.interruptQueue.peekQueue()!=null) {
		this.handleInterrupt(this.interruptQueue.deQueue());
		}
}

private void handleInterrupt(Interrupt interrupt) {
	Method method = this.interruptVectorTable.get(interrupt.getInterruptState());
	try {
//		System.out.println("handle interrupt: "+interrupt.getInterruptState().toString());
		method.invoke(this, interrupt);}
	catch (IllegalAccessException e) {e.printStackTrace();}
	catch (IllegalArgumentException e) {e.printStackTrace();}
	catch (InvocationTargetException e) {e.printStackTrace();}
}
private void fileOpen(Interrupt interrupt) {
	this.deviceMap.get(EIODevice.eFileManager).setInterrupt(interrupt);
}

private void timeOut(Interrupt interrupt) {
	if(this.runningProcess!=-1)
		this.readyQueue.add(runningProcess);
		if(this.readyQueue.peek()==null) {this.runningProcess=-1;}
		else this.runningProcess = (int)this.readyQueue.poll();		
	Interrupt newInterrupt = new Interrupt(EInterrupt.eTimerReset);
		this.interruptQueue.enQueue(newInterrupt);
		
}
private void systemOut(Interrupt interrupt) {
}
private void timerReset(Interrupt interrupt) {
	this.deviceMap.get(EIODevice.eTimer).setInterrupt(interrupt);
}
private void OStart(Interrupt interrupt) {//문제점 타임아웃을 핸들링하기 전에 io start가 발생하면 타임아웃을 한 다음에 핸들링을 하는데 이 떄 iostart가 발생한게 suspend queue로 들어가기 전에 ready queue 맨 뒤로 들어감 
	if((int)interrupt.getObject()==this.runningProcess) {
			if(this.readyQueue.peek()==null) {this.runningProcess=-1;}
			else this.runningProcess = (int)this.readyQueue.poll();}
	else
		this.readyQueue.remove((Integer)interrupt.getObject());
	this.suspendQueue.add((int)interrupt.getObject());
	this.deviceMap.get(EIODevice.eMonitorManager).setInterrupt(interrupt);
	Interrupt secondInterrupt = new Interrupt(EInterrupt.eTimerReset);
	secondInterrupt.setObject(this.runningProcess);
	this.interruptQueue.enQueue(secondInterrupt);
}

private void Oend(Interrupt interrupt) {
	Integer process = (Integer)interrupt.getObject();
	this.suspendQueue.remove(process);
	this.readyQueue.add(process);
}
private void load(Interrupt interrupt) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	Object obj=interrupt.getObject();
	ExecutableFile executableFile=(ExecutableFile)obj;
	SProcess realProcess = new SProcess();
	for(String code:executableFile.getInstructions()) {
		Instruction instruction = new Instruction(code);
		realProcess.addInstruction(instruction);
	}
	realProcess.setSize(realProcess.getInstructions().size()+4);
	int pid = this.memoryManager.allocate(realProcess);
	this.readyQueue.add(pid);
}
public void register(IOManager ioManager) {
ioManager.setCallBackInterruptQueue(this.interruptQueue);	
this.deviceMap.put(ioManager.getType(), ioManager);
}	
private void processEnd(Interrupt interrupt) {
	this.memoryManager.removeProcess((int)interrupt.getObject());
}
@Override
public void run() {//null도 가능하다
	this.eState = EState.eRunning;
	while(this.eState == EState.eRunning) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {e.printStackTrace();}
		System.out.println("--------"+"Process "+this.runningProcess+"------------");
		if(this.runningProcess!=-1) {executeInstruction();}
		else {
			if(this.readyQueue.size()!=0)this.runningProcess = this.readyQueue.poll();}
		this.checkInterrupt();
		}
}
private void executeInstruction() {
	System.out.println("readyQueue's length: "+this.readyQueue.size());
	for(int process:this.readyQueue) {
		System.out.print("process"+process+"  ");
	}
	System.out.println();
	System.out.println("suspendQueue's length: "+this.suspendQueue.size());
	for(int process:this.suspendQueue) {
		System.out.print("process"+process+"  ");
	}
	System.out.println();
	Instruction instruction = this.memoryManager.fetch(this.runningProcess);
	if(instruction.getCommand().equals("compute")) {
		System.out.println("ProcessManager::run->compute");
	}else if(instruction.getCommand().equals("jump")) {
		int labelNum = Integer.parseInt(instruction.getArg());
		System.out.println("ProcessManager::run->jump"+labelNum);
		this.memoryManager.setPC(this.runningProcess,labelNum);
	}else if(instruction.getCommand().equals("interrupt")) {
		EInterrupt eInterrupt =EInterrupt.values()[Integer.parseInt(instruction.getArg())];
		Interrupt interrupt = new Interrupt(eInterrupt);
		interrupt.setObject(this.runningProcess);
		this.interruptQueue.enQueue(interrupt);
		//enqueue
		System.out.println("ProcessManager::run->interrupt"+instruction.getArg());
	}else if(instruction.getCommand().equals("terminate")) {
		System.out.println("ProcessManager::run->terminate");
		Interrupt interrupt = new Interrupt(EInterrupt.eProcessEnd);
		interrupt.setObject(this.runningProcess);
		this.interruptQueue.enQueue(interrupt);
		this.runningProcess = -1;
		}
}
public class SProcess {
	private int pid;
	private int pc;
	private int sindex;
	private int size;
	public Vector<Instruction> getInstructions() {
		return instructions;
	}
	public void setInstructions(Vector<Instruction> instructions) {
		this.instructions = instructions;
	}
	private Vector<Instruction> instructions;
	public SProcess() {
		this.pc=0;
		this.instructions = new Vector<Instruction>();
	}
	public void setPC(int pc) {this.pc = pc;}
	public void addInstruction(Instruction instruction) {this.instructions.add(instruction);}
	public int getPid() {return pid;}
	public void setPid(int pid) {this.pid = pid;}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getSindex() {
		return sindex;
	}
	public void setSindex(int sindex) {
		this.sindex = sindex;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
		}
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
}
