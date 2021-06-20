package il.ac.telhai.os.hardware;

import org.apache.log4j.Logger;

/**
 * 
 * @author cmshalom
 * A trivial implementation of a countdown timer
 * The time unit is a clock tick
 * It will send an interrupt as soon as the counter gets down to zero
 */
public class Timer extends Peripheral {
	private static final Logger logger = Logger.getLogger(Timer.class);

	long countDownTimer = 0;
	long time = 0;
	
	public Timer(CPU cpu, Clock clock) {
		super(cpu);
		clock.addDevice(this);
	}

	public void setTime(long clockTicks) {
		time = clockTicks;
	}

	public long getTime() {
		return time;
	}
	
/**
 * Sets the alarm to a given number of clock ticks from now
 * @param fromNow
 */
	public void setAlarm(int delay) {
		logger.info("ALARM SET TO " + delay);
		countDownTimer = delay;
	}

	/**
	 * Sets the alarm to a given time
	 * @param fromNow
	 */

	public void setAlarm(long time) {
		// TODO: (not for students) Handle Overflow of time
		countDownTimer = time - this.time;
	}


	@Override
	public void tick() {
		time++;
		if (countDownTimer > 0) {
			countDownTimer--;
			if (countDownTimer == 0) {
				cpu.interrupt(this);
				logger.info ("TIMER EXPIRED");
			}
		}
	}
	
	@Override
	public void out(String s) {
		throw new IllegalStateException("Timer is not an output device");
	}

	@Override
	public int getPriority() {
		return 10;
	}

}
