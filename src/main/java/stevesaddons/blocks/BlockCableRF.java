package stevesaddons.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import stevesaddons.reference.Names;
import stevesaddons.reference.Reference;
import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.blocks.ModBlocks;

public class BlockCableRF extends BlockContainer
{
    public BlockCableRF()
    {
        super(Material.iron);
        this.setCreativeTab(ModBlocks.creativeTab);
        this.setStepSound(soundTypeMetal);
        this.setBlockName(Names.CABLE_RF);
        this.setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityRFNode();
    }

    @Override
    public void registerBlockIcons(IIconRegister registry)
    {
        this.blockIcon = registry.registerIcon(Reference.ID+":"+ Names.CABLE_RF);
    }
}
