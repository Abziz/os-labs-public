package il.ac.telhai.os.hardware;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;

import javax.swing.JButton;

/**
 * 
 * @author cmhalom
 * A very simple power (off) switch
 * It interrupts the CPU as soon as a power off event happens
 */
public class PowerSwitch extends Peripheral {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PowerSwitch.class);
        	
	public PowerSwitch(CPU cpu, Container container) {
		super(cpu);
		if (container != null) 	{
			component = powerOffButton();
			container.add(component);
		}
	}

	private JButton powerOffButton() {
		JButton button = new JButton("Off");
    	button.setBackground(Color.RED);
    	button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				powerOff();
			}
		});
    	return button;
	}
	
	@Override
	public void tick() {
		throw new IllegalStateException("Power Switch does not use clock tics");
	}
	
	public void powerOff() {
		cpu.interrupt(this);
	}

	@Override
	public void out(String s) {
		throw new IllegalStateException("Power Switch is not an output device");
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
