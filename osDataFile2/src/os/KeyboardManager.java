package os;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import os.Constants.EIODevice;
import os.Interrupt.EInterrupt;

public class KeyboardManager extends IOManager{

	public KeyboardManager() {
		super(EIODevice.eKeyboardManager);
		JFrame frame = new JFrame("Swing TextArea Input");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        JPanel panel = new JPanel();
        frame.add(panel);
        final JTextArea textArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton submitButton = new JButton("Submit");
        panel.add(scrollPane);
        panel.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = textArea.getText();
                textArea.setText("");
                Interrupt interrupt = new Interrupt(EInterrupt.eFileOpen);
                interrupt.setObject(inputText);
                callBackInterruptQueue.enQueue(interrupt);
            }
        });
        frame.setVisible(true);
	}
	@Override
	protected void process(Interrupt interrupt) {}
}
