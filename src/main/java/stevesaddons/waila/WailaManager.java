package stevesaddons.waila;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;
import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.blocks.TileEntityCluster;
import vswe.stevesfactory.blocks.TileEntityClusterElement;
import vswe.stevesfactory.blocks.TileEntityRFCluster;

public class WailaManager
{
    public static void callbackRegister(IWailaRegistrar register)
    {
        register.registerBodyProvider(new RFDataFixer(), TileEntityRFCluster.class);
        register.registerBodyProvider(new RFDataFixer(), TileEntityRFNode.class);

        register.registerBodyProvider(new WailaLabelProvider(), IInventory.class);
        register.registerBodyProvider(new WailaLabelProvider(), IFluidHandler.class);
        register.registerBodyProvider(new WailaLabelProvider(), IEnergyProvider.class);
        register.registerBodyProvider(new WailaLabelProvider(), IEnergyReceiver.class);
        register.registerBodyProvider(new WailaLabelProvider(), TileEntityClusterElement.class);
        register.registerBodyProvider(new WailaLabelProvider(), TileEntityCluster.class);
    }
}
