package il.ac.telhai.os.software.language;

import il.ac.telhai.os.hardware.Memory;

public class RegisterOperand extends InstructionOperand {
	private Register register;
	
	public RegisterOperand(boolean isIndirect, Register register) {
		super(isIndirect);
		this.register = register;
	}

	public String toString() {
		return isIndirect ? "[" + register.toString() + "]" : register.toString();
	}


	@Override
	public byte getByte(Registers reg, Memory mem) {
		int val = reg.get(register);
		if (isIndirect) {
			int seg = reg.get(register.getSegmentRegister());
			return mem.readByte(seg, val);			
		} else {
			return (byte) val;
		}

	}

	@Override
	public void setByte(Registers reg, Memory mem, byte value) {
		if (isIndirect) {
			int seg = reg.get(register.getSegmentRegister());
			mem.writeByte(seg, reg.get(register), value);
		} else {
			reg.set(register, value);
		}
	}

	@Override
	public int getWord(Registers reg, Memory mem) {
		int val = reg.get(register);
		if (isIndirect) {
			int seg = reg.get(register.getSegmentRegister());
			return mem.readWord(seg, val);			
		} else {
			return val;
		}
	}

	@Override
	public void setWord(Registers reg, Memory mem, int value) {
		if (isIndirect) {
			int seg = reg.get(register.getSegmentRegister());
			mem.writeWord(seg, reg.get(register), value);
		} else {
			reg.set(register, value);
		}
	}
}
