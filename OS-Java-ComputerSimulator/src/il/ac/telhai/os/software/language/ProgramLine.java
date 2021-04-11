package il.ac.telhai.os.software.language;

import il.ac.telhai.os.hardware.Memory;

public interface ProgramLine {
	void execute (Registers registers, Memory memory);
}
