package Count;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class JCount extends JPanel{

	private JTextField field;
	private JButton start, stop;
	private JLabel counter;
	private Counter cnt;
	private static int update = 10_000;
	
	private class Counter extends Thread{
		private int cnt = 0;
		
		@Override
		public void run() {
			while(!interrupted()) {
				if(cnt > Integer.parseInt(field.getText())) break;
				if(cnt % update == 0) {
					try {
						SwingUtilities.invokeLater( new Runnable() {
							@Override
							public void run() {
								counter.setText(Integer.toString(cnt));	
							}
						});
						sleep(100);
					} catch (InterruptedException e) {
						return;
					}
				}
				cnt++;
			}
		}
		
	}
	
	public JCount() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		field = new JTextField(15);
		start = new JButton("Start");
		stop = new JButton("Stop");
		counter = new JLabel();
		
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cnt = new Counter();
				cnt.start();
			}
		});
		
		stop.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				cnt.interrupt();
			}
		});
		
		this.add(field);
		this.add(start);
		this.add(stop);
		this.add(counter);
	}
	
	private static void createAndShowGUI() {   
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		for(int i = 0; i < 4; i++) {
			panel.add(new JCount());
		}
		frame.add(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();    
			}
		});
	}

}
