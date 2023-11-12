package os;

import java.util.HashMap;

public class OperatingSystem {
	private ProcessManager processManager;
	
	private KeyboardManager keyboardManager;
	private MonitorManager monitorManager;
	private FileManager fileManager;
	private MemoryManager memoryManager;
	private MTimer timer;

	public OperatingSystem() {
		this.processManager = new ProcessManager();
		this.fileManager = new FileManager();
		this.processManager.register(this.fileManager);
		this.timer = new MTimer();
		this.processManager.register(this.timer);
		this.monitorManager = new MonitorManager();
		this.processManager.register(this.monitorManager);
		this.keyboardManager = new KeyboardManager();
		this.processManager.register(this.keyboardManager);

	}
	public void run() {	
		this.processManager.start();
		this.fileManager.start();
		this.timer.start();
		this.monitorManager.start();
		this.keyboardManager.start();
	}

}
