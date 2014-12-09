package stevesaddons;


import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.network.MessageHandler;
import stevesaddons.proxy.CommonProxy;
import stevesaddons.reference.Reference;
import stevesaddons.registry.BlockRegistry;
import stevesaddons.registry.ItemRegistry;


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
        new StevesEnum();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        ItemRegistry.registerRecipes();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {

    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        StevesEnum.replaceCluster();
    }
}