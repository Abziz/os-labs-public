package il.ac.telhai.os.hardware;

/**
 * 
 * @author cmshalom
 * A trivial implementation of a countdown timer
 * The counter is in terms of clock ticks
 * It will send an interrupt as soon as the counter gets down to zero
 */
public class Timer extends Peripheral {
	int countDownTimer = 0;
	
	public Timer(CPU cpu, Clock clock) {
		super(cpu);
		clock.addDevice(this);
	}

	public void set(int clockTicks) {
		countDownTimer = clockTicks;
	}

	@Override
	public void tick() {
		if (countDownTimer > 0) {
			countDownTimer--;
			if (countDownTimer == 0) {
				cpu.interrupt(this);
			}
		}
	}

	@Override
	public int getPriority() {
		return 2;
	}
}
