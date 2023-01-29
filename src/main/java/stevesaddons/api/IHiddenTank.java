package stevesaddons.api;

import java.util.List;

import net.minecraftforge.fluids.IFluidHandler;

import vswe.stevesfactory.components.CommandExecutorRF;
import vswe.stevesfactory.components.ComponentMenuStuff;
import vswe.stevesfactory.components.LiquidBufferElement;
import vswe.stevesfactory.components.SlotInventoryHolder;

public interface IHiddenTank {

    IFluidHandler getTank();

    void addFluidsToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder tank,
            List<LiquidBufferElement> liquidBuffer, CommandExecutorRF commandExecutorRF);
}
