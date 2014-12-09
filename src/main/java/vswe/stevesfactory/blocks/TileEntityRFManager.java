package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import stevesaddons.interfaces.GuiRFManager;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.EnumSet;
import java.util.Iterator;

public class TileEntityRFManager extends TileEntityManager
{
    int superTimer;

    public TileEntityRFManager()
    {
        super();
    }

    @Override
    public void updateEntity()
    {
        this.justSentServerComponentRemovalPacket = false;
        if(!this.worldObj.isRemote) {
            if(this.superTimer >= 20) {
                this.superTimer = 0;
                Iterator i$ = this.getFlowItems().iterator();

                while(i$.hasNext()) {
                    FlowComponent item = (FlowComponent)i$.next();
                    if(item.getType() == ComponentType.TRIGGER) {
                        ComponentMenuInterval componentMenuInterval = (ComponentMenuInterval)item.getMenus().get(2);
                        int interval = componentMenuInterval.getInterval();
                        if(interval != 0) {
                            item.setCurrentInterval(item.getCurrentInterval() + 1);
                            if(item.getCurrentInterval() >= interval) {
                                item.setCurrentInterval(0);
                                EnumSet valid = EnumSet.of(ConnectionOption.INTERVAL);
                                if(item.getConnectionSet() == ConnectionSet.REDSTONE) {
                                    redstoneTrigger.onTrigger(item, valid);
                                } else if(item.getConnectionSet() == ConnectionSet.BUD) {
                                    budTrigger.onTrigger(item, valid);
                                }

                                activateTrigger(item, valid);
                            }
                        }
                    }
                }
            } else {
                ++this.superTimer;
            }
        }
    }

    @Override
    public void activateTrigger(FlowComponent component, EnumSet<ConnectionOption> validTriggerOutputs) {
        this.updateFirst();
        Iterator i$ = this.inventories.iterator();

        while(i$.hasNext()) {
            ConnectionBlock inventory = (ConnectionBlock)i$.next();
            if(inventory.getTileEntity().isInvalid()) {
                this.updateInventories();
                break;
            }
        }

        (new CommandExecutorRF(this)).executeTriggerCommand(component, validTriggerOutputs);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(TileEntity te, InventoryPlayer inv) {
        return new GuiRFManager((TileEntityManager)te, inv);
    }
}
