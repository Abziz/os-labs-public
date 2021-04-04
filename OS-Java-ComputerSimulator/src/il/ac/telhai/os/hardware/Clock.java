package il.ac.telhai.os.hardware;


import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author cmshalom
 * This class simulates a clock with a given frequency
 * To every device known to it, it will send
 * approximately frequency ticks per second
 * as long as it is powered on
 * Devices are made known to it invoking the addDevice method
 */
public class Clock {
	private long period;
	private Set<Clockeable> devices;
	private boolean poweredOn;
	
	public Clock (double frequency) {
		period = (int) (1000 / frequency);
		devices = new HashSet<Clockeable>();
		poweredOn = true;
	}
	
	public void addDevice(Clockeable device) {
		devices.add(device);
	}
	
	/**
	 * The clock start to provide ticks to all devices until it is powered off
	 */
	public void run() {
		while(poweredOn) {
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				return;
			}
			for (Clockeable device : devices) {
				device.tick();
			}
		}
	}
	
	/**
	 * No ticks will be send after the period the clock is powered off.
	 * However all the devices will continue to get (at most 1) tick
	 * during the current period
	 */
	public void shutdown () {
		poweredOn = false;
	}

}
