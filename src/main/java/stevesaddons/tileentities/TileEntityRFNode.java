package stevesaddons.tileentities;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import stevesaddons.components.ComponentMenuRF;
import stevesaddons.components.ComponentMenuRFInput;
import stevesaddons.components.ComponentMenuTargetRF;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.network.MessageHandler;
import stevesaddons.network.message.RFNodeUpdateMessage;
import vswe.stevesfactory.blocks.*;
import vswe.stevesfactory.components.ComponentMenu;
import vswe.stevesfactory.components.FlowComponent;

import java.util.*;

public class TileEntityRFNode extends TileEntityClusterElement implements IEnergyProvider, IEnergyReceiver, ISystemListener
{
    private boolean[] inputSides = new boolean[6];
    private boolean[] outputSides = new boolean[6];
//    private Set<TileEntityManager> managers = new HashSet<TileEntityManager>();
    private Set<FlowComponent> components = new HashSet<FlowComponent>();
    private static final String INPUTS = "Inputs";
    private static final String OUTPUTS = "Outputs";
    private boolean updated;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.isPartOfCluster() && updated) sendUpdatePacket();
    }

    private void sendUpdatePacket()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            MessageHandler.INSTANCE.sendToAll(new RFNodeUpdateMessage(this));
            updated = false;
        }
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
    }


    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        int toReceive = maxReceive;
        for (int i = 0; i < 6; i++)
        {
            if (outputSides[i])
            {
                TileEntity te = getTileEntityOnSide(i);
                if (te != null && te instanceof IEnergyReceiver && !(te instanceof TileEntityRFNode))
                {
                    toReceive -= ((IEnergyReceiver)te).receiveEnergy(ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[i]), toReceive, simulate);
                    if (toReceive == 0) break;
                }
            }
        }
        return maxReceive - toReceive;
    }

    private TileEntity getTileEntityOnSide(int side)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;
        return worldObj.getTileEntity(x, y, z);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int toExtract = maxExtract;
        for (int i = 0; i < 6; i++)
        {
            if (inputSides[i])
            {
                TileEntity te = getTileEntityOnSide(i);
                if (te != null && te instanceof IEnergyProvider && !(te instanceof TileEntityRFNode))
                {
                    toExtract -= ((IEnergyProvider)te).extractEnergy(ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[i]), toExtract, simulate);
                    if (toExtract == 0) break;
                }
            }
        }
        return maxExtract - toExtract;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return 10000;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return 20000;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return outputSides[from.ordinal()] || inputSides[from.ordinal()];
    }

    @Override
    public void added(TileEntityManager tileEntityManager)
    {
//        managers.add(tileEntityManager);
        for (FlowComponent component : tileEntityManager.getFlowItems()) update(component);
    }

    @Override
    public void removed(TileEntityManager tileEntityManager)
    {
//        managers.remove(tileEntityManager);
        for (Iterator<FlowComponent> itr = components.iterator(); itr.hasNext(); )
        {
            if (itr.next().getManager() == tileEntityManager) itr.remove();
        }
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.of(StevesEnum.CONNECT_ENERGY, StevesEnum.EXTRACT_ENERGY, StevesEnum.RECEIVE_ENERGY);
    }

    public boolean isInput(int side)
    {
        return inputSides[side];
    }

    public boolean isOutput(int side)
    {
        return outputSides[side];
    }

    public void setOutputSides(boolean[] outputSides)
    {
        this.outputSides = outputSides;
    }

    public void setInputSides(boolean[] inputSides)
    {
        this.inputSides = inputSides;
    }

    public boolean[] getOutputs()
    {
        return outputSides;
    }

    public boolean[] getInputs()
    {
        return inputSides;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        writeToNBT(new NBTTagCompound());
        return MessageHandler.INSTANCE.getPacketFrom(new RFNodeUpdateMessage(this));
    }

    public void update(FlowComponent component)
    {
        ComponentMenu menu = component.getMenus().get(0);
        if (menu instanceof ComponentMenuRF)
        {
            if (((ComponentMenuRF)menu).isSelected(this))
            {
                if (!components.contains(component))
                {
                    components.add(component);
                    updateConnections();
                }
            } else
            {
                if (components.contains(component))
                {
                    components.remove(component);
                    updateConnections();
                }
            }
        }
    }

    private void updateConnections()
    {
        for (FlowComponent component : components)
        {
            boolean[] array = getSides(component.getMenus().get(0) instanceof ComponentMenuRFInput);
            ComponentMenuTargetRF target = (ComponentMenuTargetRF)component.getMenus().get(1);
            for (int i = 0; i < 6; i++)
            {
                boolean active = target.isActive(i);
                if (active != array[i]) updated = true;
                array[i] = active;
            }
        }
    }

    private boolean[] getSides(boolean input)
    {
        return input ? inputSides : outputSides;
    }
}
