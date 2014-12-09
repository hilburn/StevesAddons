package stevesaddons.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import stevesaddons.blocks.BlockCableRF;
import stevesaddons.reference.Names;
import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.blocks.ClusterRegistry;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.blocks.TileEntityRFCluster;
import vswe.stevesfactory.blocks.TileEntityRFManager;

public class BlockRegistry
{
    public static BlockCableRF cableRFNode;

    public static void registerBlocks()
    {
        GameRegistry.registerTileEntity(TileEntityRFNode.class, Names.CABLE_RF);
        GameRegistry.registerBlock(cableRFNode = new BlockCableRF(), Names.CABLE_RF);
        ClusterRegistry.register(TileEntityRFNode.class, cableRFNode);

        GameRegistry.registerTileEntity(TileEntityRFCluster.class, Names.CABLE_RF+"Cluster");
        GameRegistry.registerTileEntity(TileEntityRFManager.class, "TileEntityFManager");
    }

    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(cableRFNode),"RRR","RCR","RRR",'R', new ItemStack(Items.redstone), 'C', new ItemStack(ModBlocks.blockCable));
    }
}
