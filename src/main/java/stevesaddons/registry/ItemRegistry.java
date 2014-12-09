package stevesaddons.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import stevesaddons.items.ItemSFMDrive;
import stevesaddons.reference.Names;
import vswe.stevesfactory.blocks.ModBlocks;

public class ItemRegistry
{
    public static Item duplicator;

    public static void registerItems()
    {
        GameRegistry.registerItem(duplicator = new ItemSFMDrive(), Names.DRIVE);
    }

    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(duplicator), " x ", "xyx", " x ", 'x', "ingotIron", 'y', new ItemStack(ModBlocks.blockManager)));
    }
}
