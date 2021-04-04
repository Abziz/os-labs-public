package il.ac.telhai.os.hardware;

/**
 * 
 * @author cmshalom
 * Every interrupt source must implement this interface in order 
 * The possible interrupt sources are Peripherals (hardware/external interrupts),
 * System Calls (software interrupts) and Traps (not implemented)
 */
public interface InterruptSource {
    int getPriority();
}