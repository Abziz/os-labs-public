package il.ac.telhai.os.software;

import java.io.FileNotFoundException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.hardware.RealMemory;
import il.ac.telhai.os.software.language.Program;
import il.ac.telhai.os.software.language.Register;
import il.ac.telhai.os.software.language.Registers;

public class Process {
	private static final Logger logger = Logger.getLogger(Process.class);

	static Process process = null;
	private static int lastId = 0;

	private int id;
    private Program program;
	Registers registers;

	public Process(Process parent) {
		if (process != null) throw new IllegalArgumentException("Only one process allowed");
		process = this;
        this.id = ++lastId;     
		this.registers = new Registers();
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
	
	@Override
	public String toString() {
		return "Process [id=" + id + ", program=" + program.getFileName() + "]";
	}	
}
