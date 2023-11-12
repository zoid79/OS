package os;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessQueue {
private Queue<Process> queue=new LinkedList<Process>();

public Queue<Process> getQueue() {
	return queue;
}

public void setQueue(Queue<Process> queue) {
	this.queue = queue;
}
public void addQueue(Process process) {
	this.queue.add(process);
}
public Process popQueue() {
	return queue.poll();
}
public Process peekQueue() {
	return queue.peek();
}
}
