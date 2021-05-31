package il.ac.telhai.os.software.language;

import il.ac.telhai.os.hardware.Memory;

public class StringOperand extends Operand {
	private String value;

	public StringOperand(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}

	@Override
	public int getWord(Registers registers, Memory memory) {
		return Integer.parseInt(value);
	}

	@Override
	public byte getByte(Registers registers, Memory memory) {
		return Byte.parseByte(value);
	}

	@Override
	public String getString(Registers registers, Memory memory) {
		return value;
	}
}
