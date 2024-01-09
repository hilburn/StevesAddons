package stevesaddons.waila;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import mcp.mobius.waila.api.IWailaRegistrar;
import vswe.stevesfactory.blocks.TileEntityCluster;
import vswe.stevesfactory.blocks.TileEntityClusterElement;
import vswe.stevesfactory.blocks.TileEntityRFCluster;

public class WailaManager {

    public static void callbackRegister(IWailaRegistrar register) {
        register.registerBodyProvider(new RFDataFixer(), TileEntityRFCluster.class);

        WailaLabelProvider labelProvider = new WailaLabelProvider();
        register.registerBodyProvider(labelProvider, IInventory.class);
        register.registerBodyProvider(labelProvider, IFluidHandler.class);
        register.registerBodyProvider(labelProvider, IEnergyProvider.class);
        register.registerBodyProvider(labelProvider, IEnergyReceiver.class);
        register.registerBodyProvider(labelProvider, TileEntityClusterElement.class);
        register.registerBodyProvider(labelProvider, TileEntityCluster.class);
    }
}
