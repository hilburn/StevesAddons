package stevesaddons.tileentities;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import stevesaddons.components.ComponentMenuRF;
import stevesaddons.components.ComponentMenuTargetRF;
import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.blocks.*;
import vswe.stevesfactory.components.ComponentMenuResult;
import vswe.stevesfactory.components.Connection;
import vswe.stevesfactory.components.FlowComponent;

import java.util.*;

public class TileEntityRFNode extends TileEntityClusterElement implements IEnergyHandler, ISystemListener
{
    private boolean[] inputSides = new boolean[6];
    private boolean[] outputSides = new boolean[6];
    private boolean[] oldConnectionSides = new boolean[6];
    private int tickCount;
    Set<TileEntityRFNode> connections = new HashSet<TileEntityRFNode>();
    Set<TileEntityManager> managers = new HashSet<TileEntityManager>();
    private static final String MANAGERS = "Managers";
    private static final String X="x";
    private static final String Y="y";
    private static final String Z="z";
    private List<WorldCoordinate> addManagers = new ArrayList<WorldCoordinate>();
    private boolean loaded;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!loaded) loadManagers();
        if (!worldObj.isRemote && tickCount++>=20)
        {
            tickCount = 0;
            updateConnections();
        }
        //TODO: load inventory list in connected managers
    }

    private void loadManagers()
    {
        if (!worldObj.isRemote)
        {
            for (WorldCoordinate manager : addManagers)
            {
                TileEntity te = worldObj.getTileEntity(manager.getX(), manager.getY(), manager.getZ());
                if (te instanceof TileEntityManager)
                {
                    TileEntityManager tileEntityManager = (TileEntityManager) te;
                    tileEntityManager.updateInventories();
                    managers.add(tileEntityManager);
                }
            }
        }
        addManagers=null;
        loaded=true;
    }

    private void updateConnections()
    {
        connections = new HashSet<TileEntityRFNode>();
        inputSides = new boolean[6];
        outputSides = new boolean[6];
        for (TileEntityManager tileEntityManager:managers)
        {
            Map<Integer,TileEntityRFNode> rfNodeMap = getRFNodeMap(tileEntityManager);
            Integer thisInt = null;
            for (Map.Entry<Integer,TileEntityRFNode> entry : rfNodeMap.entrySet())
            {
                if (entry.getValue() == this)
                {
                    thisInt = entry.getKey();
                    break;
                }
            }
            if (thisInt==null) continue;
            Set<Integer> visited = new TreeSet<Integer>();
            List<FlowComponent> items = tileEntityManager.getFlowItems();
            for (FlowComponent component: items)
            {
                if (component.getType()!= StevesEnum.RF_COMPONENT || visited.contains(component.getId())) continue;
                ComponentMenuRF menu = (ComponentMenuRF)component.getMenus().get(0);
                if (menu.getSelectedInventories().contains(thisInt))
                {
                    scanConnections(rfNodeMap, visited, component,items);
                }
            }
        }
        checkForConnectionUpdate();
    }

    private void checkForConnectionUpdate()
    {
        for (int i=0; i<6; i++)
        {
            if ((inputSides[i]|outputSides[i])!=oldConnectionSides[i])
            {
                notifyNeighbour(i);
                oldConnectionSides[i] = inputSides[i]|outputSides[i];
            }
        }
    }

    private void notifyNeighbour(int i)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(i);
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;
        worldObj.notifyBlockOfNeighborChange(x,y,z,worldObj.getBlock(xCoord,yCoord,zCoord));
    }

    private void scanConnections(Map<Integer,TileEntityRFNode> rfNodeMap, Set<Integer> visited, FlowComponent component, List<FlowComponent> components)
    {
        if (visited.contains(component.getId())) return;
        visited.add(component.getId());
        if (component.getType()== StevesEnum.RF_COMPONENT)
        {
            ComponentMenuRF menu = (ComponentMenuRF)component.getMenus().get(0);
            for (int inventory : menu.getSelectedInventories())
            {
                TileEntityRFNode node = rfNodeMap.get(inventory);
                if (node==null) continue;
                if (node == this)
                {
                    ComponentMenuTargetRF targetRF = (ComponentMenuTargetRF)component.getMenus().get(1);
                    ComponentMenuResult result = (ComponentMenuResult)component.getMenus().get(2);
                    NBTTagCompound dataRetriever = new NBTTagCompound();
                    result.writeToNBT(dataRetriever,false);
                    boolean output = dataRetriever.getByte("SelectedOption")==1;
                    for (int i = 0; i<6;i++)
                        if (output)
                            outputSides[i]|=targetRF.isActive(i);
                        else
                            inputSides[i]|=targetRF.isActive(i);

                }
                connections.add(node);
            }
        }
        for (Connection i : component.getConnections().values())
        {
            if (i==null) continue;
            scanConnections(rfNodeMap, visited,components.get(i.getComponentId()),components);
        }
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList managers = new NBTTagList();
        for (TileEntityManager manager:this.managers)
        {
            NBTTagCompound thisManager = new NBTTagCompound();
            thisManager.setInteger(X,manager.xCoord);
            thisManager.setInteger(Y,manager.yCoord);
            thisManager.setInteger(Z,manager.zCoord);
            managers.appendTag(thisManager);
        }
        tagCompound.setTag(MANAGERS,managers);
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList managers = tagCompound.getTagList(MANAGERS,10);
        for (int i = 0; i<managers.tagCount(); i++)
        {
            NBTTagCompound manager = managers.getCompoundTagAt(i);
            int x = manager.getInteger(X);
            int y = manager.getInteger(Y);
            int z = manager.getInteger(Z);
            this.addManagers.add(new WorldCoordinate(x,y,z));
        }

    }

    private Map<Integer, TileEntityRFNode> getRFNodeMap(TileEntityManager tileEntityManager)
    {
        Map<Integer,TileEntityRFNode> result = new HashMap<Integer, TileEntityRFNode>();
        for (ConnectionBlock connectionBlock : tileEntityManager.getConnectedInventories())
        {
            if (connectionBlock.isOfType(StevesEnum.RF))
            {
                result.put(connectionBlock.getId(),(TileEntityRFNode)connectionBlock.getTileEntity());
            }
        }
        return result;
    }


    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        int toReceive = maxReceive;
        if (from != ForgeDirection.UNKNOWN)
        {
            if (!inputSides[from.ordinal()]) return 0;
            Set<TileEntityRFNode> outputs = new HashSet<TileEntityRFNode>();
            int outputPacket = toReceive/connections.size();
            for (TileEntityRFNode connected : connections)
            {
                if (connected.receiveEnergy(ForgeDirection.UNKNOWN,outputPacket,true)>0) outputs.add(connected);
            }
            outputPacket = maxReceive/outputs.size();
            for (TileEntityRFNode connected : outputs)
            {
                toReceive-=connected.receiveEnergy(ForgeDirection.UNKNOWN,outputPacket,simulate);
                if (toReceive==0) return maxReceive;
            }
        }
        else
        {
            for (int i = 0; i< outputSides.length; i++)
            {
                if (!outputSides[i]) continue;
                ForgeDirection dir = ForgeDirection.getOrientation(i);
                int x = xCoord + dir.offsetX;
                int y = yCoord + dir.offsetY;
                int z = zCoord + dir.offsetZ;
                TileEntity te = worldObj.getTileEntity(x,y,z);
                if (te instanceof IEnergyHandler)
                    toReceive-=((IEnergyHandler)te).receiveEnergy(ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[i]),toReceive,simulate);
                if (toReceive==0) return maxReceive;
            }
        }
        return maxReceive-toReceive;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int toExtract = maxExtract;
        if (from != ForgeDirection.UNKNOWN)
        {
            if (!outputSides[from.ordinal()]) return 0;
            int outputPacket = toExtract/connections.size();
            Set<TileEntityRFNode> inputs = new HashSet<TileEntityRFNode>();
            for (TileEntityRFNode connected : connections)
            {
                if (connected.extractEnergy(ForgeDirection.UNKNOWN,outputPacket,true)>0) inputs.add(connected);
            }
            outputPacket = toExtract/inputs.size();
            for (TileEntityRFNode connected : inputs)
            {
                toExtract-=connected.extractEnergy(ForgeDirection.UNKNOWN,outputPacket,simulate);
                if (toExtract==0) return maxExtract;
            }
        }
        else
        {
            for (int i = 0; i< outputSides.length; i++)
            {
                if (!inputSides[i]) continue;
                ForgeDirection dir = ForgeDirection.getOrientation(i);
                int x = xCoord + dir.offsetX;
                int y = yCoord + dir.offsetY;
                int z = zCoord + dir.offsetZ;
                TileEntity te = worldObj.getTileEntity(x,y,z);
                if (te instanceof IEnergyHandler)
                    toExtract-=((IEnergyHandler)te).extractEnergy(ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[i]), toExtract, simulate);
                if (toExtract==0) return maxExtract;
            }
        }
        return maxExtract-toExtract;
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
}
