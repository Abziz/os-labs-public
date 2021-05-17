package il.ac.telhai.os.hardware;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import il.ac.telhai.os.software.ProcessControlBlock;

public class ComputerTest extends Computer {

	@Test
	public void testShutdown() throws InterruptedException, IOException {
		Computer c = new Computer(FREQUENCY, SEGMENT_SIZE, NUMBER_OF_SEGMENTS, NUMBER_OF_PAGES);
		c.run();
		assertEquals(		
				"\nCS=0	DS=1	SS=0	ES=2" +
				"\nAX=7	BX=0	CX=0	DX=4" +
				"\nSP=56	IP=36	SI=0	DI=0" +
				"\nBP=0	FL=2\t", c.cpu.getRegisters());
		assertEquals(1, ProcessControlBlock.getProcess(1).getId());
		assertNull(ProcessControlBlock.getProcess(2));
		assertNull(ProcessControlBlock.getProcess(3));
		assertEquals(1, ProcessControlBlock.getProcess(4).getParent().getId());
	}

}
