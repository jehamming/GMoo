package geemoo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VerbEditor extends JFrame{
	
	private MainWindow mainWindow;
	private String mooHeader;
	
	private JTextArea taCode = new JTextArea();
	private JScrollPane scroller = new JScrollPane(taCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JScrollBar myScrollBar = scroller.getVerticalScrollBar();
	private Font myFont = new Font("Courier", Font.PLAIN, 12);

	
	public VerbEditor() {
		init();
	}
	
	public void init() {
		setTitle("GeeMoo - VerbEditor");
		myScrollBar.setBlockIncrement(50); // Default is 10.

		taCode.setLineWrap(true);
		taCode.setEditable(true);
		taCode.setFont(myFont);

		
		JButton btnSend = new JButton("Send to Moo");
		btnSend.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sendToMoo();
			}});
		JButton btnCancel = new JButton("Close Window");
		btnCancel.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cancel();
			}});
		
		JPanel btnPanel = new JPanel();
		btnPanel.add( btnSend );
		btnPanel.add( btnCancel );

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroller, "Center");
		getContentPane().add(btnPanel, "South");

		// Must do this, or specify row/columns for JTextArea, which
		// is hard to translate into pixel sizes.

		setPreferredSize(new Dimension(580, 400));
		pack();
	}
	
	protected void cancel() {
		this.setVisible(false);
		this.dispose();
	}

	protected void sendToMoo() {
		String cmd = getMooHeader().replaceAll(".*upload: ", "");
		getMainWindow().sendToMoo(cmd);
		getMainWindow().sendToMoo(taCode.getText());
		getMainWindow().sendToMoo(".\n");
		
	}

	public String getCode() {
		return taCode.getText();
	}
	public void setCode(String code) {
		taCode.setText( code );
	}
	public MainWindow getMainWindow() {
		return mainWindow;
	}
	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
	public String getMooHeader() {
		return mooHeader;
	}
	public void setMooHeader(String mooHeader) {
		this.mooHeader = mooHeader;
	}


	


}
