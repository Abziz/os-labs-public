package il.ac.telhai.os.software.language;

import il.ac.telhai.os.hardware.Memory;

public class AbsoluteOperand extends InstructionOperand {
    private int value;
	
	public AbsoluteOperand(boolean isIndirect, int value) {
		super(isIndirect);
		this.value = value;
	}
	
	public String toString() {
		return isIndirect ? "[" + value + "]" : new Integer(value).toString();
	}

	@Override
	public byte getByte(Registers reg, Memory mem) {
		return (byte) (isIndirect ? mem.readByte(reg.get(Register.DS), this.value) : this.value); 
	}

	@Override
	public void setByte(Registers reg, Memory mem, byte value) {
		int address =  isIndirect ? mem.readWord(reg.get(Register.DS), this.value) : this.value; 
		mem.writeByte(reg.get(Register.DS), address, value);
	}

	@Override
	public int getWord(Registers reg, Memory mem) {
		return isIndirect ? mem.readWord(reg.get(Register.DS), this.value) : this.value; 
	}

	@Override
	public void setWord(Registers reg, Memory mem, int value) {
		int address =  isIndirect ? mem.readWord(reg.get(Register.DS), this.value) : this.value; 
		mem.writeWord(reg.get(Register.DS), address, value);
	}

}
