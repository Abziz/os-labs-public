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

public class Process {
	private static final Logger logger = Logger.getLogger(Process.class);

	private static Process root = null;  // The first process created
	private static int lastId = 0;
	private static Map<Integer, Process> idMap = new HashMap<Integer, Process>();

	private Process parent; // This warning should disappear when we implement getPid
	private Set<Process> children = new HashSet<Process>();

	private int id;
	private Program program;
	Registers registers;

	public Process(Process parent) {
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
		} else {
			this.registers = new Registers();
		}
	}

	private void setRegistersFor(Program program) {
		registers.set(Register.SS, 0);
		registers.set(Register.DS, 1);
		registers.set(Register.ES, 2);
		registers.set(Register.SP, program.getStackSize()+RealMemory.BYTES_PER_INT);
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

	public Process fork() {
		Process child = new Process (this);
		child.registers.set(Register.AX, 0);
		this.registers.set(Register.AX, child.getId());
		return child;
	}
	
	void exit(int status) {
		root.children.addAll(children);
		for (Process child: children) {
			child.parent = root;
		}
		children = new HashSet<Process>();
	}

	public void run(CPU cpu) {
		// TODO: The parameter cpu is currently unused. 
		// It is useless if cpu will remain a static variable of Operating System	
		cpu.contextSwitch(program, registers);
		registers.setFlag(Registers.FLAG_USER_MODE, true);
	}

	public Program getProgram() {
		return program;
	}
	
	public int getId() {
		return id;
	}
	
	public Process getParent() {
		return parent;
	}

	public static Process getProcess(int id) {
		return idMap.get(id);
	}

	@Override
	public String toString() {
		return "Process [id=" + id + ", program=" + program.getFileName() + "]";
	}

}
