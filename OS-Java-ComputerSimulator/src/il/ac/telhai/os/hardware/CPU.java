package il.ac.telhai.os.hardware;

import il.ac.telhai.os.software.language.*;

public class CPU implements Clockeable {
	
	private Clock clock;
	private Registers registers = new Registers();
	private Memory realMemory;
    private Program program;


	public CPU (Clock clock, RealMemory realMemory) {
		// TODO: Implement c'tor
	}

	@Override
	public void tick() {
		// TODO: Implement
	}

	public void execute(Instruction instruction) {
	    instruction.execute(registers, realMemory);
	}

	public void interrupt(InterruptSource source) {
	}

	public void contextSwitch (Program program) {
		this.program = program;
	}
	
	public String getRegisters() {
		return registers.toString();
	}
	
}