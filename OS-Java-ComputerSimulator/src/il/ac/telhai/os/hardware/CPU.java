package il.ac.telhai.os.hardware;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Queue;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import il.ac.telhai.os.software.InterruptHandler;
import il.ac.telhai.os.software.OperatingSystem;
import il.ac.telhai.os.software.Software;
import il.ac.telhai.os.software.language.*;

/**
 * 
 * @author cmshalom
 * A simple CPU implementation that is quite different than a real one:
 * The context switching is implemented in a very unrealistic way:
 *    The CPU contains a pointer to the currently running software 
 *    At every clock tick it will run one step of it, unless interrupted during the 
 *    previous clock tick. 
 *    In such a case it will run the interrupt handler if one is installed for 
 *    the type of interrupt source.
 */
public class CPU extends MMU implements Clockeable {
	private static final Logger logger = Logger.getLogger(CPU.class);

	private Clock clock;
	private Registers registers = new Registers();
	private Software running;
	private HashMap<Class<? extends InterruptSource>, InterruptHandler> interruptVector = 
			new HashMap<Class<? extends InterruptSource>, InterruptHandler>(); 
	private Queue<InterruptSource> pendingInterrupts = new PriorityQueue<InterruptSource>(new InterruptSourceComparator());
	
	private class InterruptSourceComparator implements Comparator<InterruptSource> {
		@Override
		public int compare(InterruptSource o1, InterruptSource o2) {
			return o1.getPriority() - o2.getPriority();
		}
	}



	public CPU (Clock clock, RealMemory realMemory, int numberOfPages) {
		super(realMemory, numberOfPages);
		this.clock = clock; 
		clock.addDevice(this);
	}

	private boolean halted() {
		boolean ret = registers.getFlag(Registers.FLAG_HALTED);
		if (ret) clock.shutdown();
		return ret;
	}

	private void handleInterrupt(InterruptSource source) {
		InterruptHandler handler = getHandler(source);
		if (handler != null) {
			boolean oldFlag = registers.getFlag(Registers.FLAG_USER_MODE);
			registers.setFlag(Registers.FLAG_USER_MODE, false);
			try {
		        handler.handle(source);
			} catch (Trap trap) {
				handleInterrupt (trap);
			}
			if (halted()) return;
			registers.setFlag(Registers.FLAG_USER_MODE, oldFlag);
		}		
	}

	@Override
	public void tick() {
		if (halted()) return;

		if (pendingInterrupts.size() > 1) logger.trace("Multiple pending interrupts");
		while (pendingInterrupts.size() != 0) {
			handleInterrupt(pendingInterrupts.remove());
			if (halted()) return;
		}

		if (running != null) {
			// This allows us to run either an Operating system written in Java,
			// or a program written in an Assembly Language
			if (running instanceof OperatingSystem) { 
				((OperatingSystem) running).step();
			} else {
				try {
					ProgramLine programLine = ((Program)running).fetchLine(registers);
					programLine.execute(registers, this);
				} catch (SystemCall call) {
					interrupt(call);
				} catch (PageFault call) {
					interrupt(call);
					registers.add(Register.IP, -1);
				} catch (Trap trap) {
					interrupt(trap);
				}
			}
		}
	}

	public void execute(Instruction instruction) {
		try {
			instruction.execute(registers, this);
		} catch (Trap t) {
			this.interrupt(t);
		}
	}
	
	public void setInterruptHandler(Class<? extends InterruptSource> cls, InterruptHandler handler) {
		interruptVector.put(cls , handler);
	}

	private InterruptHandler getHandler(InterruptSource source) {
		InterruptHandler result = null;
		@SuppressWarnings("rawtypes")
		Class cls = (Class) source.getClass();
		while (result == null && cls != null) {
			result = interruptVector.get(cls);
			cls = cls.getSuperclass();
		}
		return result;
	}

	public void interrupt(InterruptSource source) {
		pendingInterrupts.add(source);
	}

	public void contextSwitch (Software software, Registers registers) {
		running = software;
		if (registers != null) this.registers = registers;
	}

	public String getRegisters() {
		return registers.toString();
	}

}