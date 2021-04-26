package il.ac.telhai.os.software.scheduler;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.software.OperatingSystem;
import il.ac.telhai.os.software.ProcessControlBlock;

public class FCFSScheduler extends Scheduler {
	private static final Logger logger = Logger.getLogger(FCFSScheduler.class);

	public FCFSScheduler(CPU cpu, ProcessControlBlock pcb) {
		super(cpu, pcb);
		// TODO: Initialize scheduler with one process
	}

	@Override
	public void addReady(ProcessControlBlock pcb) {
		// TODO: Add a runnable process to scheduler
	}

	@Override
	public ProcessControlBlock removeCurrent() {
		// TODO: Make currently running process non runnable, i.e. remove it from scheduler
		//       and return it
	}
	
	@Override
	public void schedule() {
		ProcessControlBlock previouslyRunning = current;		
		// TODO: Choose a process to run and run it
		if (current != null && current != previouslyRunning) {
			logger.info("Process " + current.getId() + " gets the CPU");
		}
	}

}