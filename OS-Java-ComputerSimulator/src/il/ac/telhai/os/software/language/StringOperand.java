package il.ac.telhai.os.software.language;

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
}
