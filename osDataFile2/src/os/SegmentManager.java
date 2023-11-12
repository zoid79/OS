package os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import os.ProcessManager.Instruction;
import os.ProcessManager.SProcess;

public class SegmentManager extends MemoryManager{
	private ArrayList<Segment> unUsedMemory;
	private ArrayList<Segment> logicalMemory;
	private HashMap<Integer, Integer> segmentsMap;

	private int count=0;
	public SegmentManager() {
		super();
		logicalMemory = new ArrayList<Segment>();
		unUsedMemory = new ArrayList<Segment>();
		segmentsMap = new HashMap<Integer, Integer>();
		unUsedMemory.add(new Segment(0,1000));
	}
	public void addSegment(int pid, int base, int limit) {
		Segment segment = new Segment(base, limit);
		int index = logicalMemory.indexOf(null);
		if(logicalMemory.indexOf(null)!=-1) {
			logicalMemory.set(index, segment);
		}else logicalMemory.add(segment);
		index = this.logicalMemory.indexOf(segment);
		segmentsMap.put(pid, index);
    }
    public Segment getSegment(int pid) {
        return logicalMemory.get(this.segmentsMap.get(pid));
    }
    public 
    class Segment {
        private int base;
        private int limit;
        public Segment(int base, int limit) {this.base = base;this.limit = limit;}
		public int getBase() {
			return base;
		}
		public void setBase(int base) {
			this.base = base;
		}
		public int getLimit() {
			return limit;
		}
		public void setLimit(int limit) {
			this.limit = limit;
		}
}
	public Instruction fetch(int pid) {
		Segment segment = this.getSegment(pid);
		int pc = (int)memory[segment.base+1];
		if(pc+4>segment.getLimit()) {
			System.exit(0);
		}
		memory[segment.base+1] = pc+1;
		return (Instruction)memory[segment.base+pc+4];
	}
	public int allocate(SProcess process) {
		int limit = process.getSize();
		int base = 0;
		process.setPid(count);
		process.setSindex(count);
		for(Segment segment : unUsedMemory) {
			System.out.println(segment.base);
			if(limit<=segment.getLimit()) {
				base = segment.getBase();
				segment.setBase(segment.getBase()+limit);
				segment.setLimit(segment.getLimit()-limit);
				if(limit==segment.getLimit())this.unUsedMemory.remove(segment);
				break;
			}
		}
		
		this.addSegment(count++, base, limit);
		System.out.println("base: ====================="+base);
		System.out.println("limit: ===================="+limit);
		memory[base++] = process.getPid();
		memory[base++] = process.getPc();
		memory[base++] = process.getSindex();
		memory[base++] = process.getSize();
		for(int i=0;i<process.getInstructions().size();i++) {
			memory[base+i]=process.getInstructions().get(i);
		}
		return process.getPid();
	}
	public void setPC(int pid, int labelNum) {
		Segment segment = this.getSegment(pid);
		memory[segment.base+1] = labelNum;
	}
	public void removeProcess(int pid) {
		int index = this.segmentsMap.remove(pid);
		Segment removedSegment= this.logicalMemory.get(index);
		this.logicalMemory.set(index, null);
		System.out.println("==========remove==========");
		System.out.println("base: "+removedSegment.getBase()+"  limit: "+removedSegment.getLimit());
		System.out.println("==========remove==========");
		this.unUsedMemory.add(removedSegment);
		memorySort();
	}
	public void memorySort(){
		Comparator<Segment> baseComparator = Comparator.comparingInt(p -> p.base);
        Collections.sort(unUsedMemory, baseComparator);
        int beforeBase=-1;
        int beforeLimit=-1;
        int i=0;
        while(i<this.unUsedMemory.size()) {
        	System.out.println("base:"+this.unUsedMemory.get(i).base+"   limit:"+this.unUsedMemory.get(i).limit);
        	if(beforeBase+beforeLimit==this.unUsedMemory.get(i).base) {
        		this.unUsedMemory.get(i).base=beforeBase;
        		this.unUsedMemory.get(i).limit=beforeLimit+this.unUsedMemory.get(i).limit;
        		beforeBase=this.unUsedMemory.get(i).base;
            	beforeLimit=this.unUsedMemory.get(i).limit;
        		this.unUsedMemory.remove(i-1);
        	}else {
        		beforeBase=this.unUsedMemory.get(i).base;
            	beforeLimit=this.unUsedMemory.get(i).limit;
            	i++;
        	}
        	
        }
        Comparator<Segment> limitComparator = Comparator.comparingInt(p -> p.limit);
        Collections.sort(unUsedMemory, limitComparator);
    	System.out.println("Merge");
        for(Segment segment:this.unUsedMemory) 
        System.out.println("unUsedSegment: base = "+segment.getBase()+" limit = "+segment.getLimit());
	}
}
