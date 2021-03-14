package il.ac.telhai.os.software.language;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Program {
	private String fileName;
	private int entryPoint;
	private int stackSize;
	private int numberOfDataSegments;
	private List<Instruction> lines = new ArrayList<Instruction>();

	public Program(String fileName) throws FileNotFoundException, ParseException {
		this.fileName = fileName;
		Map<String, Integer> symbolTable = readSymbols(fileName);
		Scanner sc = new Scanner( new FileReader(fileName));
		while (sc.hasNext()) {
			String s = sc.nextLine().trim();
			if (!s.equals("")) { // Skip empty lines
				AssemblerLine line = new AssemblerLine(s, symbolTable);
				switch (line.getMnemonic().getType()) {
				case 1:  // Directives
					break;
				case 2:
					lines.add(new Instruction (line));
					break;
				default:
					throw new RuntimeException("Invalid Mnemonic Type for " + line.getMnemonic() + ". Check Mnemonic.java");
				}
			}
		}
		sc.close();
	}

	/**
	 * Pass 1
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	private Map<String, Integer> readSymbols(String fileName) throws FileNotFoundException, ParseException {
		Map<String, Integer> symbolTable = new HashMap<String, Integer>();
		Scanner sc = new Scanner( new FileReader(fileName));
		int lineNo = 0;
		while (sc.hasNext()) {
			String s = sc.nextLine().trim();
			if (!s.equals("")) { // Skip empty lines
				AssemblerLine line = new AssemblerLine(s, null);
				int value;
				if (line.getMnemonic().getType() == 1) {
					switch (line.getMnemonic()) {
					case EQU:
						if (line.getOp1() instanceof AbsoluteOperand) {
							value = ((AbsoluteOperand) line.getOp1()).getWord(null, null);
						} else {
							throw new ParseException("EQU needs absolute operand", 0);
						}
						break;
					default:
						throw new ParseException("Unknown directive: " + line.getMnemonic(), 0);
					}
				} else {
					value = lineNo ++;
				}
				if (line.getLabel() != null) symbolTable.put(line.getLabel().toUpperCase(), value);
			}
		}
		sc.close();
		try {
			entryPoint = symbolTable.get("MAIN");
			stackSize = symbolTable.get("STACK_SIZE");
			numberOfDataSegments = symbolTable.get("DATA_SEGMENTS");
		} catch (Exception e) {
			throw new RuntimeException("MAIN, STACK_SIZE or DATA_SEGMENTS undefined");
		}
		return symbolTable;    	
	}

	public Instruction fetchLine(Registers r) {
		Instruction ret;
		ret =  lines.get(r.get(Register.IP));
		r.add(Register.IP, 1);
		return ret;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<lines.size(); i++) {
			sb.append(i+":\t");
			sb.append(lines.get(i));
			sb.append("\n");
		}
		return sb.toString();
	}

	public String getFileName() {
		return fileName;
	}
	
	public int getEntryPoint() {
		return entryPoint;
	}

	public int getStackSize() {
		return stackSize;
	}
	
	public int getDataSegments() {
		return numberOfDataSegments;
	}
}
