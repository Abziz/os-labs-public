package il.ac.telhai.os.software.language;

/**
 * 
 * @author cmshalom
 * Every Mnemonic is either privileged or not and it has a fixed number of parameters
 */
public enum Mnemonic {
	EQU(1, false, 1),
	
	MOV(2, false, 2),
	INC(2, false, 1),
	DEC(2, false, 1), 
	CMP(2, false, 2), 
	ADD(2, false, 2), 
	SUB(2, false, 2), 
	MUL(2, false, 2), 
	NOP(2, false, 0),
	JMP(2, false, 1), 
	JZ(2, false, 1), 
	JNZ(2, false, 1),
	PUSH(2, false, 1),
	POP(2, false, 1),
	CALL(2, false, 1),
	RET(2, false, 0),
	HALT(2, true, 0), 
	USR(2, false, 0); // Set user mode 
		
	private int type;  // 1-Directive, 2-Instruction
	private boolean privileged;
	private int params;
	
	Mnemonic(int type, boolean privileged, int params) {
		this.type = type;
		this.privileged = privileged;
		this.params = params;
	}

	public int getType () {
		return type;
	}

	public boolean isPrivileged () {
		return privileged;
	}
	
	public int getParams() {
		return params;
	}
}
