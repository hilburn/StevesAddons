package stevesaddons;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stevesaddons.reference.Metadata;
import stevesaddons.reference.Reference;
import stevesaddons.registry.CommandRegistry;
import stevesaddons.threading.ThreadSafeHandler;

import java.util.Map;


@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL, dependencies = "required-after:StevesFactoryManager")
public class StevesAddons
{
    @Mod.Instance(value = Reference.ID)
    public static StevesAddons INSTANCE;

    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    public static Logger log = LogManager.getLogger(Reference.ID);

    @NetworkCheckHandler
    public final boolean networkCheck(Map<String, String> remoteVersions, Side side)
    {
        return side.isClient() || (side.isServer() && !remoteVersions.containsKey(Reference.ID));
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = Metadata.init(metadata);
        MinecraftForge.EVENT_BUS.register(new ThreadSafeHandler());
        FMLCommonHandler.instance().bus().register(this);
        ClientCommandHandler.instance.registerCommand(new CommandRegistry());
    }

    @SubscribeEvent
    public void playerLogIn(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        FMLCommonHandler.instance().bus().register(new EventHandler());
    }
}