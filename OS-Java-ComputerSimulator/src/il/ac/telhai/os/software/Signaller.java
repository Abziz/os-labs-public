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
	private Queue<Signal>  pending = new LinkedList<Signal>();

	public Signaller(ProcessControlBlock process) {
		this.process = process;
	}

	public void setHandler (int signum, int handler) throws IllegalArgumentException {
		logger.trace("Setting " + signum + " handler to " + handler);
		if (signum >= Signal._NSIG || signum == Signal.SIGKILL)  throw new IllegalArgumentException("Signum = " + signum);
		handlers[signum] = handler;
	}

	public void kill(int signum) {
		Signal s = new Signal(signum);
		if (handlers[signum]<0) {   // SIG_IGN
			logger.info(s + " ignored");						
		} else 	if (handlers[signum]==0) {  // SIG_DFL
			switch (s.getDefaultBehaviour()) {
			case IGNORE:
				logger.info(s + " ignored");										
				break;
			case TERMINATE:
				logger.info(s + " terminating");						
				process.exit(256+signum);
				break;
			default:
				assert(false); // default cannot be HANDLE
				break;
			}
		} else {
			pending.add(s);
		} 
	}

	public boolean signalsHandled() {
		Signal s;
		boolean ret = false;
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
			ret = true;
		}
		return ret;
	}
}