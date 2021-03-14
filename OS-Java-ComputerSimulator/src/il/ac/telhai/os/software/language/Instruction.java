package il.ac.telhai.os.software.language;

import java.text.ParseException;

import il.ac.telhai.os.hardware.Memory;
import il.ac.telhai.os.hardware.RealMemory;

import org.apache.log4j.Logger;

/**
 * 
 * @author cmshalom
 * Every instruction has a mnemonic code, and at most two operands
 */
public class Instruction {
	private static final Logger logger = Logger.getLogger(Instruction.class);
	
	private Mnemonic mnemonicCode;
	private InstructionOperand op1, op2;

	public Instruction(AssemblerLine line) throws ParseException {
		mnemonicCode = line.getMnemonic();
		op1 = (InstructionOperand) line.getOp1();			
		op2 = (InstructionOperand) line.getOp2();
	}

	public Instruction(String commandLine) throws ParseException {
		this(new AssemblerLine(commandLine, null));		
	}

	public void execute (Registers registers, Memory memory) {
		int result;
		
		logger.info(this);
		
		if (mnemonicCode.isPrivileged() && registers.getFlag(Registers.FLAG_USER_MODE)) {
			throw new RuntimeException(this + ": cannot execute in user mode" + registers);
		}		

		int offset = 0;
		switch (mnemonicCode) {
		case MOV:
			result = op2.getWord(registers, memory);
			op1.setWord(registers, memory, result);
			break;
		case ADD:
			result = op1.getWord(registers, memory) + op2.getWord(registers, memory);
			op1.setWord(registers, memory, result);
			break;
		case SUB:
			result = op1.getWord(registers, memory) - op2.getWord(registers, memory);
			op1.setWord(registers, memory, result);
			break;
		case MUL:
			result = op1.getWord(registers, memory) * op2.getWord(registers, memory);
			op1.setWord(registers, memory, result);
			break;
		case CMP:
			result = op1.getWord(registers, memory) - op2.getWord(registers, memory);
			break;
		case INC: 
			result = op1.getWord(registers, memory)+1;
			op1.setWord(registers, memory, result);
			break;
		case DEC: 
			result = op1.getWord(registers, memory)-1;
			op1.setWord(registers, memory, result);
			break;
		case NOP:
			result = 1; // reset zero flag
			break;
		case USR:
			result = 1; // reset zero flag
			registers.setFlag(Registers.FLAG_USER_MODE, true);
			break;
		case HALT:
			result = 1; // reset zero flag
			registers.setFlag(Registers.FLAG_HALTED, true);
			break;
		case JMP: 
			result = op1.getWord(registers, memory);
			registers.set(Register.IP, result); 
			break;
		case JZ:
			result = op1.getWord(registers, memory);
			if (registers.getFlag(Registers.FLAG_ZERO))  registers.set(Register.IP, result); 
			break;
		case JNZ: 
			result = op1.getWord(registers, memory);
			if (!registers.getFlag(Registers.FLAG_ZERO))  registers.set(Register.IP, result); 
			break;
		case PUSH:
			// TODO: Add stack Overflow here
			result = op1.getWord(registers, memory);
			offset  = registers.get(Register.SP)-RealMemory.BYTES_PER_INT;
			memory.writeWord(registers.get(Register.SS), offset, result);
			registers.set(Register.SP, offset);
			break;
		case POP:
			result = memory.readWord(registers.get(Register.SS), registers.get(Register.SP));
			op1.setWord(registers, memory, result);
			registers.add(Register.SP, RealMemory.BYTES_PER_INT);
			break;
		case CALL:
			// TODO: Add stack overflow here
			result = op1.getWord(registers, memory);
			offset  = registers.get(Register.SP)-RealMemory.BYTES_PER_INT;
			memory.writeWord(registers.get(Register.SS), offset, registers.get(Register.IP));
			registers.set(Register.SP, offset);
			registers.set(Register.IP, result);
			break;
		case RET:
			result = memory.readWord(registers.get(Register.SS), registers.get(Register.SP));
			registers.add(Register.SP, RealMemory.BYTES_PER_INT);
			registers.set(Register.IP, result);
			break;
		default:
			throw new IllegalArgumentException("Unexpected Mnemonic:" + mnemonicCode);
		}
		registers.setFlag(Registers.FLAG_ZERO, result == 0);
	}

	@Override
	public String toString() {
		return mnemonicCode + "\t" + (op1==null ? "" : op1 + 
				                     (op2==null ? "" : "," + op2));
	}

}