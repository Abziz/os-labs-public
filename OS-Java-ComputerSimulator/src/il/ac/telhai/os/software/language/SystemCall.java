package il.ac.telhai.os.software.language;

import java.text.ParseException;

import il.ac.telhai.os.hardware.InterruptSource;
import il.ac.telhai.os.hardware.Memory;

import org.apache.log4j.Logger;

/**
 * 
 * @author cmshalom
 * Every instruction has a mnemonic code, and at most two operands
 */
@SuppressWarnings("serial")
public class SystemCall extends RuntimeException implements ProgramLine, InterruptSource {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SystemCall.class);

	private Mnemonic mnemonicCode;
	private Operand op1, op2;

	public SystemCall(AssemblerLine line) throws ParseException {
		mnemonicCode = line.getMnemonic();
		op1 = line.getOp1();			
		op2 = line.getOp2();			
	}

	public SystemCall(String commandLine) throws ParseException {
		this(new AssemblerLine(commandLine, null));		
	}

	@Override
	public String toString() {
		return mnemonicCode + "\t" + (op1==null ? "" : op1 + 
				                     (op2==null ? "" : "," + op2));
	}

	@Override
	public int getPriority() {
		return 0;
	}

	public Mnemonic getMnemonicCode() {
		return mnemonicCode;
	}

	public Operand getOp1() {
		return op1;
	}

	public Operand getOp2() {
		return op2;
	}

	@Override
	public void execute(Registers registers, Memory memory) {
		throw this;
	}

}
