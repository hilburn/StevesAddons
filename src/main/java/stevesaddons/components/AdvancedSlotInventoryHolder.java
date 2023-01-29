package stevesaddons.components;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

import stevesaddons.api.IHiddenInventory;
import stevesaddons.api.IHiddenTank;
import stevesaddons.reference.Null;
import vswe.stevesfactory.components.SlotInventoryHolder;

public class AdvancedSlotInventoryHolder extends SlotInventoryHolder {

    public AdvancedSlotInventoryHolder(int id, TileEntity inventory, int sharedOption) {
        super(id, inventory, sharedOption);
    }

    @Override
    public IInventory getInventory() {
        return getTile() instanceof IHiddenInventory ? Null.NULL_INVENTORY : super.getInventory();
    }

    @Override
    public IFluidHandler getTank() {
        return getTile() instanceof IHiddenTank ? ((IHiddenTank) getTile()).getTank() : super.getTank();
    }
}
