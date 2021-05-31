package il.ac.telhai.os.software;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.InterruptSource;
import il.ac.telhai.os.hardware.MMU;
import il.ac.telhai.os.hardware.PageFault;
import il.ac.telhai.os.hardware.PageTableEntry;

public class VMM implements InterruptHandler {
	private static final Logger logger = Logger.getLogger(VMM.class);

	private MMU mmu;
	private int numberOfRealSegments;

	public VMM (MMU mmu) {
		this.mmu = mmu;
		numberOfRealSegments = mmu.getNumberOfSegments(); // This works since the mmu is initially in real mode
		if (mmu.getSegmentSize() < numberOfRealSegments) {
			throw new IllegalStateException("Free bitmap cannot be stored in one page");
		}
		initMemoryFreeList();
	}

	private void initMemoryFreeList() {
		logger.info("Initializing Real Memory");
		mmu.enterRealMode();
		mmu.writeByte(0, 0, (byte) 1); // Page 0 is in use
		for (int i=1; i< numberOfRealSegments; i++) {
			mmu.writeByte(0, i, (byte) 0); 
		}
		mmu.exitRealMode();
		logger.info("Real Memory Initialized");
	}

	private int getFreePage() {
		//        		logger.trace("Allocating segment " + i);
	}

	public PageTableEntry[] clonePageTable(PageTableEntry[] pageTable) {
		//        		logger.trace("Sharing segment " + ret[i].getSegmentNo() + ", refcnt=" + (refCount+1));
	}

	public void releasePageTable (PageTableEntry[] pageTable) {
		//				logger.trace("Releasing segment " + e.getSegmentNo() + " ,refcnt=" + (refCount-1));
	}


	@Override
	public void handle(InterruptSource source) {
		PageFault fault = (PageFault) source;
		PageTableEntry entry = fault.getEntry();
		assert(entry != null);
		if (entry.isMappedtoMemory()) {
			int newSegment = getFreePage();  // Recall that getFreePage exits realMode
			logger.trace("Copying segment " + entry.getSegmentNo() + " to segment " + newSegment);
			mmu.copySegment(newSegment, entry.getSegmentNo());
			entry.setSegmentNo(newSegment);				
			entry.setCopyOnWrite(false);
		} else {
			entry.setSegmentNo(getFreePage());
			entry.setMappedToMemory(true);			
		}
	}

	void shutdown( ) {
		mmu.enterRealMode();
		int numberOfFreePages = 0;
		for (int i=1; i<numberOfRealSegments; i++) {
			if (mmu.readByte(0, i) == 0) numberOfFreePages++;
		}
		mmu.exitRealMode();
		logger.info("Free Memory: " + numberOfFreePages + " pages.");
	}

}