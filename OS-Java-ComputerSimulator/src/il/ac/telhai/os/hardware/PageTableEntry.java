package il.ac.telhai.os.hardware;

public class PageTableEntry {
	/*
	 * For each page we have three flag bits and at most 13 bits of disk address,
	 * for a total of 16 bits.
	 * On the other hand another limit on the number of pages is the size of the page/segment:
	 * We need two bytes per segment in the page table whose size is one page (by our design)
	 */
	private static int MAPPED_TO_MEMORY = 0x8000; 
	private static int MAPPED_TO_DISC   = 0x4000; 
	private static int COPY_ON_WRITE    = 0x2000; 
	private static int MODIFIED         = 0x1000;
	static int SEGMENT_MASK             = 0x0FFF;

	private int value;

	public PageTableEntry() {
		value = 0; // An unmapped address
	}

	public PageTableEntry(PageTableEntry original) {
		this.value = original.value;
	}

	/**
	 * Constructs the entry from its memory image
	 * @param data
	 */
	public PageTableEntry(byte[] data) {
		value = (data[0] << RealMemory.BITS_PER_BYTE) | data[1];
	}
	
	public byte[] getImage() {
		byte [] ret = new byte[2];
		ret[0] = (byte) (value >> RealMemory.BITS_PER_BYTE);
		ret[1] = (byte) value;
		return ret;
	}
	
	public boolean isMappedtoMemory() {
		return (value & MAPPED_TO_MEMORY) != 0;
	}
	
	public boolean isMappedtoDisc() {
		return (value & MAPPED_TO_DISC) != 0;
	}

	public boolean isModified() {
		return (value & MODIFIED) != 0;
	}
	
	public boolean isCopyOnWrite() {
		return (value & COPY_ON_WRITE) != 0;
	}

	public boolean isEndOfList() {
		return (value == 0xFFFF);
	}
	
	public int getSegmentNo() {
		return value & SEGMENT_MASK;
	}

	private void setFlag(int flagMask, boolean b) {
		if (b) {
			value |= flagMask;
		} else {
			value &= ~flagMask;
		}			
	}
	
	public void setMappedToMemory (boolean b) {
		setFlag(MAPPED_TO_MEMORY, b);
	}

	public void setMappedToDisc (boolean b) {
		setFlag(MAPPED_TO_DISC, b);
	}

	public void setModified (boolean b) {
		setFlag(MODIFIED, b);
	}

	public void setCopyOnWrite (boolean b) {
		setFlag(COPY_ON_WRITE, b);
	}

	public void setSegmentNo (int segmentNo) {
		value &= ~SEGMENT_MASK;
		value |= (segmentNo & SEGMENT_MASK);
	}
	
	public String toString() {
		if (value == 0) return "EMPTY";
		StringBuilder sb = new StringBuilder();
		sb.append("M=");
		sb.append(isMappedtoMemory() ? "1" : "0");
		sb.append(" D=");
		sb.append(isMappedtoDisc() ? "1" : "0");
		sb.append(" C=");
		sb.append(isCopyOnWrite() ? "1" : "0");
		sb.append(" MOD=");
		sb.append(isModified() ? "1" : "0");
		sb.append(" ");
		sb.append(getSegmentNo());
		return sb.toString();
	}

}
