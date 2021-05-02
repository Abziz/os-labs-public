package il.ac.telhai.os.software;

import java.util.Set;
import org.apache.log4j.Logger;

import il.ac.telhai.os.hardware.*;
import il.ac.telhai.os.software.language.*;
import il.ac.telhai.os.software.scheduler.*;

public class OperatingSystem implements Software {
	private static final Logger logger = Logger.getLogger(OperatingSystem.class);

	private static OperatingSystem instance = null;
	CPU cpu;
	private Set<Peripheral> peripherals;
	private Timer timer;
	private boolean initialized = false;
	private Scheduler scheduler;

	public OperatingSystem(CPU cpu, Set<Peripheral> peripherals) {
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
			scheduler.schedule();
		}
	}

	private void initialize() {
		installHandlers();
		ProcessControlBlock init = new ProcessControlBlock(null);
		if (!init.exec("init.prg")) {
			throw new IllegalArgumentException("Cannot load init");
		}
		// TODO: use Round Robin Scheduler instead
		scheduler = new FCFSScheduler(cpu, init);
		scheduler.schedule();
		initialized = true;
	}

	private void installHandlers() {
		for (Peripheral p : peripherals) {
			if (p instanceof PowerSwitch) {
				cpu.setInterruptHandler(p.getClass(), new PowerSwitchInterruptHandler());
			}
			// TODO: register the Timer Interrupt Handler
		}
		cpu.setInterruptHandler(SystemCall.class, new SystemCallInterruptHandler());
	}

	private void shutdown() {
		logger.info("System going for shutdown");
		cpu.execute(Instruction.create("HALT"));
	}

	private class PowerSwitchInterruptHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			shutdown();
		}
	}

	// TODO: create the Timer Interrupt Handler

	private class SystemCallInterruptHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			SystemCall call = (SystemCall) source;
			Operand op1 = call.getOp1();
			@SuppressWarnings("unused")
			Operand op2 = call.getOp2();
			ProcessControlBlock current = scheduler.getCurrent();
			switch (call.getMnemonicCode()) {
			case SHUTDOWN:
				shutdown();
				break;
			case FORK:
				ProcessControlBlock child = current.fork();
				scheduler.addReady(child);
				current.run(cpu);
				break;
			case EXEC:
				current.exec(cpu.getString(op1));
				current.run(cpu);
				break;
			case EXIT:
				current.exit(cpu.getWord(op1));
				scheduler.removeCurrent();
				scheduler.schedule();
				break;
			case GETPID:
				current.getPid();
				current.run(cpu);
				break;
			case GETPPID:
				current.getPPid();
				current.run(cpu);
				break;
			case LOG:
				logger.info(cpu.getString(call.getOp1()));
				current.run(cpu);
				break;

			// TODO: Implement additional system calls here
			default:
				throw new IllegalArgumentException("Unknown System Call:" + call);
			}
		}
	}
}
