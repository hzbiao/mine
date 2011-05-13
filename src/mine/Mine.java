package mine;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Mine extends MIDlet {

	private static Display display;
	MGame pStart;
	
	public Mine() {
		super();
		display = Display.getDisplay(this);
		pStart = new MGame(this);
		// TODO Auto-generated constructor stub
	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		pStart.activate(display);
//		display.setCurrent(pStart);
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void destroyApp(boolean arg0) {
		// TODO Auto-generated method stub
		notifyDestroyed();
	}

}
