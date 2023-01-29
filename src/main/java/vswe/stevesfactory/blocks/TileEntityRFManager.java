package vswe.stevesfactory.blocks;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRFManager extends TileEntityManager {

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setString("id", "TileEntityMachineManagerName");
    }
}
