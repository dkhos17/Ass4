package Bank;

public class Account {
	
	private int id, balance, transactions;
	
	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
		this.transactions = 0;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getBalance() {
		return this.balance;
	}
	
	public int getTransactions() {
		return this.transactions;
	}
	
	public synchronized void withdrow(int amount) {
//		if(this.balance < amount) return;
		this.balance -= amount;
		this.transactions++;
	}
	
	public synchronized void deposit(int amount) {
		this.balance += amount;
		this.transactions++;
	}
	
	public String toString() {
		return "ID: " + this.id + ", Balance: " 
				+ this.balance + ", Transactions: " + this.transactions; 
	}
}
