package stevesaddons.items;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import stevesaddons.StevesAddons;
import stevesaddons.naming.NameRegistry;
import stevesaddons.reference.Names;
import stevesaddons.reference.Reference;
import vswe.stevesfactory.blocks.*;

import java.util.ArrayList;
import java.util.List;

public class ItemLabeler extends Item
{
    public ItemLabeler()
    {
        this.setCreativeTab(ModBlocks.creativeTab);
        this.setUnlocalizedName(Names.LABELER);
        this.setTextureName(Reference.ID.toLowerCase() + ":" + Names.LABELER);
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            player.openGui(StevesAddons.INSTANCE, 0, world, player.chunkCoordX, player.chunkCoordY, player.chunkCoordZ);
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
        super.onUsingTick(stack, player, count);
    }

    public static boolean isValidTile(World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInventory || te instanceof IFluidHandler || te instanceof IEnergyProvider || te instanceof IEnergyReceiver || te instanceof TileEntityInput ||
                te instanceof TileEntityOutput || te instanceof IRedstoneNode || te instanceof TileEntityCamouflage || te instanceof TileEntityBUD || te instanceof TileEntitySignUpdater;
    }

    public static List<String> getSavedStrings(ItemStack stack)
    {
        List<String> result = new ArrayList<String>();
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) return result;
        NBTTagList tagList = tagCompound.getTagList("saved", 8);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            result.add(tagList.getStringTagAt(i));
        }
        return result;
    }

    public static void setLabel(ItemStack stack, String string)
    {
        stack.getTagCompound().setString("Label",string);
    }

    public static void saveStrings(ItemStack stack, List<String> strings)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        NBTTagList tagList = new NBTTagList();
        for (String string : strings)
            tagList.appendTag(new NBTTagString(string));
        tagCompound.setTag("saved", tagList);
    }

    public static String getLabel(ItemStack stack)
    {
        return stack.hasTagCompound()?stack.getTagCompound().getString("Label"):"";
    }

    @Override
    @SuppressWarnings(value="unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extra)
    {
        super.addInformation(stack, player, list, extra);
        String label = getLabel(stack);
        if (label.isEmpty()) list.add("Clear Label");
        else list.add("Label: "+label);
    }


}
