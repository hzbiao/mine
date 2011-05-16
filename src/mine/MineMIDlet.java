package mine;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MineMIDlet extends MIDlet implements CommandListener {

	private static Display dis;
	Image img = null;
	Form frm = new Form("ɨ����Ϸ");
	private static Command startCmd = new Command("��ʼ��Ϸ", Command.OK, 1);
	private static Command setCmd = new Command("������Ϸ", Command.OK, 1);
	private static Command exitCmd = new Command("�˳�", Command.EXIT, 2);
	private static Command helpCmd = new Command("������Ϣ", Command.OK, 3);
	private static Command okCmd = new Command("ȷ��", Command.OK, 0);

	public MineMIDlet() {
		try {
			img = Image.createImage("/images/title.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dis = Display.getDisplay(this);
	}

	protected void startApp() throws MIDletStateChangeException {
		frm.append(ImageUtil.effect_resizeImage(img, frm.getWidth(),
				frm.getHeight() - 10));
		frm.addCommand(startCmd);
		frm.addCommand(exitCmd);
		frm.addCommand(setCmd);
		frm.addCommand(helpCmd);
		frm.setCommandListener(this);
		dis.setCurrent(frm);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == exitCmd) {
			notifyDestroyed();
		}
		if (c == startCmd) {
			MineCanvas mineCanvas = new MineCanvas(this);
			dis.setCurrent(mineCanvas);
		}
		if (c == helpCmd) {
			Form form = new Form("������Ϣ");
			form.append("1.��");
			form.addCommand(okCmd);
			form.setCommandListener(this);
			dis.setCurrent(form);
		}
		if (c == okCmd) {
			dis.setCurrent(frm);
		}
		if(c == setCmd){
			
		}
	}

	protected void pauseApp() {

	}

	protected void destroyApp(boolean arg0) {
	}

	public void comeBack() {
		dis.setCurrent(frm);
	}
}
