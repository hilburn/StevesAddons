package stevesaddons;


import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import stevesaddons.helpers.Config;
import stevesaddons.interfaces.GuiHandler;
import stevesaddons.naming.EventHandler;
import stevesaddons.naming.NameData;
import stevesaddons.naming.NameRegistry;
import stevesaddons.naming.WailaLabelProvider;
import stevesaddons.network.MessageHandler;
import stevesaddons.proxy.CommonProxy;
import stevesaddons.recipes.ClusterUncraftingRecipe;
import stevesaddons.reference.Metadata;
import stevesaddons.reference.Reference;
import stevesaddons.registry.BlockRegistry;
import stevesaddons.registry.CommandRegistry;
import stevesaddons.registry.ItemRegistry;
import vswe.stevesfactory.blocks.TileEntityManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;


@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL, dependencies = "required-after:StevesFactoryManager")
public class StevesAddons
{
    @Mod.Instance(value = Reference.ID)
    public static StevesAddons INSTANCE;

    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "stevesaddons.proxy.ClientProxy", serverSide = "stevesaddons.proxy.CommonProxy")
    public static CommonProxy PROXY;

    public static GuiHandler guiHandler = new GuiHandler();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = Metadata.init(metadata);
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
        MessageHandler.init();
        Config.init(event.getSuggestedConfigurationFile());
        NetworkRegistry.INSTANCE.registerGuiHandler(StevesAddons.INSTANCE, guiHandler);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        ItemRegistry.registerRecipes();
        BlockRegistry.registerRecipes();
        ClusterUncraftingRecipe uncrafting = new ClusterUncraftingRecipe();
        GameRegistry.addRecipe(uncrafting);
        FMLCommonHandler.instance().bus().register(uncrafting);
        EventHandler handler = new EventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
    }

    @Mod.EventHandler
    @SuppressWarnings(value="unchecked")
    public void postInit(FMLPostInitializationEvent e)
    {
        if (Config.wailaIntegration && Loader.isModLoaded("Waila"))
        {
            WailaLabelProvider.register();
        }
        if (Loader.isModLoaded("JABBA"))
        {
            try
            {
                Class dolly = Class.forName("mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover");
                Field classExtensions = dolly.getDeclaredField("classExtensions");
                Field classExtensionsNames = dolly.getDeclaredField("classExtensionsNames");
                Field classMap = dolly.getDeclaredField("classMap");
                classExtensions.setAccessible(true);
                classExtensionsNames.setAccessible(true);
                classMap.setAccessible(true);
                ArrayList<Class> extensions = (ArrayList<Class>)classExtensions.get(null);
                ArrayList<String> extensionsNames = (ArrayList<String>)classExtensionsNames.get(null);
                HashMap<String, Class> map = (HashMap<String, Class>)classMap.get(null);
                extensions.add(TileEntityManager.class);
                extensionsNames.add(TileEntityManager.class.getSimpleName());
                map.put(TileEntityManager.class.getSimpleName(), TileEntityManager.class);
            } catch (Exception ignore)
            {
            }
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        NameRegistry.setNameData(new HashMap<Integer, NameData>());
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(CommandRegistry.instance);
    }
}