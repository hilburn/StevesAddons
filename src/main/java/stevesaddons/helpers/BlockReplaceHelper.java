package stevesaddons.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;

public class BlockReplaceHelper{
    public static void replaceBlock(Class toReplaceIn, Block toReplace, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemBlockClass){

        Class<?>[] classTest = new Class<?>[4];
        Exception exception = null;

        try{
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);

            for(Field blockField:toReplaceIn.getDeclaredFields()){
                if (Block.class.isAssignableFrom(blockField.getType())){
                    Block block = (Block)blockField.get(null);

                    if (block == toReplace){
                        String registryName = Block.blockRegistry.getNameForObject(block);
                        int id = Block.getIdFromBlock(block);

                        Block newBlock = blockClass.newInstance();
                        FMLControlledNamespacedRegistry<Block> registryBlocks = GameData.getBlockRegistry();
                        Field map1 = RegistrySimple.class.getDeclaredFields()[1];
                        map1.setAccessible(true);
                        ((Map)map1.get(registryBlocks)).put(registryName,newBlock);

                        Field map2 = RegistryNamespaced.class.getDeclaredFields()[0];
                        map2.setAccessible(true);
                        ((ObjectIntIdentityMap)map2.get(registryBlocks)).func_148746_a(newBlock,id); // OBFUSCATED put object

                        blockField.setAccessible(true);
                        modifiersField.setInt(blockField,blockField.getModifiers() & ~Modifier.FINAL);
                        blockField.set(null,newBlock);

                        ItemBlock itemBlock = itemBlockClass.getConstructor(Block.class).newInstance(newBlock);
                        FMLControlledNamespacedRegistry<Item> registryItems = GameData.getItemRegistry();
                        ((Map)map1.get(registryItems)).put(registryName,itemBlock);
                        ((ObjectIntIdentityMap)map2.get(registryItems)).func_148746_a(itemBlock,id); // OBFUSCATED put object

                        classTest[0] = blockField.get(null).getClass();
                        classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
                        classTest[2] = ((ItemBlock)Item.getItemFromBlock(newBlock)).field_150939_a.getClass();
                        classTest[3] = Item.getItemFromBlock(newBlock).getClass();
                    }
                }
            }
        }catch(Exception e){
            exception = e;
        }

        if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null || classTest[3] != itemBlockClass){
            throw new RuntimeException("HardcoreEnderExpansion was unable to replace block "+toReplace.getUnlocalizedName()+"! Debug info to report: "+classTest[0]+","+classTest[1]+","+classTest[2]+","+classTest[3],exception);
        }
    }
}
