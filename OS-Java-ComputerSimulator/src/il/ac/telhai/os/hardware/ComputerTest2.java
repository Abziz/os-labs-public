package il.ac.telhai.os.hardware;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

public class ComputerTest2 extends Computer {

	@Test
	public void testFork() throws InterruptedException, IOException {
		Path src = Paths.get("init2.prg");
		Path dst = Paths.get("init.prg");
		Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
		Computer c = new Computer(FREQUENCY, SEGMENT_SIZE, NUMBER_OF_SEGMENTS, NUMBER_OF_PAGES);
		try {
    		c.run();
    		fail("Should get an exception");
		} catch (Exception e) {
			return;
		}
	}

}
