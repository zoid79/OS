package os;

import java.io.File;

public class Main {
	private OperatingSystem operatingSystem;
	public void run() {
		this.operatingSystem = new OperatingSystem();
		this.operatingSystem.run();
	}
	public static void main(String[] args) {
		
		Main main = new Main();
		main.run();

	}
	

}
