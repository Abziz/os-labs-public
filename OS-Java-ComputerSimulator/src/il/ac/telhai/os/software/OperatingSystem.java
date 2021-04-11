package il.ac.telhai.os.software;

import java.util.Set;
import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.CPU;
import il.ac.telhai.os.hardware.InterruptSource;
import il.ac.telhai.os.hardware.Peripheral;
import il.ac.telhai.os.hardware.PowerSwitch;
import il.ac.telhai.os.software.language.Instruction;

public class OperatingSystem implements Software {
	private static final Logger logger = Logger.getLogger(OperatingSystem.class);

	private static OperatingSystem instance = null;
	CPU cpu;
	private Set<Peripheral> peripherals;
	private boolean initialized = false;

	public OperatingSystem (CPU cpu, Set<Peripheral> peripherals) {
		if (instance != null) {
			throw new IllegalStateException("Operating System is a singleton");
		}
		instance = this;
		this.cpu = cpu;
		this.peripherals = peripherals;
	}

	public static OperatingSystem getInstance() {
		return instance;
	}

	public void step() {
		if (!initialized) {
			initialize();
		} else {
			logger.info( "Idle, nothing to do....");
		}
	}
		
	private void initialize() {
		installHandlers();
		initialized = true;
	}

	private void installHandlers() {
		for (Peripheral p : peripherals) {
			if (p instanceof PowerSwitch) {
				cpu.setInterruptHandler(p.getClass(), new PowerSwitchInterruptHandler());
			}
		}
	}


	private void shutdown() {
		logger.info( "System going for shutdown");
		cpu.execute(Instruction.create("HALT"));
	}

	private class PowerSwitchInterruptHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			shutdown();
		}
	}

	
}
