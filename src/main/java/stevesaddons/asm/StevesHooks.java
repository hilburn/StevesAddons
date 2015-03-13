package stevesaddons.asm;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.naming.BlockCoord;
import stevesaddons.naming.NameRegistry;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.ComponentType;
import vswe.stevesfactory.components.Connection;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.settings.Settings;

import java.util.*;

public class StevesHooks
{
    public static void addCopyButton(final TileEntityManager manager)
    {
        int index = getAfterDelete(manager.buttons);
        manager.buttons.add(index, manager.new Button(StevesEnum.COPY_COMMAND)
        {
            @Override
            protected void onClick(DataReader dataReader)
            {
                if (Settings.isLimitless(manager) || manager.getFlowItems().size() < 511)
                {
                    int id = dataReader.readComponentId();
                    Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                    FlowComponent item;
                    do
                    {
                        if (!itr.hasNext()) return;
                        item = itr.next();
                    } while (item.getId() != id);
                    Collection<FlowComponent> added = copyConnectionsWithChildren(manager.getFlowItems(), item, Settings.isLimitless(manager));
                    manager.getFlowItems().addAll(added);
                }

            }

            @Override
            public boolean activateOnRelease()
            {
                return true;
            }

            @Override
            public boolean onClick(DataWriter dataWriter)
            {
                Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                FlowComponent item;
                do
                {
                    if (!itr.hasNext()) return false;
                    item = itr.next();
                } while (!item.isBeingMoved());
                dataWriter.writeComponentId(manager, item.getId());
                return true;
            }

            @Override
            public String getMouseOver()
            {
                return !Settings.isLimitless(manager) && manager.getFlowItems().size() == 511 ? Localization.MAXIMUM_COMPONENT_ERROR.toString() : super.getMouseOver();
            }
        });
    }

    private static int getAfterDelete(List<TileEntityManager.Button> buttons)
    {
        return ComponentType.values().length + 1;
    }

    private static Collection<FlowComponent> copyConnectionsWithChildren(List<FlowComponent> existing, FlowComponent toCopy, boolean limitless)
    {
        Map<FlowComponent, FlowComponent> added = new LinkedHashMap<FlowComponent, FlowComponent>();
        copyConnectionsWithChildren(added, existing, toCopy, toCopy.getParent(), true);
        if (added.size() + existing.size() >= 511 && !limitless)
        {
            Iterator<Map.Entry<FlowComponent, FlowComponent>> itr = added.entrySet().iterator();
            for (int index = 0; itr.hasNext(); index++)
            {
                itr.next();
                if (index >= 511 - existing.size()) itr.remove();
            }
        }
        reconnect(added);
        return added.values();
    }

    private static void copyConnectionsWithChildren(Map<FlowComponent, FlowComponent> added, List<FlowComponent> existing, FlowComponent toCopy, FlowComponent newParent, boolean reset)
    {
        FlowComponent newComponent = toCopy.copy();
        newComponent.clearConnections();
        newComponent.setParent(newParent);
        if (reset)
        {
            newComponent.resetPosition();
            newComponent.setX(50);
            newComponent.setY(50);
        }
        newComponent.setId(existing.size() + added.size());
        added.put(toCopy, newComponent);
        for (FlowComponent component : existing)
        {
            if (component.getParent() == toCopy)
            {
                copyConnectionsWithChildren(added, existing, component, newComponent, false);
            }
        }
    }

    private static void reconnect(Map<FlowComponent, FlowComponent> added)
    {
        Map<Integer, FlowComponent> oldComponents = new HashMap<Integer, FlowComponent>();
        for (FlowComponent component : added.keySet())
        {
            oldComponents.put(component.getId(), component);
        }

        for (FlowComponent component : added.keySet())
        {
            for (Map.Entry<Integer, Connection> entry : component.getConnections().entrySet())
            {
                FlowComponent connectTo = added.get(oldComponents.get(entry.getValue().getComponentId()));
                if (connectTo != null)
                {
                    Connection newConnection = new Connection(connectTo.getId(), entry.getValue().getConnectionId());
                    added.get(component).setConnection(entry.getKey(), newConnection);
                }
            }
        }
    }

    public static ItemStack fixLoadingStack(ItemStack stack)
    {
        if (stack != null && stack.getItem() == null) return null;
        return stack;
    }

    public static String fixToolTip(String string, TileEntity tileEntity)
    {
        if (tileEntity != null && tileEntity.hasWorldObj())
        {
            BlockCoord coord = new BlockCoord(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            String label = NameRegistry.getSavedName(tileEntity.getWorldObj().provider.dimensionId, coord);
            if (label != null) string = "§3" + label;
            if (tileEntity instanceof IDeepStorageUnit)
            {
                ItemStack stack = ((IDeepStorageUnit)tileEntity).getStoredItemType();
                String contains = "\n";
                if (stack == null) contains += StatCollector.translateToLocal("stevesaddons.idsucompat.isEmpty");
                else
                    contains += StatCollector.translateToLocalFormatted("stevesaddons.idsucompat.contains", stack.getDisplayName());
                string += contains;
            }
        }
        return string;
    }
}
