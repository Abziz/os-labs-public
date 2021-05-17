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
		// TODO (previous lab): Initialize the list of free real memory segments

		logger.info("Real Memory Initialized");
	}
	public PageTableEntry[] clonePageTable(PageTableEntry[] pageTable) {
		// TODO: Clone a page table while setting the CopyOnWrite bits
		
		return null;
	}
	public void releasePageTable (PageTableEntry[] pageTable) {
		// TODO: free the segments mapped by the page table
		//		 - only mapped segments should be freed
		//		 - segments with CopyOnWrite bit shouldn't be freed
	}
	
	@Override
	public void handle(InterruptSource source) {
		PageFault fault = (PageFault) source;
		PageTableEntry entry = fault.getEntry();
		// TODO: handle page faults accordingly
		// 	- this should be printed if a new segment was allocated:
		//    logger.info("Allocating segment " + ???);
	}
	
	void shutdown( ) {
		logger.info("Free Memory: " + freeMemoryPages.size() + " pages.");
	}
}
