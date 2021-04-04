package il.ac.telhai.os.hardware;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * 
 * @author cmhalom
 * A very simple power (off) switch
 * It interrupts the CPU as soon as a power off event happens
 */
public class PowerSwitch extends Peripheral {

	public PowerSwitch(CPU cpu, Container container) {
		super(cpu);

		if (container != null) {
			JButton button = new JButton("Off");
			button.setBackground(Color.RED);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					powerOff();
				}
			});
			container.add(button);
		}
	}

	@Override
	public void tick() {
		// Nothing to do
	}

	public void powerOff() {
		cpu.interrupt(this);
	}

	@Override
	public int getPriority() {
		return 3;
	}

}
