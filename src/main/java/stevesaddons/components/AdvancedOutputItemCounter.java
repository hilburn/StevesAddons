package stevesaddons.components;

import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import stevesaddons.api.IHiddenInventory;
import vswe.stevesfactory.components.ItemBufferElement;
import vswe.stevesfactory.components.ItemSetting;
import vswe.stevesfactory.components.Setting;
import vswe.stevesfactory.components.SlotInventoryHolder;

public class AdvancedOutputItemCounter {

    private Setting setting;
    private boolean useWhiteList;
    private int currentInventoryStackSize;
    private int currentBufferStackSize;

    public AdvancedOutputItemCounter(List<ItemBufferElement> itemBuffer, List<SlotInventoryHolder> inventories,
            IInventory inventory, Setting setting, boolean useWhiteList) {
        this.setting = setting;
        this.useWhiteList = useWhiteList;
        if (setting != null && ((ItemSetting) setting).getItem() != null && setting.isLimitedByAmount()) {
            Iterator itr;
            ItemBufferElement itemBufferElement1;
            if (useWhiteList) {
                if ((inventories.get(0)).isShared()) {
                    itr = inventories.iterator();

                    while (itr.hasNext()) {
                        SlotInventoryHolder itemBufferElement = (SlotInventoryHolder) itr.next();
                        if (itemBufferElement.getTile() instanceof IHiddenInventory) {
                            addInventory((IHiddenInventory) itemBufferElement.getTile());
                        } else {
                            this.addInventory(itemBufferElement.getInventory());
                        }
                    }
                } else {
                    this.addInventory(inventory);
                }
            } else {
                for (itr = itemBuffer.iterator(); itr
                        .hasNext(); this.currentBufferStackSize += itemBufferElement1.getBufferSize(setting)) {
                    itemBufferElement1 = (ItemBufferElement) itr.next();
                }
            }
        }
    }

    private void addInventory(IHiddenInventory inventory) {
        this.currentInventoryStackSize += inventory.getExistingStackSize((ItemSetting) this.setting);
    }

    private void addInventory(IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack item = inventory.getStackInSlot(i);
            if (((ItemSetting) this.setting).isEqualForCommandExecutor(item)) {
                this.currentInventoryStackSize += item.stackSize;
            }
        }
    }

    public boolean areSettingsSame(Setting setting) {
        return this.setting == null && setting == null
                || this.setting != null && setting != null && this.setting.getId() == setting.getId();
    }

    public int retrieveItemCount(int desiredItemCount) {
        if (this.setting != null && this.setting.isLimitedByAmount()) {
            int itemsAllowedToBeMoved;
            if (this.useWhiteList) {
                itemsAllowedToBeMoved = ((ItemSetting) this.setting).getItem().stackSize
                        - this.currentInventoryStackSize;
            } else {
                itemsAllowedToBeMoved = this.currentBufferStackSize - ((ItemSetting) this.setting).getItem().stackSize;
            }

            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        } else {
            return desiredItemCount;
        }
    }

    public void modifyStackSize(int itemsToMove) {
        if (this.useWhiteList) {
            this.currentInventoryStackSize += itemsToMove;
        } else {
            this.currentBufferStackSize -= itemsToMove;
        }
    }
}
