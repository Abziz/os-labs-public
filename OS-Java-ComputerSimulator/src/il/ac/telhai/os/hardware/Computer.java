package il.ac.telhai.os.hardware;

import il.ac.telhai.os.software.*;

import java.awt.Container;
import java.awt.HeadlessException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.junit.Test;

/**
 * 
 * @author cmhalom
 * A computer with a CPU, Timer and a Power switch
 * Running the MockOS operating system when powered on
 */
@SuppressWarnings("serial")
public class Computer implements Runnable {
	protected static final int FREQUENCY = 100;
	protected static final int SEGMENT_SIZE = 4096;
	protected static final int NUMBER_OF_SEGMENTS = 128;
	protected static final int NUMBER_OF_PAGES = 1024;

	protected Clock clock;
	protected CPU cpu;
	protected RealMemory memory;
	protected Timer timer;
	protected PowerSwitch powerSwitch;
	protected Set<Peripheral> peripherals = new HashSet<Peripheral>();

	private JFrame frame = null;
	private Container container = null;

	public Computer () {
		
	}
	
	public Computer (double frequency, int segmentSize, int numberOfSegments, int numberOfPages) {
		try {
			frame = new JFrame("Computer Simulator");
			frame.setSize(600, 400);
			frame.setLocation(0, 0);
			container = frame.getContentPane();
		} catch (HeadlessException e) {
			frame = null;
			container = null;
		}

		clock = new Clock(frequency);
		memory = new RealMemory(segmentSize, numberOfSegments);
		cpu = new CPU(clock, memory);
		timer = new Timer(cpu, clock);
		powerSwitch = new PowerSwitch(cpu, container);
		peripherals.add(timer);
		peripherals.add(powerSwitch);

		if (frame != null) {
			frame.pack();
			frame.setVisible(true);			
		}
	}

	public void powerOn () {
		// This design is not flexible, in the sense that 
		// we cannot "boot" this computer with another operating system
		OperatingSystem os = new OperatingSystem (cpu, peripherals);
		cpu.contextSwitch(os);
		clock.run();
		for (int seg = 0; seg < memory.getNumberOfSegments(); seg++) {
			System.out.print(memory.dump(seg));			
		}
	}


	/**
	 * Creates a Computer with a FREQUENCY HZ clock
	 * and powers it on.
	 * @param args
	 */ 
	public static void main(String[] args) {

		Computer c = new Computer(FREQUENCY, SEGMENT_SIZE, NUMBER_OF_SEGMENTS, NUMBER_OF_PAGES);
		c.powerOn();
		System.exit(0); // So that all frames are closed		
	}
	
	@Override
	public void run() {
		powerOn();
	}

}