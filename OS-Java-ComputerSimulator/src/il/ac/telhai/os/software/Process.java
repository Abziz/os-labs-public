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
	private static int lastId = 0;       // The id of the last process created
	private static Map<Integer, Process> idMap = new HashMap<Integer, Process>();  // All the existing processes are here

	private Process parent;
	private Set<Process> children = new HashSet<Process>();

	private int id;
	private Program program;
	Registers registers;

	public Process(Process parent) {
		// TODO: Make sure that a) there is exactly one root (parent = null),
		//                      b) the parent points to all children,
		//                      b) Process inherits its registers from its parent
		//                      c) every new process is added to the idMap
		this.registers = new Registers();
	}

	private void setRegistersFor(Program program) {
		registers.set(Register.SS, 0);
		registers.set(Register.DS, 1);
		registers.set(Register.ES, 2);
		registers.set(Register.SP, program.getStackSize()+RealMemory.BYTES_PER_INT);
		registers.set(Register.IP, program.getEntryPoint());
	}

	// TODO: Write the fork function
	
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

	public void run(CPU cpu) {
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
		// TODO: Return the process with given id (if it exists, null otherwise)
		return null;
	}

	@Override
	public String toString() {
		return "Process [id=" + id + ", program=" + program.getFileName() + "]";
	}

}
