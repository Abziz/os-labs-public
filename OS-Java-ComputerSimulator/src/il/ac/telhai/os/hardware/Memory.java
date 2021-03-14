package il.ac.telhai.os.hardware;

public interface Memory {
	int getNumberOfSegments();
	int getSegmentSize();
	byte readByte (int segment, int offset);
	void writeByte (int segment, int offset, byte value);
	int readWord (int segment, int offset);
	void writeWord (int segment, int offset, int value);
}
