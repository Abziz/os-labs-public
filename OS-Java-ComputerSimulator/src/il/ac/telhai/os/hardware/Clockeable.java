package il.ac.telhai.os.hardware;

/**
 * 
 * @author cmshalom
 * Every device to get clock ticks must implement this interface
 * (and also made known to the corresponding clock device)
 */
public interface Clockeable {
	/**
	 * Informs the hardware about one clock tick elapsed
	 */
    void tick(); 
}
