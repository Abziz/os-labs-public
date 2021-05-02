package il.ac.telhai.os.software.scheduler;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.software.OperatingSystem;
import il.ac.telhai.os.software.ProcessControlBlock;

public class FCFSScheduler extends Scheduler {
	private static final Logger logger = Logger.getLogger(FCFSScheduler.class);

	private Queue<ProcessControlBlock> readyProcesses = new LinkedList<ProcessControlBlock>();

	public FCFSScheduler(CPU cpu, ProcessControlBlock pcb) {
		super(cpu, pcb);
		readyProcesses.add(pcb);
	}

	@Override
	public void addReady(ProcessControlBlock pcb) {
		readyProcesses.add(pcb);
	}

	@Override
	public ProcessControlBlock removeCurrent() {
		ProcessControlBlock result = readyProcesses.remove();
		assert(result == current);
		current = null;
		return result;		
	}

	@Override
	public void schedule() {
		ProcessControlBlock previouslyRunning = current;
		current = readyProcesses.peek();
		if (current != null) {
			current.run(cpu);
		} else {
			logger.warn("Idle, nothing to do" );
			cpu.contextSwitch(OperatingSystem.getInstance(), null);
		}
		if (current != null && current != previouslyRunning) {
			logger.info("Process " + current.getId() + " gets the CPU");
		}
	}

}
