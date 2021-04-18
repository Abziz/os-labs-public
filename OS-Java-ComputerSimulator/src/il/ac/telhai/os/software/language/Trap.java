package il.ac.telhai.os.software.language;

import il.ac.telhai.os.hardware.InterruptSource;

@SuppressWarnings("serial")
public abstract class Trap extends RuntimeException implements InterruptSource {

	@Override
	public int getPriority() {
		return 1;
	}

}
