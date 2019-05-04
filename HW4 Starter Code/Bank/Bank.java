package Bank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {

	//if we don't want BlockingQueue static, we can readFile in Bank constructor...  
	private static BlockingQueue<Transaction> Q;
	private static List<Account> accs;
	private static CountDownLatch wait;
	private int defSize = 20, accNum = 20;
	
	
	private class WorkerThread extends Thread {
	    @Override
	    public void run() {
	    	try {
	    		while(true) {
					Transaction tr = Q.take();
					if(tr.getFrom() == -1) {
						wait.countDown();
						break;
					}
					Account from = accs.get(tr.getFrom());
					Account to = accs.get(tr.getTo());
					int amount = tr.getAmount();
					
					from.withdrow(amount);
					to.deposit(amount);
	    		}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	
	    }
	}
	
	public Bank(int workers, String file) {
		Q = new ArrayBlockingQueue<>(defSize);
		accs = new ArrayList<>();
		
		wait = new CountDownLatch(workers);
		for(int i = 0; i < workers; i++) {
			WorkerThread worker = new WorkerThread();
			worker.start();
		}
		
		for(int i = 0; i < accNum; i++) {
			accs.add(new Account(i, 1000));
		}	
	}
	
	
	public static void main(String[] args) {
		Bank bank = new Bank(Integer.parseInt(args[0]), args[1]);
		
		try {
			BufferedReader bf = new BufferedReader(new FileReader(args[1]));
			String line = bf.readLine();
				
			while (line != null) {
				String[] arr = line.split(" ");
				int from = Integer.parseInt(arr[0]);
				int to = Integer.parseInt(arr[1]);
				int amount = Integer.parseInt(arr[2]);

				Q.put(new Transaction(from, to, amount));
			    line = bf.readLine();
			}
			bf.close();

			for(int i = 0; i < Integer.parseInt(args[0]); i++) {
				Q.put(new Transaction(-1, -1, 0));
			}
			wait.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < accs.size(); i++) {
			Account acc = accs.get(i);
			System.out.println(acc.toString());
		}
	}
	
}
