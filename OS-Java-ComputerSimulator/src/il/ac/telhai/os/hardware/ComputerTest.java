package il.ac.telhai.os.hardware;

import static org.junit.Assert.*;

import org.junit.Test;

public class ComputerTest extends Computer {

	@Test
	public void test() throws InterruptedException {
		Computer c = new Computer(FREQUENCY, SEGMENT_SIZE, NUMBER_OF_SEGMENTS, NUMBER_OF_PAGES);
		Thread t = new Thread (c);
		t.start();
		Thread.sleep(5000);
		c.powerSwitch.powerOff();
		Thread.sleep(1000);
		assertEquals(		
				"\nCS=0	DS=0	SS=0	ES=0" +
				"\nAX=0	BX=0	CX=0	DX=0" +
				"\nSP=0	IP=0	SI=0	DI=0" +
				"\nBP=0	FL=2\t", c.cpu.getRegisters());
	}

}
