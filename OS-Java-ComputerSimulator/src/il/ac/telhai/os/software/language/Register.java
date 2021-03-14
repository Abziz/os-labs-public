package il.ac.telhai.os.software.language;

public enum Register {
	CS (null), DS(null), SS(null), ES(null),
	AX (DS), BX(DS), CX(DS), DX(DS), 
	SP (SS), IP(CS), SI(ES), DI(ES), 
	BP (SS), FL(null);
	
	private Register segmentRegister;
	
	Register (Register segmentRegister) {
		this.segmentRegister = segmentRegister;
	}
	
	Register getSegmentRegister() {
		return segmentRegister;
	}

}
