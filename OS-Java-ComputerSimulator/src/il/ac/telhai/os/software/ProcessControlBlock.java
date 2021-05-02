package il.ac.telhai.os.software;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.hardware.RealMemory;
import il.ac.telhai.os.software.language.Program;
import il.ac.telhai.os.software.language.Register;
import il.ac.telhai.os.software.language.Registers;

public class ProcessControlBlock {
	private static final Logger logger = Logger.getLogger(ProcessControlBlock.class);

	private static ProcessControlBlock root = null; // The first process created
	private static int lastId = 0;
	private static Map<Integer, ProcessControlBlock> idMap = new HashMap<Integer, ProcessControlBlock>();

	private ProcessControlBlock parent; // This warning should disappear when we implement getPid
	private Set<ProcessControlBlock> children = new HashSet<ProcessControlBlock>();

	private int id;
	private Program program;
	Registers registers;

	public ProcessControlBlock(ProcessControlBlock parent) {
		// Add to process tree
		if (parent != null) {
			parent.children.add(this);
		} else {
			if (root != null)
				throw new IllegalArgumentException("Only one root process allowed");
			root = this;
		}
		this.parent = parent;

		// Assign an id to process
		do {
			lastId++;
		} while (idMap.containsKey(lastId));
		this.id = lastId;

		// Add to the id Map
		idMap.put(this.id, this);

		if (parent != null) {
			this.program = parent.program;
			this.registers = new Registers(parent.registers);
		} else {
			this.registers = new Registers();
		}
	}

	private void setRegistersFor(Program program) {
		registers.set(Register.SS, 0);
		registers.set(Register.DS, 1);
		registers.set(Register.ES, 2);
		registers.set(Register.SP, program.getStackSize() + RealMemory.BYTES_PER_INT);
		registers.set(Register.IP, program.getEntryPoint());
	}

	public boolean exec(String fileName) {
		try {
			this.program = new Program(fileName);
		} catch (FileNotFoundException | ParseException e) {
			logger.error(fileName + e);
			return false;
		}
		setRegistersFor(program);
		return true;
	}

	public void exit(int status) {
		// TODO: assert (parent != null);
		root.children.addAll(children);
		for (ProcessControlBlock child : children) {
			child.parent = root;
		}
		children.clear();
		if (parent != null) // TODO: remove this check
			parent.children.remove(this);

		idMap.remove(id);

	}

	public ProcessControlBlock fork() {
		ProcessControlBlock child = new ProcessControlBlock(this);
		child.registers.set(Register.AX, 0);
		this.registers.set(Register.AX, child.getId());
		return child;
	}

	public void run(CPU cpu) {
		cpu.contextSwitch(program, registers);
		registers.setFlag(Registers.FLAG_USER_MODE, true);
	}

	public void getPid() {
		registers.set(Register.AX, id);
	}

	public void getPPid() {
		registers.set(Register.AX, parent == null ? -1 : parent.id);
	}

	public Program getProgram() {
		return program;
	}

	public int getId() {
		return id;
	}

	public ProcessControlBlock getParent() {
		return parent;
	}

	public static ProcessControlBlock getProcess(int id) {
		return idMap.get(id);
	}

	@Override
	public String toString() {
		return "Process [id=" + id + ", program=" + program.getFileName() + "]";
	}

}
