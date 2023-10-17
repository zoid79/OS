package os;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import os.Interrupt.EInterrupt;
import os.ProcessManager.Instruction;

public class FileManager extends Thread{
	//private Process process;

	private CircularQueue<Interrupt> interruptQueue;
	private CircularQueue<Interrupt> fileInterruptQueue;

	public FileManager(CircularQueue<Interrupt> interruptQueue, CircularQueue<Interrupt> fileInterruptQueue) {
			this.interruptQueue = interruptQueue;
			this.fileInterruptQueue = fileInterruptQueue;
		}

	public void start() {
		ExecutableFile process1 = new ExecutableFile();
		process1.setHeader("process1");
		ExecutableFile process2 = new ExecutableFile();
		process2.setHeader("process2");
		ExecutableFile process3 = new ExecutableFile();
		process3.setHeader("process3");
		ExecutableFile process4 = new ExecutableFile();
		process4.setHeader("process4");
		ExecutableFile process5 = new ExecutableFile();
		process5.setHeader("process5");


		String DATA_DIRECTORY = "exe/";
        loadFile(process1, DATA_DIRECTORY);
        loadFile(process2, DATA_DIRECTORY);
        loadFile(process3, DATA_DIRECTORY);
        loadFile(process4, DATA_DIRECTORY);
        loadFile(process5, DATA_DIRECTORY);

System.out.println("********************************");
	}

	private void loadFile(ExecutableFile process, String DATA_DIRECTORY) {
		String code;
		try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+process.getHeader()));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process.setCode(code);
	        }
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt = new Interrupt(EInterrupt.eProcessStart);

        interrupt.setObject(process);
        this.interruptQueue.enQueue(interrupt);
	}
	public class ExecutableFile {
		private int pc;
		private Vector<String> instructions;
		private String header;
		public int getPc() {
			return pc;
		}
		public void setPc(int pc) {
			this.pc = pc;
		}

		public String getHeader() {
			return header;
		}
		public void setHeader(String header) {
			this.header = header;
		}
		public ExecutableFile() {
			this.pc=0;
			this.instructions = new Vector<String>();
		}
		public Vector<String> getInstructions() {
			return instructions;
		}
		public void setInstructions(Vector<String> instructions) {
			this.instructions = instructions;
		}
		public void setCode(String code) {
			this.instructions.add(code);
		}
		public void setPC(int pc) {
			this.pc = pc;
		}
	}
}
