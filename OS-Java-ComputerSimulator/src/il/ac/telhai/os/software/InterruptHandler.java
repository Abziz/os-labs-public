package il.ac.telhai.os.software;

import il.ac.telhai.os.hardware.InterruptSource;

/**
 * An interrupt handler contains a single method that receives the interrupt
 * source to be used in case there may be several sources of the same type.
 * @author cmhalom
 *
 */
public interface InterruptHandler {
	void handle (InterruptSource source);
}
