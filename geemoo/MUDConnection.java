package geemoo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class MUDConnection implements Runnable {

	String host;
	int port;
	Socket mySocket;
	DataOutputStream dout;
	InputStreamReader din;
	BufferedReader br;
	MainWindow mainWindow;
	HashMap textTriggers = new HashMap();
	
	private boolean buffering = false;
	private String bufferedText = "";
	private int dispatcher = -1;
	private String header;
	
	private final static int BUFF_XML = 0;
	private final static int BUFF_VERB = 1;
	
	
	public MUDConnection() {
	}


	public void setHost(String h) {
		host = h;
	}

	public void setPort(int p) {
		port = p;
	}



	public void run() {

		String hostAndPort = new String(host + ":" + port);

		mainWindow.firstAppend("*** Connecting to " + hostAndPort + " ***");

		try {
			mySocket = new Socket(host, port);
			dout = new DataOutputStream(mySocket.getOutputStream());
			din = new InputStreamReader(mySocket.getInputStream());
			br = new BufferedReader(din);
		} catch (Exception e) {
			showStatus("Unable to connect to " + host + ":" + port + " " + e);

			mainWindow.showDialog("Unable to connect to " + hostAndPort + "\n" + e);

			mainWindow.doAppend("*** Not connected to " + hostAndPort + " ***");

			return;
		}

		mainWindow.doAppend("*** Connected to " + hostAndPort + " ***");

		while (true) {
			String line;
			try {
				line = br.readLine();
			} catch (Exception e) {
				showStatus("Unable to read line from socket: " + e);

				mainWindow.doAppend("*** Connection dropped: " + hostAndPort + " ***");

				return;
			}

			if (line == null) {
				mainWindow.doAppend("*** Disconnected from " + hostAndPort + " ***");

				return;
			}

			if ( ! handleText( line ) ) {
				mainWindow.doAppend(line);
			}
			
		}

	}

	private boolean handleText(String line) {
		boolean handled = false;
		if ( buffering ) {
			if ( line.trim().equals(".")) {
				buffering = false;
				// end of buffering, discard "."
				if ( dispatcher == BUFF_VERB ) {
					// Start VerbEditor
					VerbEditor e = new VerbEditor();
					e.setMainWindow(getMainWindow());
					e.setCode(bufferedText);
					bufferedText = "";
					e.setMooHeader(header);
					e.setVisible(true);
					e.toFront();
					handled = true;
				}
			} else if (line.trim().equalsIgnoreCase("</html>")){
				// Send xml data to GeeView
				getMainWindow().addHTML(bufferedText);
				bufferedText = "";
				handled = true;
				buffering = false;
			} else {
				// just buffer
				bufferedText = bufferedText.concat(line + "\n");
				handled = true;
			}
			
		} else {
			if ( line.startsWith("#$# edit") ) {
				// @edit used
				buffering = true;
				header = line;
				bufferedText = "";
				dispatcher = BUFF_VERB;
				handled = true;
			}
			if ( line.trim().equalsIgnoreCase("<html>") ) {
				// HTML
				buffering = true;
				header = line;
				bufferedText = "";
				dispatcher = BUFF_XML;
				handled = true;
			}
		}
		

		return handled;

	}

	public void sendLine(String line) {
		try {
			dout.writeBytes(line + "\n");
		} catch (Exception e) {
			showStatus("Can't write line: " + e);
		}
	}

	public void showStatus(String s) {
		System.out.println("MUDConnection: " + s);
	}

	public void doDisconnect() {
		try {
			mySocket.close();
		} catch (Exception e) {
		}
		;

	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

}
