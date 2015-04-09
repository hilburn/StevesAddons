package stevesaddons.components;

import net.minecraft.nbt.NBTTagCompound;
import stevesaddons.asm.StevesHooks;
import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.components.ConnectionOption;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.components.TextBoxNumber;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.EnumSet;

public class ComponentMenuDelayed extends ComponentMenuTriggered
{
    private static final int TEXT_BOX_X = 15;
    private static final int TEXT_BOX_Y = 35;
    private static final int MENU_WIDTH = 120;
    private static final int TEXT_MARGIN_X = 5;
    private static final int TEXT_Y = 10;
    private static final int TEXT_Y2 = 15;
    private static final int TEXT_SECOND_X = 60;
    private static final int TEXT_SECOND_Y = 38;
    private TextBoxNumber intervalTicks;
    private TextBoxNumber intervalSeconds;
    private static EnumSet<ConnectionOption> delayed = EnumSet.of(StevesEnum.DELAYED_OUTPUT);

    public ComponentMenuDelayed(FlowComponent parent)
    {
        super(parent);
        this.textBoxes.addTextBox(this.intervalSeconds = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 3, true)
        {
            public void onNumberChanged()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                int val = getDelay();
                if(val < getMin()) {
                    val = getMin();
                }
                dw.writeData(val, 31);
                PacketHandler.sendDataToServer(dw);
            }
        });
        this.textBoxes.addTextBox(this.intervalTicks = new TextBoxNumber(TEXT_BOX_X + intervalSeconds.getWidth() + TEXT_MARGIN_X, TEXT_BOX_Y, 2, true) {
            public void onNumberChanged() {
                DataWriter dw = getWriterForServerComponentPacket();
                int val = getDelay();
                if(val < getMin()) {
                    val = getMin();
                }
                dw.writeData(val, 31);
                PacketHandler.sendDataToServer(dw);
            }

            @Override
            public int getMaxNumber()
            {
                return 19;
            }
        });
        setDelay(5);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(StevesEnum.DELAY_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X, 0.7F, 4210752);
        gui.drawString(Localization.SECOND.toString(), TEXT_SECOND_X, TEXT_SECOND_Y, 0.7F, 4210752);
        super.draw(gui, mX, mY);
    }

    @Override
    public int getDelay()
    {
        return intervalTicks.getNumber() + intervalSeconds.getNumber() * 20;
    }

    @Override
    public void setDelay(int val)
    {
        intervalTicks.setNumber(val % 20);
        intervalSeconds.setNumber(val/20);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        super.readFromNBT(nbtTagCompound, version, pickup);
        if (this.isVisible() && this.counter >= 0) StevesHooks.registerTicker(getParent(), this);
    }

    @Override
    protected EnumSet<ConnectionOption> getConnectionSets()
    {
        return delayed;
    }

    @Override
    public String getName()
    {
        return StevesEnum.DELAY.toString();
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == StevesEnum.DELAYED;
    }

    @Override
    protected void act()
    {
        super.act();
        StevesHooks.unregisterTrigger(getParent(), this);
    }

    @Override
    protected void resetCounter()
    {
        counter = -1;
    }

    @Override
    public int getMin()
    {
        return 5;
    }
}
