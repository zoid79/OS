package os;

import java.util.LinkedList;

public class OperatingSystem {
	private ProcessManager processManager;
	private FileManager fileManager;
	private IOManager ioManager;
	private MemoryManager memoryManager;
	private CircularQueue<Interrupt> interruptQueue;
	private CircularQueue<Interrupt> IOInterruptQueue;
	private CircularQueue<Interrupt> FileInterruptQueue;
	private CircularQueue<Interrupt> timerInterruptQueue;

	

	private MTimer timer;

	public OperatingSystem() {
		this.interruptQueue =new  CircularQueue<Interrupt>();
		this.IOInterruptQueue =new  CircularQueue<Interrupt>();
		this.FileInterruptQueue =new  CircularQueue<Interrupt>();
		this.timerInterruptQueue =new  CircularQueue<Interrupt>();

		this.ioManager = new IOManager(this.interruptQueue,this.IOInterruptQueue);
		this.processManager = new ProcessManager(this.interruptQueue,this.IOInterruptQueue,this.FileInterruptQueue,this.timerInterruptQueue);
		this.memoryManager = new MemoryManager(this.interruptQueue);
		this.fileManager = new FileManager(this.interruptQueue,this.FileInterruptQueue);
		this.timer = new MTimer(this.interruptQueue,this.timerInterruptQueue);
	}
	public void run() {	
		this.fileManager.start();
		this.processManager.start();
		this.ioManager.start();
		this.memoryManager.start();
		this.timer.start();
	}

}
