import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

public class WebWorker extends Thread {
	
	private String urlString;
	private WebFrame.Launcher launch;
	private int idx;
	private long timer;
	public WebWorker(WebFrame.Launcher launch, String urlString, int idx) {
		this.urlString = urlString;
		this.launch = launch;
		this.idx = idx;
	}
	
	public void run() {
		timer = System.currentTimeMillis();
 		InputStream input = null;
		StringBuilder contents = null;
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
			}
			
			// Successful download if we get here
			timer = System.currentTimeMillis() - timer;
			Calendar cal = Calendar.getInstance();
	        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
			String val = dateFormat.format(cal.getTime()) + " ";
			val += Long.toString(timer) + "ms ";
			val += Integer.toString(contents.length()) + " bytes";
			launch.setValue(val, idx, 1);
			launch.download_commit();
			return;
		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {
			launch.setValue("err", idx, 1);
			launch.download_commit();
		}
		catch(InterruptedException exception) {
			launch.setValue("interrupted", idx, 1);
			launch.download_commit();
		}
		catch(IOException ignored) {
			launch.setValue("err", idx, 1);
			launch.download_commit();
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}
	}
	
	
}
