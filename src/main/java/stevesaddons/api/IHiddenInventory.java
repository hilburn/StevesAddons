package stevesaddons.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import vswe.stevesfactory.components.CommandExecutorRF;
import vswe.stevesfactory.components.ComponentMenuStuff;
import vswe.stevesfactory.components.ConditionSettingChecker;
import vswe.stevesfactory.components.ItemBufferElement;
import vswe.stevesfactory.components.ItemSetting;
import vswe.stevesfactory.components.Setting;
import vswe.stevesfactory.components.SlotInventoryHolder;

public interface IHiddenInventory {

    int getInsertable(ItemStack stack);

    void insertItemStack(ItemStack stack);

    void addItemsToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder inventory,
            List<ItemBufferElement> itemBuffer, CommandExecutorRF commandExecutorRF);

    void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap);

    int getExistingStackSize(ItemSetting setting);
}
