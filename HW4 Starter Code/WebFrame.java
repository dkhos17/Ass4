import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.Semaphore;

public class WebFrame extends JFrame {
	
	public class Launcher extends Thread{
		private Semaphore wait;
		private int num;
		public Launcher(int num) {
			this.num = num;
		}
		
		public void download_commit() {
			wait.release();
		}
		
		public void setValue(String val, int r, int c) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mod.setValueAt(val, r, c);				
				}
			});
		}
		
		@Override
		public void run() {
			int i = 0;
			WebWorker worker[] = new WebWorker[mod.getRowCount()];
			try {
				wait = new Semaphore(num);
				for(; i < mod.getRowCount(); i++) {
					if(isInterrupted()) {
						for(int j = 0; j < i; j++)
							worker[j].interrupt();
						return;
					}
					wait.acquire();
					worker[i] = new WebWorker(this, (String) mod.getValueAt(i, 0), i);
					worker[i].start();
				}
			} catch (InterruptedException e) {
				for(int j = 0; j < i; j++)
					worker[j].interrupt();
			}
		}
	}
	
	private JButton single, concurrent, stop;
	private JLabel running, complet, elaps;
	private DefaultTableModel mod;
	private Launcher launch;
	private JTextField field;
	private JTable table;
	
	public WebFrame(String file) {
		mod = new DefaultTableModel(new String[] { "url", "status"}, 0);
		table = new JTable(mod);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		JScrollPane scrollpane = new JScrollPane(table);   
		scrollpane.setPreferredSize(new Dimension(600,300)); 
		readFile(file);
		
		JPanel up = new JPanel();
		up.setLayout(new BoxLayout(up, BoxLayout.Y_AXIS));
		up.add(scrollpane);
		up.add(new JLabel("\n\n"));
		
		JPanel down = new JPanel();
		down.setLayout(new BoxLayout(down, BoxLayout.Y_AXIS));
		single = new JButton("Single Thread Fetch");
		concurrent = new JButton("Concurrent Fetch");
		down.add(single);
		down.add(concurrent);
		down.add(new JLabel("\n"));
		
		field = new JTextField();
		field.setMaximumSize(new Dimension(200,100));
		down.add(field);
		
		running = new JLabel("Running: ");
		complet = new JLabel("Completed: ");
		elaps = new JLabel("Elapsed: ");
		down.add(running);
		down.add(complet);
		down.add(elaps);
		down.add(new JLabel("\n"));
		
		JProgressBar load = new JProgressBar();
		down.add(load);
		down.add(new JLabel("\n"));
		
		stop = new JButton("Stop");
		down.add(stop);
		
		addListeners();
		JPanel web = new JPanel();
		web.setLayout(new BoxLayout(web, BoxLayout.Y_AXIS));
		web.add(up);
		web.add(down);
		this.add(web);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	private void setVal(String line, int r, int c) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				mod.setValueAt(line, r, c);				
			}
		});
	}
	
	private void readFile(String file) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String line = bf.readLine();
			int row = 0;
			mod.addRow(new String[] {"", ""});
			setVal(line, row++, 0);
			while(line != null) {
				mod.addRow(new String[] {"", ""});
				setVal(line, row++, 0);
				line = bf.readLine();
			}
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void addListeners() {
		single.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launch = new Launcher(1);
				launch.start();
			}
		});
		
		concurrent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launch = new Launcher(Integer.parseInt(field.getText()));
				launch.start();
			}
		});
		
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launch.interrupt();
			}
		});
	}
	
	public static void main(String[] args) {
		WebFrame webf = new WebFrame(args[0]);
	}

}
