package il.ac.telhai.os.hardware;

import java.util.HashMap;

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
public class CPU implements Clockeable {

	private Clock clock;
	private Registers registers = new Registers();
	private Memory realMemory;
	private Software running;
	private HashMap<Class<? extends InterruptSource>, InterruptHandler> interruptVector = 
			new HashMap<Class<? extends InterruptSource>, InterruptHandler>(); 
	private InterruptSource pendingInterrupt;

	public CPU (Clock clock, RealMemory realMemory) {
		this.clock = clock; 
		clock.addDevice(this);
		this.realMemory = realMemory;
	}

	private boolean halted() {
		boolean ret = registers.getFlag(Registers.FLAG_HALTED);
		if (ret) clock.shutdown();
		return ret;
	}


	@Override
	public void tick() {
		if (halted()) return;

		if (pendingInterrupt != null) {
			InterruptSource source = pendingInterrupt;
			pendingInterrupt = null;
			InterruptHandler handler = getHandler(source);
			if (handler != null) {
				registers.setFlag(Registers.FLAG_USER_MODE, false);
				handler.handle(source);
				if (halted()) return;
			}
		}

		if (running != null) {
			// This allows us to run either an Operating system written in Java,
			// or a program written in an Assembly Language
			if (running instanceof OperatingSystem) { 
				((OperatingSystem) running).step();
			} else {
				try {
					ProgramLine programLine = ((Program)running).fetchLine(registers);
					programLine.execute(registers, realMemory);
				} catch (SystemCall call) {
					interrupt(call);
				} catch (Trap trap) {
					interrupt(trap);
					registers.add(Register.IP, -1);
				}
			}
		}
	}

	public void execute(Instruction instruction) {
		try {
			instruction.execute(registers, realMemory);
		} catch (Trap t) {
			this.interrupt(t);
		}
	}
	
	public int getWord(Operand op) {
		return op.getWord(registers, realMemory);
	}

	public int getByte(Operand op) {
		return op.getByte(registers, realMemory);
	}
	
	public String getString(Operand op) {
			return op.getString(registers, realMemory);
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
		pendingInterrupt = source;
	}

	public void contextSwitch (Software software, Registers registers) {
		running = software;
		if (registers != null) this.registers = registers;
	}

	public String getRegisters() {
		return registers.toString();
	}

}