package il.ac.telhai.os.software;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.hardware.PageTableEntry;
import il.ac.telhai.os.hardware.RealMemory;
import il.ac.telhai.os.software.language.Operand;
import il.ac.telhai.os.software.language.Program;
import il.ac.telhai.os.software.language.Register;
import il.ac.telhai.os.software.language.Registers;

public class ProcessControlBlock {
	private static final Logger logger = Logger.getLogger(ProcessControlBlock.class);

	private static ProcessControlBlock root = null;  // The first process created
	private static int lastId = 0;
	private static Map<Integer, ProcessControlBlock> idMap = new HashMap<Integer, ProcessControlBlock>();

	private ProcessControlBlock parent; // This warning should disappear when we implement getPid
	private Set<ProcessControlBlock> children = new HashSet<ProcessControlBlock>();

	private int id;
	private Program program;
	Registers registers;
	private PageTableEntry[]  pageTable;
	private Signaller signaller;
	boolean terminated = false;

	public ProcessControlBlock(ProcessControlBlock parent) {
		// Add to process tree
		if (parent != null) {
			parent.children.add(this);
		} else {
			if (root != null) throw new IllegalArgumentException("Only one root process allowed");
			root = this;
		}
		this.parent = parent;

		//                  Assign an id to process
		do {
			lastId++;
		} while (idMap.containsKey(lastId));
		this.id = lastId;

		//                  Add to the id Map
		idMap.put(this.id, this);

		if (parent != null) {
			this.program = parent.program;
			this.registers = new Registers(parent.registers);
			this.pageTable = OperatingSystem.getInstance().vmm.clonePageTable(parent.pageTable);			
		} else {
			this.registers = new Registers();
		}
		signaller = new Signaller(this);
	}

	private void setRegistersFor(Program program) {
		registers.set(Register.SS, 0);
		registers.set(Register.DS, 1);
		registers.set(Register.ES, 2);
		registers.set(Register.SP, program.getStackSize()+RealMemory.BYTES_PER_INT);
		registers.set(Register.IP, program.getEntryPoint());
	}

	private void setPageTableFor(Program program) {
		// Create a page table with 1 + program.getDataSegments() segments
		// none of which is mapped
		// Segment zero is the stack segment (see register settings above)
		pageTable = new PageTableEntry[program.getDataSegments()+2];
		for (int i = 0; i < pageTable.length; i++) {
			pageTable[i] = new PageTableEntry();
		}
	}

	public boolean exec(String fileName) {
		if (pageTable != null) {
			OperatingSystem.getInstance().vmm.releasePageTable (pageTable);
		}
		try {
			this.program = new Program(fileName);
		} catch (FileNotFoundException | ParseException e) {
			logger.error(fileName + e);
			return false;
		}
		setRegistersFor(program);
		setPageTableFor(program);
		return true;
	}

	public void exit(int status) {
		assert (parent != null);
		root.children.addAll(children);
		for (ProcessControlBlock child: children) {
			child.parent = root;
		}
		children.clear();
		parent.signaller.kill(Signal.SIGCHLD);
		parent.children.remove(this);

		idMap.remove(id);

		OperatingSystem.getInstance().vmm.releasePageTable (pageTable);
		pageTable = null;
		signaller = null;
		terminated = true;
	}


	public ProcessControlBlock fork() {
		ProcessControlBlock child = new ProcessControlBlock (this);
		child.registers.set(Register.AX, 0);
		this.registers.set(Register.AX, child.getId());
		return child;
	}

	public void run(CPU cpu) {
		cpu.contextSwitch(program, registers);
		cpu.setPageTable(pageTable);
		registers.setFlag(Registers.FLAG_USER_MODE, true);
		signaller.handleSignals();
	}

	public void signal (int signum, int handler) {
		try {
			signaller.setHandler(signum, handler);
			registers.set(Register.AX, 0);
		} catch (Exception e) {
			registers.set(Register.AX, -1);			
		}
	}
	
	public void kill (int pid, int signum) {
		ProcessControlBlock receivingProcess = ProcessControlBlock.getProcess(pid);
		if (receivingProcess == null) {
			registers.set(Register.AX, -1);			
		} else {
			try {
				receivingProcess.getSignaller().kill(signum);						
				registers.set(Register.AX, 0);						
			} catch (Exception e) {				
				registers.set(Register.AX, -1);						
			}
		}
	}

	public void getPid() {
		registers.set(Register.AX, id);		
	}

	public void getPPid() {
		registers.set(Register.AX, parent==null?-1:parent.id);				
	}

	public int getWord(Operand op) {
		return op.getWord(registers, OperatingSystem.getInstance().cpu);
	}

	public int getByte(Operand op) {
		return op.getByte(registers, OperatingSystem.getInstance().cpu);
	}

	public String getString(Operand op) {
		return op.getString(registers, OperatingSystem.getInstance().cpu);
	}


	public Program getProgram() {
		return program;
	}

	Signaller getSignaller() {
		return signaller;
	}

	public int getId() {
		return id;
	}

	public boolean isTerminated() {
		return terminated;
	}
	
	public ProcessControlBlock getParent() {
		return parent;
	}

	public static ProcessControlBlock getProcess(int id) {
		return idMap.get(id);
	}

	public static void shutdown() {
		logger.info("Active Processes");
		for (ProcessControlBlock p : idMap.values()) {
			logger.info(p);
			logger.trace("Page Table");
			for (PageTableEntry e : p.pageTable) {
				logger.trace(e);
			}
			logger.trace("");
		}
	}

	@Override
	public String toString() {
		String parentStr = (parent == null) ? "" : ",parent = " + parent.id; 
		return "Process [id=" + id + parentStr +  ", program=" + program.getFileName() + "]";
	}


}
