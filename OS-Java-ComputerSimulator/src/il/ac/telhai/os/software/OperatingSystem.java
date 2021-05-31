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
	VMM vmm;



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
			scheduler.schedule();
		}
	}
		
	private void initialize() {
		installHandlers();
		ProcessControlBlock init = new ProcessControlBlock(null);
		if (!init.exec("init.prg")) {
			throw new IllegalArgumentException ("Cannot load init");
		}
		scheduler = new RoundRobinScheduler(cpu, init, timer);
		scheduler.schedule();
		initialized = true;
	}	
	
	private void installHandlers() {
		for (Peripheral p : peripherals) {
			if (p instanceof PowerSwitch) {
				cpu.setInterruptHandler(p.getClass(), new PowerSwitchInterruptHandler());
			} else if (p instanceof Timer) {
				timer = (Timer) p;
				cpu.setInterruptHandler(p.getClass(), new TimerInterruptHandler());
			}
		}
		cpu.setInterruptHandler(SystemCall.class, new SystemCallInterruptHandler());
		vmm = new VMM(cpu);
		cpu.setInterruptHandler(PageFault.class, vmm);
		cpu.setInterruptHandler(SegmentationViolation.class, new SegmentationFaultHandler());
	}


	private void shutdown() {
		logger.info( "System going for shutdown");
		ProcessControlBlock.shutdown();
		vmm.shutdown();
		cpu.execute(Instruction.create("HALT"));
	}

	private class PowerSwitchInterruptHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			shutdown();
		}
	}
	
	private class TimerInterruptHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			ProcessControlBlock current = scheduler.removeCurrent();
			scheduler.addReady(current);
			scheduler.schedule();
		}
	}

	private class SegmentationFaultHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			ProcessControlBlock current = scheduler.removeCurrent();
			logger.info("Segmentation Fault in process:" + current.getId());
			current.exit(256);
			scheduler.schedule();
		}
	}

	private class SystemCallInterruptHandler implements InterruptHandler {
		@Override
		public void handle(InterruptSource source) {
			SystemCall call = (SystemCall) source;
			logger.trace(call);
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
				current.exec(current.getString(op1));
				current.run(cpu);
				break;
			case EXIT:
				current.exit(current.getWord(op1));
				scheduler.removeCurrent();
				scheduler.schedule();
				break;
			case YIELD:
				scheduler.removeCurrent();
				scheduler.addReady(current);
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
				logger.info(current.getString(op1));
				current.run(cpu);
                break;
			default:
				throw new IllegalArgumentException("Unknown System Call:" + call);
			}
		}
	}
}
