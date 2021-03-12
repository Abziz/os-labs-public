package il.ac.telhai.os.software.language;

import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * @author cmshalom
 * Every line has an optional label followed by a colon a mnemonic code, and at most two operands
 */
public class AssemblerLine {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AssemblerLine.class);

	private static final String labelPattern = "(\\w*)\\s*:";
	private static final String mnemonicPatern = "\\s*([A-Z]+)\\s*?";
	private static final String operandPattern = "\\s*([^,/\\s][^,/]*)";
	private static final String commentPattern = "(//.*)?";
	private static final Pattern assemblerLine = 
			Pattern.compile(
					"\\s*("+labelPattern + ")?" + 
							mnemonicPatern + 
							"(" + operandPattern + "(,(" + operandPattern + "))?)?" +
							commentPattern , 
							Pattern.CASE_INSENSITIVE);
	private static final int LABEL_GROUP = 2;
	private static final int MNEMONIC_GROUP = 3;
	private static final int OP1_GROUP = 5;
	private static final int OP2_GROUP = 8;

	private String label;
	private Mnemonic mnemonic;
	private Operand op1;
	private Operand op2;

	public AssemblerLine (String commandLine, Map<String, Integer> symbolTable) throws ParseException {
		Matcher matcher = assemblerLine.matcher(commandLine);
		if (!matcher.matches()) throw new ParseException(commandLine, 0);

		label = matcher.group(LABEL_GROUP);
		try {
			mnemonic = Mnemonic.valueOf(matcher.group(MNEMONIC_GROUP).toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ParseException(matcher.group(MNEMONIC_GROUP)+":illegal Mnemonic", 0);
		}
		op1 = Operand.newOperand(matcher.group(OP1_GROUP), symbolTable);
		op2 = Operand.newOperand(matcher.group(OP2_GROUP), symbolTable);
		if (mnemonic.getParams() == 2 && op2 == null) throw new ParseException(mnemonic + ":needs two operands", 0);
		if (mnemonic.getParams() == 1 && (op1 == null || op2 != null)) throw new ParseException(mnemonic + ":needs one operands", 0);
		if (mnemonic.getParams() == 0 && (op1 != null || op2 != null)) throw new ParseException(mnemonic + ":needs no operands", 0); 
	}

	public String getLabel() {
		return label;
	}

	public Mnemonic getMnemonic() {
		return mnemonic;
	}

	public Operand getOp1() {
		return op1;
	}

	public Operand getOp2() {
		return op2;
	}
}
