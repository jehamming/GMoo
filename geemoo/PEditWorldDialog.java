package geemoo;
import java.awt.event.* ;
import javax.swing.* ;
import java.util.* ;
import java.awt.Dimension ;
import java.awt.Frame ;
import java.awt.GridLayout ;

public class PEditWorldDialog extends JDialog
    implements ActionListener {

    Hashtable world ;
    boolean   savePressed ;

    JTextField   name       = new JTextField(50) ;
    JTextField   address    = new JTextField(50) ;
    JTextField   port       = new JTextField(5) ;
    JButton      saveButton = new JButton("save") ;

    JLabel       nameLabel    = new JLabel("Name:") ;
    JLabel       addressLabel = new JLabel("Address:") ;
    JLabel       portLabel    = new JLabel("Port:") ;
    JLabel       blankLabel   = new JLabel("") ;

    public PEditWorldDialog(Frame parent) {
	super(parent, "Editing World") ;

	getContentPane().setLayout(new GridLayout(4, 2)) ;
	getContentPane().add(nameLabel) ;
	getContentPane().add(name) ;
	getContentPane().add(addressLabel) ;
	getContentPane().add(address) ;
	getContentPane().add(portLabel) ;
	getContentPane().add(port) ;
	getContentPane().add(blankLabel) ;
	getContentPane().add(saveButton) ;

	saveButton.addActionListener(this) ;

	pack() ;

    }

    public void setWorld(Hashtable w) {
	world = w ;
	savePressed = false ;

	name.setText((String)world.get("name")) ;
	address.setText((String)world.get("address")) ;
	port.setText((String)world.get("port")) ;
    }

    public boolean savePressed() {
	return savePressed ;
    }

    public Dimension getPreferredSize() {
	return new Dimension(250, 125) ;
    }

    public void actionPerformed(ActionEvent e) {
	Object source = e.getSource() ;

	if (source == saveButton) {
	    savePressed = true ;
	}

	setVisible(false) ;
    }

    public String getName()    { return name.getText() ; }
    public String getAddress() { return address.getText() ; }
    public String getPort()    { return port.getText() ; }

}
