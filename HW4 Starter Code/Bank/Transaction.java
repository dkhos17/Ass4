package Bank;

public class Transaction {

	private int from, to, amount;
	
	public Transaction(int from, int to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
	public int getAmount() {
		return amount;
	}
	
}
