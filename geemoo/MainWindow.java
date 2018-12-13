package geemoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MainWindow extends JFrame implements KeyListener, ActionListener {

	HTMLPanel htmlPanel = new HTMLPanel();
	JTextField input = new JTextField();
	JScrollPane scroller = new JScrollPane(htmlPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JScrollBar myScrollBar = scroller.getVerticalScrollBar();
	String mudname;
	String address;
	int port;
	Color oldColor;
	Color grayColor = new Color(128, 128, 128);
	Font myFont = new Font("Courier", Font.PLAIN, 12);
	List<String> history = new ArrayList<String>();
	int cmdNumber = 0; // What history command we're viewing.
	int lastCmd = -1;
	Thread netThread;
	MUDConnection myConnection;
	JDesktopPane desktop = new JDesktopPane();
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem connect = new JMenuItem("Connect...");
	JMenuItem exit = new JMenuItem("Exit");
	JMenu worldsMenu = new JMenu("Worlds");
	JMenuItem newWorld = new JMenuItem("New World...");
	JMenuItem editWorlds = new JMenuItem("Edit Worlds...");
	JMenu windowMenu = new JMenu("Window");
	boolean connected = false;
	Hashtable worldList;
	String worldsFileName = new String("worldlist");
	
	String htmlFace = "Courier";
	String htmlSize = "12px";


	PConnectDialog connectDialog;
	PEditWorldDialog editWorldDialog;

	public MainWindow() {
		init();
	}


	public void init() {
		setTitle("GeeMoo - MainWindow");
		myScrollBar.setBlockIncrement(50); // Default is 10.

		input.addActionListener(this);
		input.addKeyListener(this);
		input.setFont(myFont);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroller, "Center");
		getContentPane().add(input, "South");

		// Must do this, or specify row/columns for JTextArea, which
		// is hard to translate into pixel sizes.

		setPreferredSize(new Dimension(580, 400));

		initToolbar();
		
		addWindowListener( new WindowListener(){
			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}});

		pack();

		input.requestFocus();
	}

	private void initToolbar() {
		connect.addActionListener(this);
		fileMenu.add(connect);
		exit.addActionListener(this);
		fileMenu.add(exit);
		menuBar.add(fileMenu);

		newWorld.addActionListener(this);
		worldsMenu.add(newWorld);

		editWorlds.addActionListener(this);
		worldsMenu.add(editWorlds);
		worldsMenu.addSeparator();

		// Populate the drop-down with worlds.
		loadWorldList();
		generateWorldsMenu();

		menuBar.add(worldsMenu);

		menuBar.add(windowMenu);

		this.setJMenuBar(menuBar);
		connectDialog = new PConnectDialog(this, false); // Not Modal.
		connectDialog.setLocationRelativeTo(this); // Show atop MUDDesktop.

		editWorldDialog = new PEditWorldDialog(this);
		editWorldDialog.setLocationRelativeTo(this);
	}

	public void saveWorldList() {

		try {
			FileOutputStream fos = new FileOutputStream(worldsFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(worldList);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			showStatus("Problem saving worldList.");
		}

	}

	public void loadWorldList() {

		try {
			FileInputStream fis = new FileInputStream(worldsFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);

			worldList = (Hashtable) ois.readObject();
			if (worldList == null) {
				worldList = new Hashtable(10);
			}
		} catch (Exception e) {
			showStatus("** Problem reading worldList: " + e);
			worldList = new Hashtable(10);
		}

	}

	public void generateWorldsMenu() {
		Enumeration e = worldList.elements();
		while (e.hasMoreElements()) {
			Hashtable h = (Hashtable) e.nextElement();
			JWorldItem mi = new JWorldItem((String) h.get("name"));
			worldsMenu.add(mi);
			mi.addActionListener(this);

		}
	}

	public void sendToMoo(String s) {
		myConnection.sendLine(s);
	}

	// ActionListener interface.
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == input) {
			String cmd = e.getActionCommand();
			doAppend(cmd);
			input.setText("");
			input.requestFocus();

			if (cmd.length() > 0) {
				myConnection.sendLine(cmd);
				history.add(cmd);
				lastCmd++;
				cmdNumber = lastCmd + 1;
			}
		} else if (source == exit) {
			System.exit(0);
		} else if (source == connect) {

			connectDialog.setVisible(true);
			connectDialog.toFront();

		} else if (source == newWorld) {

			Hashtable world = new Hashtable();
			editWorldDialog.setWorld(world);
			editWorldDialog.setVisible(true);
			editWorldDialog.toFront();

		} else if (source instanceof JWorldItem) {

			String bname = ((JMenuItem) source).getText();
			Hashtable world = (Hashtable) worldList.get(bname);
			if (world != null) {
				showStatus("Got world: " + world);

				// This was all cut and pasted from above.
				// I should be able to generate a subclass of
				// AbstractAction to use in both cases, right?
				// How do I assocate an action with a JDialog?
				// Is it just as easy as:
				//
				// dialog.addActionListener(new ConnectAction())?
				// Probably.

				// String name = (String)world.get("name") ;
				String address = (String) world.get("address");
				int port = Integer.parseInt((String) world.get("port"));

				if (connected)
					doDisconnect();
				doConnect(address, address, port);

			}

		}

	}

	public boolean doConnect(String nym, String addr, int p) {
		mudname = nym;
		address = addr;
		port = p;

		showStatus("Asked to connect to: " + mudname + " " + port);

		myConnection = new MUDConnection();
		myConnection.setMainWindow(this);
		myConnection.setHost(address);
		myConnection.setPort(port);

		netThread = new Thread(myConnection);
		netThread.start();
		connected = true;
		return true;

	}

	public void doDisconnect() {
		myConnection.doDisconnect();
		connected = false;
	}

	public void showStatus(String s) {
		System.out.println("MUDWindow: " + s);
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_PAGE_UP) {
			// showStatus("keyReleased: page up.") ;
			scrollUp();
		} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
			// showStatus("keyReleased: page down.") ;
			scrollDown();
		} else if (keyCode == KeyEvent.VK_UP) {

			// showStatus("keyReleased: up arrow.") ;

			if (cmdNumber > 0) {
				cmdNumber--;
				input.setText((String) history.get(cmdNumber));
			}
		} else if (keyCode == KeyEvent.VK_DOWN) {

			// showStatus("keyReleased: down arrow: "
			// + cmdNumber + " " + lastCmd + " " + history.size()) ;

			if (cmdNumber < lastCmd) {
				cmdNumber++;
				input.setText((String) history.get(cmdNumber));
			} else {
				input.setText("");
			}

		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void doAppend(String line) {
		htmlPanel.addHTML("<font face=\""+htmlFace+"\" size=\""+htmlSize+"\" >"+line + "</font>\n");
		if (isActive()) {
			input.requestFocus();
		}
		scrollToBottom();
	}

	public void firstAppend(String line) {
		htmlPanel.addHTML(line + "\n");
		input.requestFocus();
		scrollToBottom();
	}

	public void requestFocus() {
		input.requestFocus();
		scrollToBottom();
	}

	public void scrollUp() {
		int start = myScrollBar.getValue();
		int inc = myScrollBar.getBlockIncrement();
		int min = 0;
		int newValue;

		// showStatus("scrollUp: start = " + start + " increment: " + inc) ;

		if (start == min) {
			return;
		}

		if ((start - inc) >= min) {
			newValue = start - inc;
		} else {
			newValue = 0;
		}

		myScrollBar.setValue(newValue);

	}

	public void scrollDown() {
		int start = myScrollBar.getValue();
		int inc = myScrollBar.getBlockIncrement();
		int max = myScrollBar.getMaximum();
		int newValue;

		// showStatus("scrollDown: start = " + start + " increment: " + inc) ;

		if (start == max) {
			return;
		}

		if ((start + inc) <= max) {
			newValue = start + inc;
		} else {
			newValue = max;
		}

		myScrollBar.setValue(newValue);

	}

	public void scrollToBottom() {

		// Threadsafe version. A bit of a pain to do, but.
		if (SwingUtilities.isEventDispatchThread()) {
			// Can't call invokeAndWait from dispatch thread.

			myScrollBar.setValue(myScrollBar.getMaximum());

		} else {
			MUDScroller scr = new MUDScroller();
			scr.setScrollBar(myScrollBar);
			scr.setValue(myScrollBar.getMaximum());

			try {
				SwingUtilities.invokeLater(scr);
			} catch (Exception e) {
				showStatus("invokeAndWait errored: " + e);
			}
		}

	}

	public void showDialog(String line) {
		JOptionPane.showMessageDialog(this, line);
	}

	public static void main(String[] args) {
		MainWindow w = new MainWindow();
		w.setVisible(true);

	}

	public HTMLPanel getGeeView() {
		return htmlPanel;
	}

	public void addHTML(String bufferedText) {
		htmlPanel.addHTML( bufferedText );
		scrollToBottom();
		
	}

}
