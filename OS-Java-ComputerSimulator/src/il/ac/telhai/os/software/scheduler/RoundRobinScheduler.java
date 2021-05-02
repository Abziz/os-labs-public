package il.ac.telhai.os.software.scheduler;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.hardware.Timer;
import il.ac.telhai.os.software.ProcessControlBlock;

public class RoundRobinScheduler extends Scheduler {
	private static final Logger logger = Logger.getLogger(RoundRobinScheduler.class);
	private static final int TIME_SLOT_SIZE = 10;

	private Queue<ProcessControlBlock> readyProcesses = new LinkedList<ProcessControlBlock>();
	private Timer timer;

	public RoundRobinScheduler(CPU cpu, ProcessControlBlock pcb, Timer timer) {
		super(cpu, pcb);
		readyProcesses.add(pcb);
		this.timer = timer;
	}

	@Override
	public void addReady(ProcessControlBlock pcb) {
		// TODO: add process to the ready queue
	}

	@Override
	public ProcessControlBlock removeCurrent() {
		// TODO: remove currently running process and return it
	}

	@Override
	public void schedule() {
		ProcessControlBlock previouslyRunning = current;

		// TODO: decide which process runs next and set the timer for preemption

		if (current != null && current != previouslyRunning) {
			logger.info("Process " + current.getId() + " gets the CPU");
		}
	}
}
