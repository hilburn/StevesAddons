package stevesaddons.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import stevesaddons.asm.StevesHooks;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.EnumSet;


public abstract class ComponentMenuTriggered extends ComponentMenu
{

    protected TextBoxNumberList textBoxes = new TextBoxNumberList();

    private static final String NBT_DELAY = "Delay";
    private static final String NBT_COUNTDOWN = "Countdown";
    private int countdown;

    public ComponentMenuTriggered(FlowComponent parent) {
        super(parent);
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY) {
        this.textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
    }

    public void onClick(int mX, int mY, int button) {
        this.textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        return this.textBoxes.onKeyStroke(gui, c, k);
    }

    public void onDrag(int mX, int mY, boolean isMenuOpen) {
    }

    public void onRelease(int mX, int mY, boolean isMenuOpen) {
    }

    public void writeData(DataWriter dw) {
        int val = this.getDelay();
        if(val < getMin()) {
            val = getMin();
        }
        dw.writeData(val, 32);
    }

    public void readData(DataReader dr) {
        this.setDelay(dr.readData(32));
    }

    public void copyFrom(ComponentMenu menu) {
        this.setDelay(((ComponentMenuTriggered)menu).getDelay());
    }

    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuTriggered newDataTriggered = (ComponentMenuTriggered)newData;
        if(newDataTriggered.getDelay() != this.getDelay()) {
            copyFrom(newData);
            DataWriter dw = this.getWriterForClientComponentPacket(container);
            writeData(dw);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    public void readNetworkComponent(DataReader dr) {
        readData(dr);
    }

    public abstract int getDelay();

    public abstract void setDelay(int val);

    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup) {
        this.setDelay(nbtTagCompound.getInteger(NBT_DELAY));
        countdown = nbtTagCompound.getInteger(NBT_COUNTDOWN);
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        nbtTagCompound.setInteger(NBT_DELAY, this.getDelay());
        nbtTagCompound.setInteger(NBT_COUNTDOWN, this.countdown);
    }

    public int getMin()
    {
        return 1;
    }

    public void setCountdown()
    {
        if (isVisible())
        {
            countdown = 0;
            StevesHooks.registerTicker(getParent(), this);
        }
    }

    public void tick()
    {
        if (isVisible() && ++countdown >= getDelay())
        {
            act();
        }
    }

    protected void act()
    {
        getParent().getManager().activateTrigger(getParent(), getConnectionSets());
        countdown = 0;
    }

    protected abstract EnumSet<ConnectionOption> getConnectionSets();
}
