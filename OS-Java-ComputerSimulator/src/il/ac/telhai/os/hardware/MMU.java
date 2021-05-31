package il.ac.telhai.os.hardware;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.Memory;
import il.ac.telhai.os.hardware.RealMemory;
import il.ac.telhai.os.software.SegmentationViolation;

/**
 * 
 * @author cmshalom
 * This is a partial implementation of an MMU
 * It has a manual write-through cache of size of one segment it
 *
 */
public class MMU implements Memory {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MMU.class);

	private RealMemory memory;
	private PageTableEntry[] oldPageTable;
	private PageTableEntry[] pageTable=null; 

	public MMU (RealMemory memory, int numberOfPages) {
		if (memory.getSegmentSize() < 2 * numberOfPages) {
			throw new IllegalArgumentException("Page table does not fit to one page");
		}
		this.memory = memory;
	}

	/**
	 * Note that it is idempotent
	 */
	public void enterRealMode () {
		if (pageTable != null) {
			oldPageTable = pageTable;
			pageTable = null;					
		}
	}

	/**
	 * Note that it is idempotent
	 */
	public void exitRealMode () {
		pageTable = oldPageTable;		
	}

	public void setPageTable (PageTableEntry[] pageTable) {
		this.pageTable = pageTable; 		
	}
	
	public PageTableEntry[] getPageTable() {
		return pageTable;
	}
	
	@Override
	public int getSegmentSize() {
		return memory.getSegmentSize();
	}

	@Override
	public int getNumberOfSegments() {
		return pageTable == null ? memory.getNumberOfSegments() : pageTable.length;
	}
	
	public void copySegment(int destinationSegment, int sourceSegment) {
		memory.dma(destinationSegment, sourceSegment);
	}
	
	private int xlateSegmentNo(int pageNo, boolean isAWrite) {
		if (pageTable == null) return pageNo;
		PageTableEntry entry = (pageNo >= 0 && pageNo < pageTable.length) ? pageTable[pageNo] : null;
		if (entry == null) throw new SegmentationViolation();
		if (!entry.isMappedtoMemory()) throw new PageFault(entry);
		if (entry.isCopyOnWrite() && isAWrite) throw new PageFault(entry);
		return entry.getSegmentNo();
	}
	
	@Override
	public byte readByte(int pageNo, int offset) {
		int segmentNo = xlateSegmentNo(pageNo, false);
		return memory.readByte(segmentNo, offset);
	}

	@Override
	public void writeByte(int pageNo, int offset, byte value) {
		int segmentNo = xlateSegmentNo(pageNo, true);
		memory.writeByte(segmentNo, offset, value);
	}

	@Override
	public int readWord(int pageNo, int offset) {
		int segmentNo = xlateSegmentNo(pageNo, false);
		return memory.readWord(segmentNo, offset);			
	}

	@Override
	public void writeWord(int pageNo, int offset, int value) {
		int segmentNo = xlateSegmentNo(pageNo, true);
		memory.writeWord(segmentNo, offset, value);
	}
	
}
