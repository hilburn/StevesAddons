package stevesaddons;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import stevesaddons.threading.SearchItems;

public class EventHandler
{
    @SubscribeEvent
    public void worldTick(TickEvent.ClientTickEvent event)
    {
        if (event.side == Side.CLIENT)
        {
            SearchItems.setItems();
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }
}