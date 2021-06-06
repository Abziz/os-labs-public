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
		mmu.enterRealMode();
        for (int i=1; i<numberOfRealSegments; i++) {
            if (mmu.readByte(0, i) == 0) {
            	mmu.writeByte(0, i, (byte) 1); 
        		mmu.exitRealMode();
        		logger.trace("Allocating segment " + i);
        		return i;            	
            }
        }
        throw new IllegalStateException("Real Memory Full");
	}
	
	public PageTableEntry[] clonePageTable(PageTableEntry[] pageTable) {
		mmu.enterRealMode();
		PageTableEntry[] ret = new PageTableEntry[pageTable.length];
		for (int i = 0; i < pageTable.length; i++) {
			ret[i] = new PageTableEntry(pageTable[i]);
			if (ret[i].isMappedtoMemory() || ret[i].isMappedtoDisc()) {
				ret[i].setCopyOnWrite(true);			
				pageTable[i].setCopyOnWrite(true);
				byte refCount = mmu.readByte(0, ret[i].getSegmentNo());
				if (refCount==127) {
					throw new IllegalStateException("Reference count reached 127 in page sharing");
				}
        		logger.trace("Sharing segment " + ret[i].getSegmentNo() + ", refcnt=" + (refCount+1));
				mmu.writeByte(0, ret[i].getSegmentNo(), (byte) (refCount+1));
			}
		}
		mmu.exitRealMode();
		return ret;
	}
	
	public void releasePageTable (PageTableEntry[] pageTable) {
		// TODO: What happens if parent exits before children?
		//       or parent wants to write 
		//       Shared pages should not be released. 
		mmu.enterRealMode();
		for (int i=0; i<pageTable.length; i++) {
            PageTableEntry e = pageTable[i];
            pageTable[i] = null;
			if (e.isMappedtoMemory()) {
				byte refCount = mmu.readByte(0, e.getSegmentNo());
				mmu.writeByte(0, e.getSegmentNo(), (byte) (refCount-1));
				logger.trace("Releasing segment " + e.getSegmentNo() + " ,refcnt=" + (refCount-1));
			}
		}
		mmu.exitRealMode();
	}


	@Override
	public void handle(InterruptSource source) {
		PageFault fault = (PageFault) source;
		PageTableEntry entry = fault.getEntry();
		assert(entry != null);
		if (entry.isMappedtoMemory()) {
			mmu.enterRealMode();
			byte refCount = mmu.readByte(0, entry.getSegmentNo());
			if (refCount > 1) {  // There are at least one more process sharing the page
				mmu.writeByte(0, entry.getSegmentNo(), (byte) (refCount-1));
				int newSegment = getFreePage();  // Recall that getFreePage exits realMode
				logger.trace("Copying segment " + entry.getSegmentNo() + " to segment " + newSegment);
				mmu.copySegment(newSegment, entry.getSegmentNo());
				entry.setSegmentNo(newSegment);				
			}
			mmu.exitRealMode();
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
