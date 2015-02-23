package stevesaddons;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.network.MessageHandler;
import stevesaddons.proxy.CommonProxy;
import stevesaddons.recipes.ClusterUncraftingRecipe;
import stevesaddons.reference.Reference;
import stevesaddons.registry.BlockRegistry;
import stevesaddons.registry.ItemRegistry;
import vswe.stevesfactory.blocks.TileEntityManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;


@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL, dependencies = "required-after:StevesFactoryManager")
public class StevesAddons
{
    @Mod.Instance(value = Reference.ID)
    public static StevesAddons INSTANCE = new StevesAddons();

    @SidedProxy(clientSide = "stevesaddons.proxy.ClientProxy", serverSide = "stevesaddons.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
        MessageHandler.init();
        StevesEnum.replaceBlocks();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        ItemRegistry.registerRecipes();
        BlockRegistry.registerRecipes();
        ClusterUncraftingRecipe uncrafting = new ClusterUncraftingRecipe();
        GameRegistry.addRecipe(uncrafting);
        FMLCommonHandler.instance().bus().register(uncrafting);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
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
                ArrayList<Class>  extensions = (ArrayList<Class>) classExtensions.get(null);
                ArrayList<String> extensionsNames = (ArrayList<String>) classExtensionsNames.get(null);
                HashMap<String, Class> map = (HashMap<String, Class>) classMap.get(null);
                extensions.add(TileEntityManager.class);
                extensionsNames.add(TileEntityManager.class.getSimpleName());
                map.put(TileEntityManager.class.getSimpleName(),TileEntityManager.class);
            } catch (Exception e1){}
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {

    }
}