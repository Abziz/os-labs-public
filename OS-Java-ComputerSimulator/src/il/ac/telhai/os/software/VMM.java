package il.ac.telhai.os.software;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.InterruptSource;
import il.ac.telhai.os.hardware.MMU;
import il.ac.telhai.os.hardware.PageFault;
import il.ac.telhai.os.hardware.PageTableEntry;

public class VMM implements InterruptHandler {
	private static final Logger logger = Logger.getLogger(VMM.class);

	private MMU mmu;
	private int numberOfRealSegments;
	private Queue<Integer> freeMemoryPages = null;

	public VMM (MMU mmu) {
		this.mmu = mmu;
		numberOfRealSegments = mmu.getNumberOfSegments(); // This works since the mmu is initially in real mode
		initMemoryFreeList();
	}

	private void initMemoryFreeList() {
		logger.info("Initializing Real Memory");
		freeMemoryPages = new LinkedList<Integer>();
		for (int i=1; i < this.numberOfRealSegments; i++) {
			freeMemoryPages.add(i);
		}
		logger.info("Real Memory Initialized");
	}

	private int getFreePage() {
		int result = freeMemoryPages.remove();
		logger.info("Allocating segment " + result);
		return result;
	}
	
	public PageTableEntry[] clonePageTable(PageTableEntry[] pageTable) {
		PageTableEntry[] ret = new PageTableEntry[pageTable.length];
		for (int i = 0; i < pageTable.length; i++) {
			ret[i] = new PageTableEntry(pageTable[i]);
			if (ret[i].isMappedtoMemory() || ret[i].isMappedtoDisc()) {
				ret[i].setCopyOnWrite(true);			
				pageTable[i].setCopyOnWrite(true);
			}
		}
		return ret;
	}
	
	public void releasePageTable (PageTableEntry[] pageTable) {
		// TODO: (not for students) What happens if parent exits before children?
		//       Shared pages should not be released. 
		for (int i=0; i<pageTable.length; i++) {
            PageTableEntry e = pageTable[i];
            pageTable[i] = null;
			if (e.isMappedtoMemory() && !e.isCopyOnWrite()) {
				freeMemoryPages.add(e.getSegmentNo());
			}
		}
	}


	@Override
	public void handle(InterruptSource source) {
		PageFault fault = (PageFault) source;
		PageTableEntry entry = fault.getEntry();
		if (entry == null) {
			throw new SegmentationViolation();
		}
		if (entry.isMappedtoMemory()) {
			int newPage = getFreePage();
			mmu.copySegment(newPage, entry.getSegmentNo());
			entry.setSegmentNo(newPage);
			entry.setCopyOnWrite(false);
		} else {
			entry.setSegmentNo(getFreePage());
			entry.setMappedToMemory(true);			
		}
	}
	
	void shutdown( ) {
		logger.info("Free Memory: " + freeMemoryPages.size() + " pages.");
	}

}
