package stevesaddons.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.blocks.TileEntityRFManager;
import vswe.stevesfactory.blocks.BlockManager;

public class BlockRFManager extends BlockManager
{
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityRFManager();
    }
}
