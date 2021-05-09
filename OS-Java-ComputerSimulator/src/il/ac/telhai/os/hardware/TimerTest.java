package il.ac.telhai.os.hardware;

import org.junit.Test;

public class TimerTest extends CPU {
	private static final int FREQUENCY = 10;
	private static final Clock clock = new Clock(FREQUENCY);  
	protected static final int SEGMENT_SIZE = 4096;
	protected static final int NUMBER_OF_PAGES = 1024;

	
	public TimerTest() {
		super(clock, new RealMemory(SEGMENT_SIZE, 1), NUMBER_OF_PAGES);
	}


	@Test
	public void test() {
		Timer t = new Timer(this, clock);
		t.setAlarm(10);
		clock.run();
	}
	
	public void interrupt (InterruptSource source) {
		clock.shutdown();
	}
	
	@Override
	public void tick() {  // So that the CPU does not really work
		
	}

}
