package os;

import os.Interrupt.EInterrupt;

public class Interrupt {
	public enum EInterrupt{
		eOStart,
		etimeOut,
		eSystemOut,
		eOend,
		eLoad,
		eProcessEnd,
		eTimerReset,
		eIStart,
		eIEnd,
		eFileOpen,
		eMemoryRead}
	private EInterrupt eState;
	private Object object;
	public Object getObject() {return object;}
	public void setObject(Object object) {this.object = object;}
	public Interrupt(EInterrupt eState) {this.eState = eState;}
	public EInterrupt getInterruptState(){return eState;}
}