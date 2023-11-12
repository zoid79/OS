package os;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import os.Constants.EIODevice;
import os.Interrupt.EInterrupt;
import os.ProcessManager.SProcess;

public class MonitorManager extends IOManager{
	private JTextArea textArea;
	public MonitorManager() {
		super(EIODevice.eMonitorManager);
		JFrame frame = new JFrame("Console to Swing Output");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        textArea = new JTextArea();
        textArea.setEditable(false);

        frame.add(new JScrollPane(textArea));

        frame.setVisible(true);
	}

	@Override
	protected void process(Interrupt interrupt) {

			int object = (int) interrupt.getObject();
			textArea.setText("process"+object+": using now");
			try {Thread.sleep(1000);}
			catch (InterruptedException e) {e.printStackTrace();}
			Interrupt callBackInterrupt = new Interrupt(EInterrupt.eOend);
			callBackInterrupt.setObject(object);
			this.callBackInterruptQueue.enQueue(callBackInterrupt);
			textArea.setText("");
	}

}
