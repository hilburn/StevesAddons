package vswe.stevesfactory.blocks;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.*;

public class TileEntityRFCluster extends TileEntityCluster implements IEnergyHandler
{
    private boolean requestedInfo;
    private List<TileEntityClusterElement> elements = new ArrayList();
    private List<ClusterRegistry> registryList = new ArrayList();
    private Map<ClusterMethodRegistration, List<TileEntityRFCluster.Pair>> methodRegistration = new HashMap();
    private ITileEntityInterface interfaceObject;
    private TileEntityCamouflage camouflageObject;
    private static final String NBT_SUB_BLOCKS = "SubBlocks";
    private static final String NBT_SUB_BLOCK_ID = "SubId";
    private static final String NBT_SUB_BLOCK_META = "SubMeta";

    public TileEntityRFCluster() {
        ClusterMethodRegistration[] arr$ = ClusterMethodRegistration.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            ClusterMethodRegistration clusterMethodRegistration = arr$[i$];
            this.methodRegistration.put(clusterMethodRegistration, new ArrayList());
        }

    }

    @Override
    public void loadElements(ItemStack itemStack) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if(compound != null && compound.hasKey("Cable")) {
            NBTTagCompound cable = compound.getCompoundTag("Cable");
            byte[] types = cable.getByteArray("Types");
            this.loadElements((byte[])types);
        }

    }

    private void loadElements(byte[] types) {
        this.registryList.clear();
        this.elements.clear();
        byte[] arr$ = types;
        int len$ = types.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte type = arr$[i$];
            ClusterRegistry block = (ClusterRegistry)ClusterRegistry.getRegistryList().get(type);
            this.registryList.add(block);
            TileEntityClusterElement element = (TileEntityClusterElement)block.getBlock().createNewTileEntity(this.getWorldObj(), 0);
            this.elements.add(element);
            if(element instanceof ITileEntityInterface) {
                this.interfaceObject = (ITileEntityInterface)element;
            } else if(element instanceof TileEntityCamouflage) {
                this.camouflageObject = (TileEntityCamouflage)element;
            }

            Iterator i$1 = element.getRegistrations().iterator();

            while(i$1.hasNext()) {
                ClusterMethodRegistration clusterMethodRegistration = (ClusterMethodRegistration)i$1.next();
                ((List)this.methodRegistration.get(clusterMethodRegistration)).add(new TileEntityRFCluster.Pair(block, element));
            }

            element.xCoord = this.xCoord;
            element.yCoord = this.yCoord;
            element.zCoord = this.zCoord;
            element.setWorldObj(this.worldObj);
            element.setPartOfCluster(true);
        }

    }

    @Override
    public List<TileEntityClusterElement> getElements() {
        return this.elements;
    }

    @Override
    public void updateEntity() {
        Iterator i$ = this.elements.iterator();

        while(i$.hasNext()) {
            TileEntityClusterElement element = (TileEntityClusterElement)i$.next();
            this.setWorldObject(element);
            element.updateEntity();
        }

        if(!this.requestedInfo && this.worldObj.isRemote) {
            this.requestedInfo = true;
            this.requestData();
        }

    }

    @Override
    public void setWorldObject(TileEntityClusterElement te) {
        if(!te.hasWorldObj()) {
            te.setWorldObj(this.worldObj);
        }

    }

    @SideOnly(Side.CLIENT)
    private void requestData() {
        PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 1);
    }

    private List<TileEntityRFCluster.Pair> getRegistrations(ClusterMethodRegistration method) {
        return (List)this.methodRegistration.get(method);
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack itemStack) {
        Iterator i$ = this.getRegistrations(ClusterMethodRegistration.ON_BLOCK_PLACED_BY).iterator();

        while(i$.hasNext()) {
            TileEntityRFCluster.Pair blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onBlockPlacedBy(this.worldObj, this.xCoord, this.yCoord, this.zCoord, entity, blockContainer.registry.getItemStack());
        }

    }

    @Override
    public void onNeighborBlockChange(Block block) {
        Iterator i$ = this.getRegistrations(ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED).iterator();

        while(i$.hasNext()) {
            TileEntityRFCluster.Pair blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onNeighborBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, block);
        }

    }

    @Override
    public boolean canConnectRedstone(int side) {
        Iterator i$ = this.getRegistrations(ClusterMethodRegistration.CAN_CONNECT_REDSTONE).iterator();

        TileEntityRFCluster.Pair blockContainer;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
        } while(!blockContainer.registry.getBlock().canConnectRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord, side));

        return true;
    }

    @Override
    public void onBlockAdded() {
        Iterator i$ = this.getRegistrations(ClusterMethodRegistration.ON_BLOCK_ADDED).iterator();

        while(i$.hasNext()) {
            TileEntityRFCluster.Pair blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onBlockAdded(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }

    }

    @Override
    public boolean shouldCheckWeakPower(int side) {
        Iterator i$ = this.getRegistrations(ClusterMethodRegistration.SHOULD_CHECK_WEAK_POWER).iterator();

        TileEntityRFCluster.Pair blockContainer;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
        } while(!blockContainer.registry.getBlock().shouldCheckWeakPower(this.worldObj, this.xCoord, this.yCoord, this.zCoord, side));

        return true;
    }

    @Override
    public int isProvidingWeakPower(int side) {
        int max = 0;

        TileEntityRFCluster.Pair blockContainer;
        for(Iterator i$ = this.getRegistrations(ClusterMethodRegistration.IS_PROVIDING_WEAK_POWER).iterator(); i$.hasNext(); max = Math.max(max, blockContainer.registry.getBlock().isProvidingWeakPower(this.worldObj, this.xCoord, this.yCoord, this.zCoord, side))) {
            blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
        }

        return max;
    }

    @Override
    public int isProvidingStrongPower(int side) {
        int max = 0;

        TileEntityRFCluster.Pair blockContainer;
        for(Iterator i$ = this.getRegistrations(ClusterMethodRegistration.IS_PROVIDING_STRONG_POWER).iterator(); i$.hasNext(); max = Math.max(max, blockContainer.registry.getBlock().isProvidingStrongPower(this.worldObj, this.xCoord, this.yCoord, this.zCoord, side))) {
            blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
        }

        return max;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        Iterator i$ = this.getRegistrations(ClusterMethodRegistration.ON_BLOCK_ACTIVATED).iterator();

        TileEntityRFCluster.Pair blockContainer;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            blockContainer = (TileEntityRFCluster.Pair)i$.next();
            this.setWorldObject(blockContainer.te);
        } while(!blockContainer.registry.getBlock().onBlockActivated(this.worldObj, this.xCoord, this.yCoord, this.zCoord, player, side, hitX, hitY, hitZ));

        return true;
    }

    @Override
    public Container getContainer(TileEntity te, InventoryPlayer inv) {
        return this.interfaceObject == null?null:this.interfaceObject.getContainer((TileEntity)this.interfaceObject, inv);
    }

    @Override
    public GuiScreen getGui(TileEntity te, InventoryPlayer inv) {
        return this.interfaceObject == null?null:this.interfaceObject.getGui((TileEntity)this.interfaceObject, inv);
    }

    @Override
    public void readAllData(DataReader dr, EntityPlayer player) {
        if(this.interfaceObject != null) {
            this.interfaceObject.readAllData(dr, player);
        }

    }

    @Override
    public void readUpdatedData(DataReader dr, EntityPlayer player) {
        if(this.interfaceObject != null) {
            this.interfaceObject.readUpdatedData(dr, player);
        }

    }

    @Override
    public void writeAllData(DataWriter dw) {
        if(this.interfaceObject != null) {
            this.interfaceObject.writeAllData(dw);
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagList subList = new NBTTagList();

        for(int i = 0; i < this.elements.size(); ++i) {
            TileEntityClusterElement element = (TileEntityClusterElement)this.elements.get(i);
            ClusterRegistry registryElement = (ClusterRegistry)this.registryList.get(i);
            NBTTagCompound sub = new NBTTagCompound();
            sub.setByte("SubId", (byte) registryElement.getId());
            sub.setByte("SubMeta", (byte) element.getBlockMetadata());
            element.writeContentToNBT(sub);
            subList.appendTag(sub);
        }

        tagCompound.setTag("SubBlocks", subList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList subList = tagCompound.getTagList("SubBlocks", 10);
        ArrayList bytes = new ArrayList();

        for(int byteArr = 0; byteArr < subList.tagCount(); ++byteArr) {
            NBTTagCompound i = subList.getCompoundTagAt(byteArr);
            bytes.add(Byte.valueOf(i.getByte("SubId")));
        }

        byte[] var8 = new byte[bytes.size()];

        int var9;
        for(var9 = 0; var9 < bytes.size(); ++var9) {
            var8[var9] = ((Byte)bytes.get(var9)).byteValue();
        }

        this.loadElements((byte[])var8);

        for(var9 = 0; var9 < subList.tagCount(); ++var9) {
            NBTTagCompound sub = subList.getCompoundTagAt(var9);
            TileEntityClusterElement element = (TileEntityClusterElement)this.elements.get(var9);
            element.setMetaData(sub.getByte("SubMeta"));
            element.readContentFromNBT(sub);
        }

    }

    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id) {
        if(id == 0) {
            if(this.camouflageObject != null) {
                this.camouflageObject.writeData(dw, player, onServer, id);
            }
        } else if(onServer) {
            dw.writeData(this.elements.size(), DataBitHelper.CLUSTER_SUB_ID);

            int i;
            for(i = 0; i < this.elements.size(); ++i) {
                dw.writeData((byte)((ClusterRegistry)this.registryList.get(i)).getId(), DataBitHelper.CLUSTER_SUB_ID);
            }

            for(i = 0; i < this.elements.size(); ++i) {
                dw.writeData((byte)((TileEntityClusterElement)this.elements.get(i)).func_145832_p(), DataBitHelper.BLOCK_META);
            }
        }

    }

    @Override
    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id) {
        if(id == 0) {
            if(this.camouflageObject != null) {
                this.camouflageObject.readData(dr, player, onServer, id);
            }
        } else if(onServer) {
            PacketHandler.sendBlockPacket(this, player, 1);
        } else {
            int length = dr.readData(DataBitHelper.CLUSTER_SUB_ID);
            byte[] types = new byte[length];

            int i;
            for(i = 0; i < length; ++i) {
                types[i] = (byte)dr.readData(DataBitHelper.CLUSTER_SUB_ID);
            }

            this.loadElements((byte[])types);

            for(i = 0; i < length; ++i) {
                ((TileEntityClusterElement)this.elements.get(i)).setMetaData(dr.readData(DataBitHelper.BLOCK_META));
            }
        }

    }

    @Override
    public int infoBitLength(boolean onServer) {
        return 1;
    }

    @Override
    public byte[] getTypes() {
        byte[] bytes = new byte[this.registryList.size()];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)((ClusterRegistry)this.registryList.get(i)).getId();
        }

        return bytes;
    }
    
    private class Pair {
        private ClusterRegistry registry;
        private TileEntityClusterElement te;

        private Pair(ClusterRegistry registry, TileEntityClusterElement te) {
            this.registry = registry;
            this.te = te;
        }
    }
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        Iterator i$ = this.getRegistrations(StevesEnum.RECEIVE_ENERGY).iterator();
        int toReceive = 0;
        while(i$.hasNext()) {
            TileEntityRFCluster.Pair blockContainer = (TileEntityRFCluster.Pair)i$.next();
            toReceive+=((TileEntityRFNode)blockContainer.te).receiveEnergy(from,maxReceive-toReceive,simulate);
        }
        return toReceive;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        Iterator i$ = this.getRegistrations(StevesEnum.EXTRACT_ENERGY).iterator();
        int toExtract = maxExtract;
        while(i$.hasNext()) {
            TileEntityRFCluster.Pair blockContainer = (TileEntityRFCluster.Pair)i$.next();
            toExtract-=((TileEntityRFNode)blockContainer.te).extractEnergy(from,toExtract,simulate);
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
        Iterator i$ = this.getRegistrations(StevesEnum.CONNECT_ENERGY).iterator();
        while(i$.hasNext()) {
            TileEntityRFCluster.Pair blockContainer = (TileEntityRFCluster.Pair)i$.next();
            if (((TileEntityRFNode)blockContainer.te).canConnectEnergy(from)) return true;
        }
        return false;
    }
}
