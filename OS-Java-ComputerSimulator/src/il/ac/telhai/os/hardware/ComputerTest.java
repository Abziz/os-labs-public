package il.ac.telhai.os.hardware;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import il.ac.telhai.os.software.Process;

public class ComputerTest extends Computer {

	@Test
	public void testShutdown() throws InterruptedException, IOException {
		Computer c = new Computer(FREQUENCY, SEGMENT_SIZE, NUMBER_OF_SEGMENTS, NUMBER_OF_PAGES);
		Thread t = new Thread (c);
		t.start();
		Thread.sleep(4000);
		assertEquals(		
				"\nCS=0	DS=1	SS=0	ES=2" +
				"\nAX=4	BX=0	CX=5	DX=0" +
				"\nSP=92	IP=5	SI=0	DI=0" +
				"\nBP=0	FL=2\t", c.cpu.getRegisters());
		assertNull(Process.getProcess(1).getParent());
		assertEquals(1,Process.getProcess(2).getParent().getId());
		assertEquals(1,Process.getProcess(3).getParent().getId());
		assertEquals(1,Process.getProcess(4).getParent().getId());
		assertNull(Process.getProcess(5));
	}

}
