package il.ac.telhai.os.hardware;

import org.apache.log4j.Logger;

/**
 * This is an implementation of segmented memory, where all the segments are disjoint.
 * As usual, the basic addressable unit is a byte
 * Bytes can be accesses via the readByte and writeByte methods 
 * Double Words also can be accessed as one unit
 * via the readInt and writeInt methods
 * @author Mordo Shalom
 *
 */
public class RealMemory implements Memory {
	private static Logger logger = Logger.getLogger(RealMemory.class);
	public static final int BYTES_PER_INT = 4;
	public static final int BITS_PER_BYTE = 8;
	private final int segmentSize;
	private final int numberOfSegments; 

	private byte[][] memory;
	
	public RealMemory (int segmentSize, int numberOfSegments) {
		this.segmentSize = segmentSize;
		this.numberOfSegments = numberOfSegments;
		memory = new byte[numberOfSegments][];
		for (int i =0 ; i < numberOfSegments; i++) {
			memory[i] = new byte[segmentSize];
		}
	}
	
	@Override
	public int getSegmentSize() {
		return segmentSize;
	}

	@Override
	public int getNumberOfSegments() {
		return numberOfSegments;
	}
	
	private byte[] getSegment(int segment) {
		assert(segment >= 0 && segment < numberOfSegments);
		return memory[segment];
	}
	public byte readByte (int segment, int offset) {
		assert(offset >= 0 && offset < segmentSize);
		return getSegment(segment)[offset];
	}
	
	public void writeByte (int segment, int offset, byte value) {
		logger.trace("BYTE " + segment + ":" + offset + "=" + value);
		assert(offset >= 0 && offset < segmentSize);
		getSegment(segment)[offset] = value;
	}
	
	public int readWord (int segment, int offset) {
		assert(offset >= 0 && offset <= segmentSize - BYTES_PER_INT);
		assert(offset % BYTES_PER_INT == 0);
		byte[] mySegment = getSegment(segment);
		
		int result = 0;
		for (int i=offset; i < offset + BYTES_PER_INT;i++) {
			result <<= BITS_PER_BYTE;
		    result += mySegment[i] & 0xff;
		}
		return result;
	}
	
	public void writeWord (int segment, int offset, int value) {
		logger.trace("WORD " + segment + ":" + offset + "=" + value);
		assert(offset >= 0 && offset <= segmentSize - BYTES_PER_INT);
		assert(offset % BYTES_PER_INT == 0);
		byte[] mySegment = getSegment(segment);
		for (int i = offset + BYTES_PER_INT -1 ; i >= offset; i--) {
		    mySegment[i] = (byte) (value & 0xff);
			value >>= BITS_PER_BYTE;
		}
	}

	public void dma(int destinationSegment, int sourceSegment, int offset, int length) {
        byte[] src = getSegment(sourceSegment);
		byte[] dst = getSegment(destinationSegment);
		System.arraycopy(src, offset, dst, offset, length);
	}

	public void dma(int destinationSegment, int sourceSegment) {
		dma(destinationSegment, sourceSegment, 0, segmentSize);
	}

	public String dump(int segment) {
		// TODO: (Not for students) Make it like od
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i< segmentSize; i+=BYTES_PER_INT) {
			int value = this.readWord(segment, i);
			if (value != 0) {
				if (sb.length() == 0) {
					sb.append("Dump of Segment:" + segment + "\n");					
				}
				sb.append(i + ":" + value + "\n");
			}
		}
		return sb.toString();
	}

}
