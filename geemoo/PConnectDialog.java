package geemoo;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.border.TitledBorder ;

public class PConnectDialog extends JDialog
    implements ActionListener {

    JTextField address        = new JTextField(50) ;
    JTextField port           = new JTextField(5) ;
    JButton    connectButton  = new JButton("connect") ;

    JLabel     addressLabel   = new JLabel("Address: ") ;
    JLabel     portLabel      = new JLabel("Port: ") ;
    JLabel     blankLabel     = new JLabel("") ;

    JPanel     contentPanel   = new JPanel() ;

    TitledBorder border         = new TitledBorder("connect") ;
    boolean      connectPressed = false ;
    MainWindow parent;

    public PConnectDialog(MainWindow p, boolean modal) {
	super(p, "Connect to...", modal) ;
	
	parent = p;

 	getContentPane().setLayout(new GridLayout(3, 2)) ;
	getContentPane().add(addressLabel) ;
	getContentPane().add(address) ;
	getContentPane().add(portLabel) ;
	getContentPane().add(port) ;
	getContentPane().add(blankLabel) ;
	getContentPane().add(connectButton) ;
	
	/*

	  contentPanel.setLayout(new GridLayout(3, 2)) ;
	  contentPanel.add(addressLabel) ;
	  contentPanel.add(address) ;
	  contentPanel.add(portLabel) ;
	  contentPanel.add(port) ;
	  contentPanel.add(blankLabel) ;
	  contentPanel.add(connectButton) ;
	  contentPanel.setBorder(border) ;
	  getContentPane().add(contentPanel) ;

	*/


	connectButton.addActionListener(this) ;
	port.addActionListener(this) ;

	pack() ;
	
    }

    public Dimension getPreferredSize() {
	return new Dimension(250, 100) ;
    }

    public String getAddress() {
	return address.getText() ;
    }

    public int getPort() {
	return Integer.parseInt(port.getText()) ;
    }

    public void setVisible(boolean arg) {
	super.setVisible(arg) ;
	if (arg == true) {
	    connectPressed = false ;
	}
    }

    public boolean connectPressed() {
	return connectPressed ;
    }

    public void actionPerformed(ActionEvent e) {
	Object source = e.getSource() ;

	if (source == connectButton) {
	    connectPressed = true ;
	}

	// If the person hits "return" after entering the port...
	if (source == port && port.getText().length() > 0) {
	    connectPressed = true ;
	}
	int i = Integer.parseInt( port.getText() );
	parent.doConnect("connect", address.getText(), i);
	
	

	setVisible(false) ;
    }

    public void showStatus(String line) {
	System.out.println("PConnectDialog: " + line) ;
    }

}
