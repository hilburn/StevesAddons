package stevesaddons.naming;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import stevesaddons.items.ItemLabeler;
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
                    NameRegistry.removeName(world, x, y, z);
                }else
                {
                    NameRegistry.saveName(world, x, y, z, label);
                }
            }
            event.setCanceled(true);
        }
    }

    private static boolean isLabeler(ItemStack stack)
    {
        return stack!=null && stack.getItem() == ItemRegistry.labeler;
    }

    @SubscribeEvent
    public void renderEvent(RenderWorldLastEvent event)
    {
        try
        {
            doEvent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void doEvent()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (!isLabeler(mc.thePlayer.getCurrentEquippedItem()) || mc.renderEngine == null || RenderManager.instance == null || RenderManager.instance.getFontRenderer() == null || mc.gameSettings.thirdPersonView != 0 || mc.objectMouseOver == null) return;
        switch (mc.objectMouseOver.typeOfHit)
        {
            case BLOCK:
                BlockCoord coord = new BlockCoord(mc.objectMouseOver.blockX,mc.objectMouseOver.blockY,mc.objectMouseOver.blockZ);
                String name = NameRegistry.getSavedName(mc.theWorld.provider.dimensionId, coord);
                if (name != null)
                {
                    //TODO: Fancy render shizz
                }
                break;
        }
    }
}
