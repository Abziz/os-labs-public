package il.ac.telhai.os.software.language;

import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * 
 * @author cmshalom
 * A trivial implementation of a set of registers
 */
public class Registers {
	private static final Logger logger = Logger.getLogger(Registers.class);

	public static final int FLAG_USER_MODE = 1;
	public static final int FLAG_HALTED = 2;
	public static final int FLAG_ZERO = 4;

	private static final int NUMBER_OF_REGISTERS = Register.values().length;

	private int[] registers;

	public Registers() {
		registers = new int[NUMBER_OF_REGISTERS];
	}

	public Registers(Registers other) {
		registers = Arrays.copyOf(other.registers, NUMBER_OF_REGISTERS);
	}
	
	public int get(Register register) {
		return registers[register.ordinal()];
	}

	public void set(Register register, int value) {
		logger.trace(register + "=" + value);
		registers[register.ordinal()] = value;
	}
	
	public void setFlag (int flagMask, boolean value) {
		int flags = get(Register.FL);
		if (value) {
			flags |= flagMask;			
		} else {
			flags &= ~flagMask;
		}
		set(Register.FL, flags);
	}

	public boolean getFlag(int flag) {
		return (get(Register.FL) & flag) != 0;
	}

	public void add(Register register, int value) {
		logger.trace(register + "+=" + value);
		registers[register.ordinal()] += value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n");
		int i = 1;
		for (Register r : Register.values()) {
			sb.append(r);
			sb.append("=");
			sb.append(get(r));
			sb.append(i++ % 4 == 0 ? "\n" : "\t");
		}
		return sb.toString();
	}
	
}
