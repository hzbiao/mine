package mine;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class Options implements CommandListener
{
    private MineMIDlet midlet;
    private Form form;
    private Command cmdOK;
    private Command cmdCancel;
    private ChoiceGroup choiceDegree;
    private int degree;
    public Options(MineMIDlet m)
    {
        degree = 1;
        midlet = m;
        loadOptions();
        form = new Form("游戏设置");
        choiceDegree = new ChoiceGroup("等级难度:", 1);
        choiceDegree.append("初级", null);
        choiceDegree.append("中级", null);
        choiceDegree.append("高级", null);
        choiceDegree.setSelectedIndex(degree-1 , true);
        form.append(choiceDegree);
        cmdOK = new Command("确定", Command.OK, 2);
        cmdCancel = new Command("取消", Command.CANCEL, 1);
        form.addCommand(cmdOK);
        form.addCommand(cmdCancel);
        form.setCommandListener(this);
    }
    public int getDegree()
    {
        return degree;
    }

    public Form getForm()
    {
        return form;
    }

  

    public void commandAction(Command c, Displayable s)
    {
        if(c == cmdOK)
        {
            degree = choiceDegree.getSelectedIndex() + 1;
            saveOptions();
            midlet.comeBack();
        } else
        if(c == cmdCancel)
            midlet.comeBack();
    }

    private void loadOptions()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore("Options", false);
            if(rs.getNumRecords() > 0)
            {
                byte bs[] = rs.getRecord(1);
                if(bs.length >= 1)
                {
                    degree = bs[0];
                    if(degree < 1)
                        degree = 1;
                    if(degree > 3)
                        degree = 3;
                }
            }
            rs.closeRecordStore();
        }
        catch(RecordStoreException e) { }
    }

    private void saveOptions()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore("Options", true);
            byte bs[] = new byte[1];
            bs[0] = (byte)degree;
            if(rs.getNumRecords() > 0)
                rs.setRecord(1, bs, 0, bs.length);
            else
                rs.addRecord(bs, 0, bs.length);
            rs.closeRecordStore();
        }
        catch(RecordStoreException e) { }
    }


}