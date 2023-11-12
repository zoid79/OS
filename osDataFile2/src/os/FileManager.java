package os;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import os.Constants.EIODevice;
import os.FileManager.ExecutableFile;
import os.Interrupt.EInterrupt;
import os.ProcessManager.Instruction;

public class FileManager extends IOManager{
	String DATA_DIRECTORY = "exe/";


	public FileManager() {
		super(EIODevice.eFileManager);
		}

	private void loadFile(ExecutableFile process, String fileName) {
		String code;
		try {
			Scanner scanner = new Scanner(new File(DATA_DIRECTORY+fileName));
			while(scanner.hasNext()) {
				code=scanner.nextLine();
				process.setCode(code);
	        }
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interrupt interrupt = new Interrupt(EInterrupt.eLoad);

        interrupt.setObject(process);
        this.callBackInterruptQueue.enQueue(interrupt);
	}
	public class ExecutableFile {
		private int pc;
		private Vector<String> instructions;
		public int getPc() {
			return pc;
		}
		public void setPc(int pc) {
			this.pc = pc;
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
	@Override
	protected void process(Interrupt interrupt) {
		ExecutableFile process = new ExecutableFile();
        loadFile(process, interrupt.getObject().toString());
	}
}
