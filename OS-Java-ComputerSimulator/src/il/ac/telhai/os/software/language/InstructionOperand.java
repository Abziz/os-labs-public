package il.ac.telhai.os.software.language;

import il.ac.telhai.os.hardware.Memory;

public abstract class InstructionOperand extends Operand {
	protected boolean isIndirect;
	public abstract byte getByte(Registers reg, Memory mem);
	public abstract void setByte(Registers reg, Memory mem, byte value);
	public abstract int getWord(Registers reg, Memory mem);
	public abstract void setWord(Registers reg, Memory mem, int value);

	public InstructionOperand (boolean isIndirect) {
		this.isIndirect = isIndirect;
	}

}
