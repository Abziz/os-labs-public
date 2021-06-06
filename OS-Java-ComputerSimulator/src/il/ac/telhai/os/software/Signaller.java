package il.ac.telhai.os.software;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.software.language.Instruction;

public class Signaller {
	private static final Logger logger = Logger.getLogger(Signaller.class);

	private ProcessControlBlock process;
	private int[] handlers = new int[Signal._NSIG];
	private Queue<Signal> pending = new LinkedList<Signal>();

	public Signaller(ProcessControlBlock process) {
		this.process = process;
	}

	public void setHandler(int signum, int handler) {
		// TODO: set the disposition of the signal 'signum' to 'handler'
		// logger.trace("Setting " + signum + " handler to " + handler);

	}

	public void kill(int signum) {
		Signal s = new Signal(signum);
		// TODO: handle the given signal accordingly
		// logger.info(s + " ignored");
		// logger.info(s + " terminating");
		// logger.info("Handling " + s);
	}
	
	public void handleSignals() {
		Signal s;
		while((s = pending.poll()) != null) {
			logger.info("Handling " + s);
			Instruction instr;
			CPU cpu = OperatingSystem.getInstance().cpu;
			instr = Instruction.create("CALL " + handlers[s.getSigno()]);
			logger.trace("Executing " + instr);
			cpu.execute(instr);
			instr = Instruction.create("PUSH " + s.getSigno());
			logger.trace("Executing " + instr);
			cpu.execute(instr);						
		}
	}

}