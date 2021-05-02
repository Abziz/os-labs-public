package il.ac.telhai.os.hardware;

import java.awt.Component;

/**
 * 
 * @author cmhalom
 * A peripheral is an external device that can interrupt the CPU
 * It implements Clockeable so that it can it can operate
 * It implements InterruptSource so that it can send interrupts
 * It's constructor get as CPU parameter so that it knows where to 
 * send the interrupts
 * The constructor may optionally create a component that can be used as a user I/F
 * to the simulated device 
 */
public abstract class Peripheral implements Clockeable, InterruptSource {
	protected CPU cpu;
	protected Component component;
	
    public Peripheral (CPU cpu) {
    	this.cpu = cpu;
    }
    
    // It has default visibility so that it can be used only by hardware
    Component getComponent() {
    	return component;
    }

    public abstract void out(String s);
}
