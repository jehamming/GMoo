package geemoo;
import javax.swing.JScrollBar;

public class MUDScroller implements Runnable {

    JScrollBar myScrollBar ;
    int        value ;
    public void setScrollBar(JScrollBar sb) { myScrollBar = sb ; }
    public void setValue(int value) { this.value =value ; }

    public void run() {
	myScrollBar.setValue(myScrollBar.getMaximum()) ;
    }

}
