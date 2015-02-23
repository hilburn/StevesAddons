package vswe.stevesfactory.blocks;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRFManager extends TileEntityManager
{
    @Override
    public void func_145841_b(NBTTagCompound tagCompound)
    {
        super.func_145841_b(tagCompound);
        tagCompound.setString("id", "TileEntityMachineManagerName");
    }
}
