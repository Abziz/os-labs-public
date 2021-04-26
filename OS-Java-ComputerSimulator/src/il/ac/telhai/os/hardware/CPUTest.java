package il.ac.telhai.os.hardware;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import il.ac.telhai.os.software.language.Program;

public class CPUTest {
	private static final int FREQUENCY = 100;
	private static final int SEGMENT_SIZE = 4096;
	private static final int NUMBER_OF_SEGMENTS = 128;

	private Clock clock;
	private CPU cpu;
	private RealMemory memory;

	
	@Before
	public void initComputer () throws FileNotFoundException, ParseException {
		clock = new Clock(FREQUENCY);
		memory = new RealMemory(SEGMENT_SIZE, NUMBER_OF_SEGMENTS);
		cpu = new CPU(clock, memory);

	}


	@Test
	public void testTick() throws IOException, ParseException {
		Program program = new Program("cputest.prg");
		cpu.contextSwitch(program, null);
		assertEquals("\nCS=0\tDS=0\tSS=0\tES=0" +
				     "\nAX=0\tBX=0\tCX=0\tDX=0" + 
				     "\nSP=0\tIP=0\tSI=0\tDI=0" + 
				     "\nBP=0\tFL=0\t", 
				     cpu.getRegisters());
		cpu.tick();
		assertEquals("\nCS=0\tDS=0\tSS=0\tES=0" +
			     "\nAX=0\tBX=0\tCX=5\tDX=0" + 
			     "\nSP=0\tIP=1\tSI=0\tDI=0" + 
			     "\nBP=0\tFL=0\t", 
			     cpu.getRegisters());
		cpu.tick();
		assertEquals("\nCS=0\tDS=0\tSS=0\tES=0" +
			     "\nAX=0\tBX=0\tCX=5\tDX=0" + 
			     "\nSP=100\tIP=2\tSI=0\tDI=0" + 
			     "\nBP=0\tFL=0\t", 
			     cpu.getRegisters());
		for (int i = 0; i<1000; i++) cpu.tick();
		assertEquals("\nCS=0\tDS=0\tSS=0\tES=0" +
			     "\nAX=120\tBX=0\tCX=0\tDX=0" + 
			     "\nSP=92\tIP=11\tSI=0\tDI=0" + 
			     "\nBP=0\tFL=2\t", 
			     cpu.getRegisters());
		assertEquals("Dump of Segment:0\n" +
				     "92:20\n" +
				     "96:10\n" +
				     "100:5\n" +
				     "104:4\n" +
				     "108:3\n" +
				     "112:2\n" +
				     "116:1\n", 
				     memory.dump(0));
		for (int seg = 1; seg < NUMBER_OF_SEGMENTS; seg++) {
		    assertEquals("", memory.dump(seg));
		}
	}
}
