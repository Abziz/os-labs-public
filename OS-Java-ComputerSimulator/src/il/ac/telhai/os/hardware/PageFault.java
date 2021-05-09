package il.ac.telhai.os.hardware;

import il.ac.telhai.os.software.language.Trap;

@SuppressWarnings("serial")
public class PageFault extends Trap {
	
	private PageTableEntry entry;
	
	public PageFault(PageTableEntry entry) {
		this.entry = entry;
	}
    
	public PageTableEntry getEntry() {
		return entry;
	}

	@Override
	public int getPriority() {
		return 1;
	}

}
