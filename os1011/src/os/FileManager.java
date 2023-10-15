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
		Process process1 = new Process();
		Process process2 = new Process();
		Process process3 = new Process();
		Process process4 = new Process();
		Process process5 = new Process();

		String DATA_DIRECTORY = "exe/";
		String code;
        try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+"process1"));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process1.setCode(code);
	        }
			process1.setHeader("process1");
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt1 = new Interrupt(EInterrupt.eProcessStart);

        interrupt1.setObject(process1);
        this.interruptQueue.enQueue(interrupt1);
        try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+"process2"));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process2.setCode(code);
	        }
			process2.setHeader("process2");
			scanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt2 = new Interrupt(EInterrupt.eProcessStart);

        interrupt2.setObject(process2);
        this.interruptQueue.enQueue(interrupt2);

        process3 = new Process();
        try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+"process2"));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process3.setCode(code);
	        }
			process3.setHeader("process3");
			scanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt3 = new Interrupt(EInterrupt.eProcessStart);
        interrupt3.setObject(process3);
        this.interruptQueue.enQueue(interrupt3);

        try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+"process2"));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process4.setCode(code);
	        }
			process4.setHeader("process4");
			scanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt4 = new Interrupt(EInterrupt.eProcessStart);
        interrupt4.setObject(process4);
        this.interruptQueue.enQueue(interrupt4);

        try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+"process2"));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process5.setCode(code);
	        }
			process5.setHeader("process5");
			scanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt5 = new Interrupt(EInterrupt.eProcessStart);

        interrupt5.setObject(process5);
        this.interruptQueue.enQueue(interrupt5);
System.out.println("********************************");
	}
	public class Process {
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
		public Process() {
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
