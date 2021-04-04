package il.ac.telhai.os.hardware;

import java.util.HashMap;

import il.ac.telhai.os.software.InterruptHandler;
import il.ac.telhai.os.software.OperatingSystem;
import il.ac.telhai.os.software.Software;
import il.ac.telhai.os.software.language.*;

/**
 * 
 * @author cmshalom
 */
public class CPU implements Clockeable {

	private Clock clock;
	private Registers registers = new Registers();
	private Memory realMemory;
    private Software running;

	public CPU (Clock clock, RealMemory realMemory) {
		this.clock = clock; 
		clock.addDevice(this);
		this.realMemory = realMemory;
	}

	@Override
	public void tick() {
		if (registers.getFlag(Registers.FLAG_HALTED)) {
			clock.shutdown();
			return;
		}

		// TODO: Puth here code that handles any pending interrupt
		
	    if (running != null) {
	    	// This allows us to run either an Operating system written in Java,
	    	// or a program written in an Assembly Language
		    if (running instanceof OperatingSystem) { 
			    ((OperatingSystem) running).step();
		    } else {
				Instruction instruction = ((Program)running).fetchLine(registers);
				instruction.execute(registers, realMemory);
		    }
		}

	}

	public void execute(Instruction instruction) {
	    instruction.execute(registers, realMemory);
	}

	public void setInterruptHandler(Class<? extends InterruptSource> cls, InterruptHandler handler) {
		// TODO: Installs a handler in the interrupt vector
	}
	
	public void interrupt(InterruptSource source) {
		// TODO: Record a pending interrupt to be processed during the next tick
	}

	
	public void contextSwitch (Software running) {
		this.running = running;
	}
	
	public String getRegisters() {
		return registers.toString();
	}
	
}