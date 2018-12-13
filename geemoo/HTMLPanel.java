package geemoo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class HTMLPanel extends JPanel {

	private static final long serialVersionUID = 2840981881960475392L;
	JTextPane output = new JTextPane();
	Font myFont = new Font("Courier", Font.PLAIN, 12);

	public HTMLPanel() {
		init();
	}
	
	public void addHTML( String html ) {
		//System.out.println("HTMLPanel: received HTML:\n" + html);
		String s = output.getText();
		
		s = s.replaceAll("</body>\n</html>","");
		s = s.concat(html + "<br/>\n</body>\n</html>");
		System.out.println("Current text:" + s);
		output.setText( s );
	}

	public void init() {		
		setLayout(new BorderLayout());
		output.setContentType("text/html");
		output.setText( "<html>\n</html>" );
		//output.setLineWrap(true);
		output.setEditable(false);
		output.setFont( myFont );
		add( output );
	}

	



}
