package stevesaddons.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import stevesaddons.reference.Names;
import stevesaddons.reference.Reference;
import stevesaddons.tileentities.TileEntityMENode;
import vswe.stevesfactory.blocks.ModBlocks;

public class BlockCableME extends BlockContainer
{
    public BlockCableME()
    {
        super(Material.glass);
        this.setCreativeTab(ModBlocks.creativeTab);
        this.setStepSound(soundTypeGlass);
        this.setBlockName(Names.CABLE_ME);
        this.setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMENode();
    }

    @Override
    public void registerBlockIcons(IIconRegister ir)
    {
        this.blockIcon = ir.registerIcon(Reference.ID + ":" + Names.CABLE_ME);
    }
}
