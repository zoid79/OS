package os;

import java.util.Timer;
import java.util.TimerTask;

import os.Constants.EIODevice;
import os.Interrupt.EInterrupt;

public class MTimer extends IOManager{
	private Timer timer;
	TimerTask timerTask;
	public MTimer() {
		super(EIODevice.eTimer);
		timer = new Timer();

	}

	@Override
	public void process(Interrupt interrupt) {
			if(interrupt!=null) {
				timer.cancel();
				this.resetTimer();
				timer.schedule(timerTask,0);
		}
	}

	private void resetTimer() {
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
				Interrupt interrupt = new Interrupt(EInterrupt.etimeOut);
				callBackInterruptQueue.enQueue(interrupt);
			}
		};
	}

}
