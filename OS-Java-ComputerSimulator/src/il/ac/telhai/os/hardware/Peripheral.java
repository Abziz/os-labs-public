package il.ac.telhai.os.hardware;

/**
 * 
 * @author cmhalom
 * A peripheral is an external device that can interrupt the CPU
 * It implements Clockeable so that it can it can operate
 * It implements InterruptSource so that it can send interrupts
 * It's constructor get as CPU parameter so that it knows where to 
 * send the interrupts
 */
public abstract class Peripheral implements Clockeable, InterruptSource {
	protected CPU cpu;
	
    public Peripheral (CPU cpu) {
    	this.cpu = cpu;
    }
}
