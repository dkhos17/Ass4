import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();	
	private String hash;
	private static CountDownLatch wait;
	
	public void Hack(String input, int hackers, int max_len) {
		wait = new CountDownLatch(hackers);
		this.hash = input;
		
		int from = 0;
		for(int i = 0; i < hackers; i++) {
			int to = from + CHARS.length/hackers;
			if(from >= CHARS.length) from = CHARS.length;
			
			Hacker hacker = new Hacker(from, to, max_len);
			hacker.start();
			from = to;
		}
		try {
			wait.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private class Hacker extends Thread {
	    private int from, to, len;
	    private MessageDigest md;
	    
		public Hacker(int from, int to, int len) {
			this.from = from;
			this.to = to;
			this.len = len;
			try {
				md = MessageDigest.getInstance("SHA");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private boolean check(String t) {
			byte[] hashBytes = md.digest(t.getBytes());
			String curr = hexToString(hashBytes);
			return curr.equals(hash);
		}
		
		private void possibles(String curr, int len) {
			if(len == 0) return;
			for(char c : CHARS) {
				String t = curr+c;
				if(check(t)) System.out.println(t);
				possibles(curr+c, len-1);
			}
		}
		
	    @Override
	    public void run() {
			for(;from < to && from < CHARS.length; from++) {
		   		String curr = ""+CHARS[from]+"";
		   		possibles(curr, len-1);
		   	}
		   	wait.countDown();	
	    }
	}
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	// possible test values:
	// a 86f7e437faa5a7fce15d1ddcb9eaeaea377667b8
	// fm adeb6f2a18fe33af368d91b09587b68e3abcb9a7
	// a! 34800e15707fae815d7c90d49de44aca97e2d759
	// xyz 66b27417d37e024c46526c2f6d358a754fc552f3
	
	public static void main(String[] args) {
		try {
			if(args.length == 1) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				byte[] hashBytes = md.digest(args[0].getBytes());
				String hash = hexToString(hashBytes);
				System.out.println(hash);
				return;
			}
			Cracker crack = new Cracker();
			crack.Hack(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
