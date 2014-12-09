package stevesaddons.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.blocks.BlockCableCluster;
import vswe.stevesfactory.blocks.TileEntityRFCluster;

public class BlockCableRFCluster extends BlockCableCluster
{
    public BlockCableRFCluster()
    {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityRFCluster();
    }

}
