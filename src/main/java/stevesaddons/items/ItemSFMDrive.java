package stevesaddons.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import stevesaddons.reference.Names;
import stevesaddons.reference.Reference;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.blocks.TileEntityManager;

import java.util.List;

public class ItemSFMDrive extends Item
{
    public ItemSFMDrive()
    {
        this.setCreativeTab(ModBlocks.creativeTab);
        this.setUnlocalizedName(Names.DRIVE);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        if (stack.hasTagCompound() && validateNBT(stack))
        {
            int x = stack.getTagCompound().getInteger("x");
            int y = stack.getTagCompound().getInteger("y");
            int z = stack.getTagCompound().getInteger("z");
            list.add("Data stored from Manager at:");
            list.add("x: "+x+" y: "+y+ " z: "+z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon((Reference.ID+":" + getUnlocalizedName()).toLowerCase());
    }

    private boolean validateNBT(ItemStack stack)
    {
        if (stack.getTagCompound().getString("id").equals("TileEntityMachineManagerName"))return true;
        stack.setTagCompound(null);
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!player.isSneaking()) return true;
        if (!world.isRemote )
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                if (stack.hasTagCompound() && validateNBT(stack))
                {
                    NBTTagCompound tagCompound = stack.getTagCompound();
                    tagCompound.setInteger("x", x);
                    tagCompound.setInteger("y", y);
                    tagCompound.setInteger("z", z);
                    te.readFromNBT(tagCompound);
                    stack.setTagCompound(null);
                }
                else
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    te.writeToNBT(tagCompound);
                    tagCompound.setTag("ench",new NBTTagList());
                    stack.setTagCompound(tagCompound);
                }
                return true;
            }
        }
        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
}
