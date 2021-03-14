package il.ac.telhai.os.hardware;

import static org.junit.Assert.*;

import org.junit.Test;

public class RealMemoryTest {
	RealMemory memory = new RealMemory(128, 128);

	@Test
	public void testWriteByte() {
		memory.writeByte(2, 3, (byte) 25);
		assertEquals(25, memory.readByte(2, 3));
	}

	@Test
	public void testWriteInt() {
		memory.writeWord(2, 12, 2500);
		assertEquals(2500, memory.readWord(2, 12));
		assertEquals(0, memory.readByte(2, 12));
		assertEquals(0, memory.readByte(2, 13));
		assertEquals(9, memory.readByte(2, 14));
		assertEquals(-60, memory.readByte(2, 15));
	}
	
	@Test
	public void testdma() {
		memory.writeWord(2, 12, 2500);
		memory.dma(3, 2, 4, 100);
		assertEquals(2500, memory.readWord(3, 12));
	}

}
