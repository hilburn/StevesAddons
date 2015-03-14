package stevesaddons.threading;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.components.ScrollController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SearchItems implements Runnable
{
    public static Map<String, ItemStack> items = new LinkedHashMap<String, ItemStack>();
    public static Map<String, ItemStack> advancedItems = new LinkedHashMap<String, ItemStack>();

    private String search;
    private ScrollController controller;
    private boolean showAll;

    public SearchItems(String search, ScrollController controller, boolean showAll)
    {
        this.search = search;
        this.controller = controller;
        this.showAll = showAll;
    }

    @Override
    public void run()
    {
        List<ItemStack> itemStacks = new ArrayList<ItemStack>();
        if (search.equals(".inv"))
        {
            InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
            for (int itemStack = 0; itemStack < inventory.getSizeInventory(); ++itemStack)
            {
                ItemStack stack = inventory.getStackInSlot(itemStack);
                if (stack != null)
                {
                    stack = stack.copy();
                    stack.stackSize = 1;
                    itemStacks.add(stack);
                }
            }
        } else
        {
            if (!showAll)
            {
                Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
                for (Map.Entry<String, ItemStack> entry : Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? advancedItems.entrySet() : items.entrySet())
                {
                    if (pattern.matcher(entry.getKey()).find()) itemStacks.add(entry.getValue());
                }
            } else
            {
                itemStacks.addAll(Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? advancedItems.values() : items.values());
            }
        }
        setResult(this.controller, itemStacks);
    }

    public static void setResult(ScrollController controller, List<ItemStack> stackList)
    {
        controller.getResult().clear();
        controller.getResult().addAll(stackList);
    }

    public static void setItems()
    {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (Object anItemRegistry : Item.itemRegistry)
        {
            Item item = (Item)anItemRegistry;
            item.getSubItems(item, null, stacks);
        }
        for (ItemStack stack : stacks)
        {
            String searchString = "";
            List tooltipList;
            List advTooltipList;
            try
            {
                tooltipList = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                advTooltipList = stack.getTooltip(Minecraft.getMinecraft().thePlayer, true);
            } catch (Throwable var11)
            {
                continue;
            }
            for (Object string : tooltipList)
            {
                if (string != null)
                    searchString += string + "\n";
            }
            items.put(searchString, stack);
            for (Object string : advTooltipList)
            {
                if (string != null)
                    searchString += string + "\n";
            }
            advancedItems.put(searchString, stack);
        }
    }
}
