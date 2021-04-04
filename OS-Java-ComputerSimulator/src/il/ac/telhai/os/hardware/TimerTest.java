package il.ac.telhai.os.hardware;

import org.junit.Test;

public class TimerTest extends CPU {
	private static final int FREQUENCY = 10;
	private static final Clock clock = new Clock(FREQUENCY);  
	
	public TimerTest() {
		super(clock, new RealMemory(100, 1));
	}


	@Test
	public void test() {
		Timer t = new Timer(this, clock);
		t.set(10);
		clock.run();
	}
	
	public void interrupt (InterruptSource source) {
		clock.shutdown();
	}
	
	@Override
	public void tick() {  // So that the CPU does not really work
		
	}

}
