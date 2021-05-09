package il.ac.telhai.os.software.scheduler;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.hardware.Timer;
import il.ac.telhai.os.software.OperatingSystem;
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
		logger.trace("addReady(): " + this);
		readyProcesses.add(pcb);
		logger.trace("addReady() END:" + this);
	}

	@Override
	public ProcessControlBlock removeCurrent() {
		logger.trace("removeCurrent(): " + this);
		ProcessControlBlock result = readyProcesses.remove();
		assert(result == current);
		current = null;
		timer.setAlarm(0);
		logger.trace("removeCurrent() END: " + this);
		return result;		

	}

	@Override
	public void schedule() {
		logger.trace("schedule(): " + this);
		ProcessControlBlock previouslyRunning = current;
		current = readyProcesses.peek();
		if (current != null) {
			timer.setAlarm(TIME_SLOT_SIZE);
			current.run(cpu);
		} else {
			logger.warn("Idle, nothing to do" );
			cpu.contextSwitch(OperatingSystem.getInstance(), null);
		}
		if (current != null && current != previouslyRunning) {
			logger.info("Process " + current.getId() + " gets the CPU");
		}
	}

	public String toString() {
		return "Ready="+ readyProcesses + ",current=" + current;
	}

}
