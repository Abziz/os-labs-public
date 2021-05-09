package il.ac.telhai.os.software;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.InterruptSource;
import il.ac.telhai.os.hardware.MMU;
import il.ac.telhai.os.hardware.PageFault;
import il.ac.telhai.os.hardware.PageTableEntry;

public class VMM implements InterruptHandler {
	private static final Logger logger = Logger.getLogger(VMM.class);

	private MMU mmu;
	private int numberOfRealSegments;
	private LinkedList<Integer> freeMemoryPages;

	public VMM(MMU mmu) {
		this.mmu = mmu;
		numberOfRealSegments = mmu.getNumberOfSegments();
		initMemoryFreeList();
	}

	private void initMemoryFreeList() {
		logger.info("Initializing Real Memory");
		// TODO: Initialize the list of free real memory segments

		logger.info("Real Memory Initialized");
	}

	@Override
	public void handle(InterruptSource source) {
		PageFault fault = (PageFault) source;
		PageTableEntry entry = fault.getEntry();
		logger.info("Handling page fault on segment " + entry.getSegmentNo());
		// TODO: Get a free segment from the free list
		//       and modify the entry appropriately
		
		logger.info("Allocating segment " + entry.getSegmentNo());
	}
}
