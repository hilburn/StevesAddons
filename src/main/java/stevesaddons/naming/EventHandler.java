package stevesaddons.naming;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import stevesaddons.items.ItemLabeler;
import stevesaddons.network.MessageHandler;
import stevesaddons.network.message.SearchRegistryGenerateMessage;
import stevesaddons.registry.ItemRegistry;

public class EventHandler
{
    @SubscribeEvent
    public void playerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
            NameRegistry.syncNameData((EntityPlayerMP)event.player);
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event)
    {
        NameRegistry.removeName(event.world, event.x, event.y, event.z);
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event)
    {
        WorldSavedData data = event.world.perWorldStorage.loadData(NameData.class, NameData.KEY);
        if (data!=null)
            NameRegistry.setWorldData(event.world.provider.dimensionId, (NameData) data);
    }

    @SubscribeEvent
    public void worldSave(WorldEvent.Save event)
    {
        NameData nameData = NameRegistry.getWorldData(event.world.provider.dimensionId, false);
        if (nameData != null)
            event.world.perWorldStorage.setData(NameData.KEY, nameData);
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event)
    {
        NameData nameData = NameRegistry.getWorldData(event.world.provider.dimensionId, false);
        if (nameData != null)
            event.world.perWorldStorage.setData(NameData.KEY, nameData);
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
            MessageHandler.INSTANCE.sendTo(new SearchRegistryGenerateMessage(),(EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event)
    {
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && isLabeler(stack))
        {
            World world = event.world;
            int x = event.x;
            int y = event.y;
            int z = event.z;
            if (ItemLabeler.isValidTile(world, x, y, z))
            {
                String label = ItemLabeler.getLabel(stack);
                if (label.isEmpty())
                {
                    if (NameRegistry.removeName(world, x, y, z))
                    {
                        event.entityPlayer.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("stevesaddons.chat.cleared")));
                    }
                }else
                {
                    NameRegistry.saveName(world, x, y, z, label);
                    event.entityPlayer.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocalFormatted("stevesaddons.chat.saved", label)));
                }
                event.setCanceled(true);
            }
        }
    }

    private static boolean isLabeler(ItemStack stack)
    {
        return stack!=null && stack.getItem() == ItemRegistry.labeler;
    }
}
