package stevesaddons.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class RFDataFixer implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor,
            IWailaConfigHandler iWailaConfigHandler) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor,
            IWailaConfigHandler iWailaConfigHandler) {
        if (((IEnergyProvider) iWailaDataAccessor.getTileEntity()).getMaxEnergyStored(ForgeDirection.UNKNOWN) == -1)
            ((ITaggedList) list).removeEntries("RFEnergyStorage");
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor,
            IWailaConfigHandler iWailaConfigHandler) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity,
            NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2) {
        return null;
    }
}
