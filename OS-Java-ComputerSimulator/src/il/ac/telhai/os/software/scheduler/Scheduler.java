package il.ac.telhai.os.software.scheduler;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.software.ProcessControlBlock;

public abstract class Scheduler {
	protected CPU cpu;
	protected ProcessControlBlock current;
	
	public Scheduler (CPU cpu, ProcessControlBlock process) {
		this.cpu = cpu;
		this.current = process;
	}

	public ProcessControlBlock getCurrent() {
		return current;
	}

	public abstract void addReady(ProcessControlBlock process);
	public abstract ProcessControlBlock removeCurrent();
	public abstract void schedule();
}