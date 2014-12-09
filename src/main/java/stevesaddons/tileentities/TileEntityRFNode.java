package stevesaddons.tileentities;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.network.MessageHandler;
import stevesaddons.network.MessageHelper;
import stevesaddons.network.message.RFNodeUpdateMessage;
import vswe.stevesfactory.blocks.*;

import java.util.*;

public class TileEntityRFNode extends TileEntityClusterElement implements IEnergyHandler, ISystemListener
{
    private boolean[] inputSides = new boolean[6];
    private boolean[] outputSides = new boolean[6];
    private boolean[] oldSides = new boolean[12];
    Set<TileEntityManager> managers = new HashSet<TileEntityManager>();
    private static final String MANAGERS = "Managers";
    private static final String INPUTS = "Inputs";
    private static final String OUTPUTS = "Outputs";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private List<WorldCoordinate> addManagers = new ArrayList<WorldCoordinate>();
    private boolean updated;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.isPartOfCluster() && updated && checkUpdate()) sendUpdatePacket();
    }

    private boolean checkUpdate()
    {
        if (worldObj.isRemote) return false;
        updated = false;
        for (int i = 0; i < 6; i++)
        {
            if (inputSides[i] != oldSides[i] || outputSides[i] != oldSides[i + 6]) return true;
        }
        return false;
    }

    private void sendUpdatePacket()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            MessageHandler.INSTANCE.sendToAll(new RFNodeUpdateMessage(this));
            for (int i = 0; i < 6; i++)
            {
                oldSides[i] = inputSides[i];
                oldSides[i + 6] = outputSides[i];
            }
        }
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList managers = new NBTTagList();
        for (TileEntityManager manager : this.managers)
        {
            NBTTagCompound thisManager = new NBTTagCompound();
            thisManager.setInteger(X, manager.xCoord);
            thisManager.setInteger(Y, manager.yCoord);
            thisManager.setInteger(Z, manager.zCoord);
            managers.appendTag(thisManager);
        }
        tagCompound.setTag(MANAGERS, managers);
        tagCompound.setByte(INPUTS, MessageHelper.booleanToByte(inputSides));
        tagCompound.setByte(OUTPUTS, MessageHelper.booleanToByte(outputSides));
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList managers = tagCompound.getTagList(MANAGERS, 10);
        for (int i = 0; i < managers.tagCount(); i++)
        {
            NBTTagCompound manager = managers.getCompoundTagAt(i);
            int x = manager.getInteger(X);
            int y = manager.getInteger(Y);
            int z = manager.getInteger(Z);
            this.addManagers.add(new WorldCoordinate(x, y, z));
        }
        inputSides = MessageHelper.byteToBooleanArray(tagCompound.getByte(INPUTS));
        outputSides = MessageHelper.byteToBooleanArray(tagCompound.getByte(OUTPUTS));
        updated = true;
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
                if (te != null && te instanceof IEnergyHandler)
                {
                    toReceive -= ((IEnergyHandler) te).receiveEnergy(ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[i]), toReceive, simulate);
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
                if (te != null && te instanceof IEnergyHandler)
                {
                    toExtract -= ((IEnergyHandler) te).extractEnergy(ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[i]), toExtract, simulate);
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
        managers.add(tileEntityManager);
    }

    @Override
    public void removed(TileEntityManager tileEntityManager)
    {
        managers.remove(tileEntityManager);
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

    public void setInputSides(Integer[] sides)
    {
        if (!updated) resetArrays();
        for (int side : sides)
        {
            inputSides[side] = true;
        }
    }

    private void resetArrays()
    {
        inputSides = new boolean[6];
        outputSides = new boolean[6];
        updated = true;
    }

    public void setOutputSides(Integer[] sides)
    {
        if (!updated) resetArrays();
        for (int side : sides)
        {
            outputSides[side] = true;
        }
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
}
