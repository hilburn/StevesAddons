package stevesaddons.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class GuiEmptyContainer extends Container
{
    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return false;
    }
}
