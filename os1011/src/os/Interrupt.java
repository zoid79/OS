package os;

import os.Interrupt.EInterrupt;

public class Interrupt {
	public enum EInterrupt{eIOStart,etimeOut,eSystemOut,eIOend,eProcessStart,eProcessEnd}
	private EInterrupt eState;
	private Object object;
	public Object getObject() {return object;}
	public void setObject(Object object) {this.object = object;}
	public Interrupt(EInterrupt eState) {this.eState = eState;}
	public EInterrupt getInterruptState(){return eState;}
}
