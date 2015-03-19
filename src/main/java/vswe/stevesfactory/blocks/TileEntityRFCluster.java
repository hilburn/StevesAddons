package vswe.stevesfactory.blocks;

import cofh.api.energy.IEnergyHandler;
import net.minecraftforge.common.util.ForgeDirection;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.tileentities.TileEntityRFNode;

import java.util.*;

public class TileEntityRFCluster extends TileEntityCluster implements IEnergyHandler
{

    private List getRegistrations(ClusterMethodRegistration clusterMethodRegistration)
    {
        return new ArrayList(); //This gets ASMed out when the access transformers take place
    }

    private TileEntityRFNode getTileEntity(Object i)
    {
        return null; //This return value gets ASMed int when the access transformers take place
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        int toReceive = 0;
        for (Object i : getRegistrations(StevesEnum.RECEIVE_ENERGY))
        {
            toReceive += getTileEntity(i).receiveEnergy(from, maxReceive - toReceive, simulate);
        }
        return toReceive;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int toExtract = maxExtract;
        for (Object i : getRegistrations(StevesEnum.EXTRACT_ENERGY))
        {
            toExtract -= getTileEntity(i).extractEnergy(from, toExtract, simulate);
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
        for (Object i : getRegistrations(StevesEnum.CONNECT_ENERGY))
        {
            if (getTileEntity(i).canConnectEnergy(from)) return true;
        }
        return false;
    }
}
