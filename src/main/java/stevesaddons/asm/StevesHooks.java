package stevesaddons.asm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import stevesaddons.api.IHiddenInventory;
import stevesaddons.api.IHiddenTank;
import stevesaddons.components.ComponentMenuTriggered;
import stevesaddons.helpers.StevesEnum;
import stevesaddons.naming.BlockCoord;
import stevesaddons.naming.NameRegistry;
import stevesaddons.reference.Null;
import stevesaddons.threading.SearchItems;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.ConnectionBlock;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.ComponentHelper;
import vswe.stevesfactory.components.ComponentMenuItem;
import vswe.stevesfactory.components.ComponentType;
import vswe.stevesfactory.components.Connection;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.components.ScrollController;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.settings.Settings;

public class StevesHooks {

    public static final Multimap<TileEntityManager, FlowComponent> delayedRegistry = HashMultimap.create();

    public static void addCopyButton(final TileEntityManager manager) {
        int index = getAfterDelete(manager.buttons);
        manager.buttons.add(index, manager.new Button(StevesEnum.COPY_COMMAND) {

            @Override
            protected void onClick(DataReader dataReader) {
                if (Settings.isLimitless(manager) || manager.getFlowItems().size() < 511) {
                    int id = dataReader.readComponentId();
                    Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                    FlowComponent item;
                    do {
                        if (!itr.hasNext()) return;
                        item = itr.next();
                    } while (item.getId() != id);
                    Collection<FlowComponent> added = copyConnectionsWithChildren(
                            manager.getFlowItems(),
                            item,
                            Settings.isLimitless(manager));
                    manager.getFlowItems().addAll(added);
                }
            }

            @Override
            public boolean activateOnRelease() {
                return true;
            }

            @Override
            public boolean onClick(DataWriter dataWriter) {
                Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                FlowComponent item;
                do {
                    if (!itr.hasNext()) return false;
                    item = itr.next();
                } while (!item.isBeingMoved());
                dataWriter.writeComponentId(manager, item.getId());
                return true;
            }

            @Override
            public String getMouseOver() {
                return !Settings.isLimitless(manager) && manager.getFlowItems().size() == 511
                        ? Localization.MAXIMUM_COMPONENT_ERROR.toString()
                        : super.getMouseOver();
            }
        });
    }

    private static int getAfterDelete(List<TileEntityManager.Button> buttons) {
        return ComponentType.values().length + 1;
    }

    private static Collection<FlowComponent> copyConnectionsWithChildren(List<FlowComponent> existing,
            FlowComponent toCopy, boolean limitless) {
        Map<FlowComponent, FlowComponent> added = new LinkedHashMap<FlowComponent, FlowComponent>();
        copyConnectionsWithChildren(added, existing, toCopy, toCopy.getParent(), true);
        if (added.size() + existing.size() >= 511 && !limitless) {
            Iterator<Map.Entry<FlowComponent, FlowComponent>> itr = added.entrySet().iterator();
            for (int index = 0; itr.hasNext(); index++) {
                itr.next();
                if (index >= 511 - existing.size()) itr.remove();
            }
        }
        reconnect(added);
        return added.values();
    }

    private static void copyConnectionsWithChildren(Map<FlowComponent, FlowComponent> added,
            List<FlowComponent> existing, FlowComponent toCopy, FlowComponent newParent, boolean reset) {
        FlowComponent newComponent = toCopy.copy();
        newComponent.clearConnections();
        newComponent.setParent(newParent);
        if (reset) {
            newComponent.resetPosition();
            newComponent.setX(50);
            newComponent.setY(50);
        }
        newComponent.setId(existing.size() + added.size());
        added.put(toCopy, newComponent);
        for (FlowComponent component : existing) {
            if (component.getParent() == toCopy) {
                copyConnectionsWithChildren(added, existing, component, newComponent, false);
            }
        }
    }

    private static void reconnect(Map<FlowComponent, FlowComponent> added) {
        Map<Integer, FlowComponent> oldComponents = new HashMap<Integer, FlowComponent>();
        for (FlowComponent component : added.keySet()) {
            oldComponents.put(component.getId(), component);
        }

        for (FlowComponent component : added.keySet()) {
            for (Map.Entry<Integer, Connection> entry : component.getConnections().entrySet()) {
                try {
                    FlowComponent connectTo = added.get(oldComponents.get(entry.getValue().getComponentId()));
                    if (connectTo != null) {
                        Connection newConnection = new Connection(
                                connectTo.getId(),
                                entry.getValue().getConnectionId());
                        added.get(component).setConnection(entry.getKey(), newConnection);
                    }
                } catch (NullPointerException ignored) {
                    break;
                }
            }
        }
    }

    public static ItemStack fixLoadingStack(ItemStack stack) {
        if (stack != null && stack.getItem() == null) return null;
        return stack;
    }

    public static String fixToolTip(String string, TileEntity tileEntity) {
        if (tileEntity != null && tileEntity.hasWorldObj()) {
            String label = getLabel(tileEntity);
            if (label != null) string = "§3" + label;
            string += getContentString(tileEntity);
        }
        return string;
    }

    public static void removeFlowComponent(TileEntityManager manager, int idToRemove, List<FlowComponent> components) {
        boolean isManagerList = manager.getFlowItems() == components;
        List<Integer> ids = getIdsToRemove(idToRemove, components);
        List<FlowComponent> removed = new ArrayList<FlowComponent>();
        for (int id : ids) {
            for (int i = components.size() - 1; i >= 0; --i) {
                FlowComponent component = components.get(i);
                if (i == id) {
                    component.setParent(null);
                    removed.add(component);
                    components.remove(i);
                } else {
                    component.updateConnectionIdsAtRemoval(id);
                }
            }

            if (manager.getSelectedComponent() != null && manager.getSelectedComponent().getId() == id) {
                manager.setSelectedComponent(null);
            }

            for (int i = id; i < components.size(); ++i) {
                components.get(i).decreaseId();
            }
        }

        if (isManagerList && manager.getWorldObj().isRemote) {
            for (FlowComponent remove : removed) manager.getZLevelRenderingList().remove(remove);
            // for (Iterator<FlowComponent> itr = manager.getZLevelRenderingList().iterator();
            // itr.hasNext();)
            // {
            // if (removed.contains(itr.next()))
            // {
            // itr.remove();
            // }
            // }
        }
    }

    public static List<Integer> getIdsToRemove(int idToRemove, List<FlowComponent> items) {
        List<Integer> ids = new ArrayList<Integer>();
        getIdsToRemove(ids, idToRemove, items);
        Collections.sort(ids);
        Collections.reverse(ids);
        return ids;
    }

    private static void getIdsToRemove(List<Integer> ids, int idToRemove, List<FlowComponent> items) {
        for (FlowComponent component : items) {
            if (component.getParent() != null && component.getParent().getId() == idToRemove)
                getIdsToRemove(ids, component.getId(), items);
        }
        ids.add(idToRemove);
    }

    public static boolean instanceOf(Class clazz, TileEntity entity) {
        return clazz.isInstance(entity) || entity instanceof IHiddenTank && clazz == IFluidHandler.class
                || entity instanceof IHiddenInventory && clazz == IInventory.class
                || clazz == IEnergyConnection.class
                        && (entity instanceof IEnergyProvider || entity instanceof IEnergyReceiver);
    }

    public static String getContentString(TileEntity tileEntity) {
        String result = "";
        if (tileEntity instanceof IDeepStorageUnit) {
            ItemStack stack = ((IDeepStorageUnit) tileEntity).getStoredItemType();
            String contains = "\n";
            if (stack == null || stack.isItemEqual(Null.NULL_STACK))
                contains += StatCollector.translateToLocal("stevesaddons.idsucompat.isEmpty");
            else contains += StatCollector
                    .translateToLocalFormatted("stevesaddons.idsucompat.contains", stack.getDisplayName());
            result += contains;
        } else if (tileEntity instanceof IFluidHandler) {
            String tankInfo = "";
            int i = 1;
            FluidTankInfo[] fluidTankInfo = ((IFluidHandler) tileEntity).getTankInfo(ForgeDirection.UNKNOWN);
            if (fluidTankInfo != null) {
                for (FluidTankInfo info : fluidTankInfo) {
                    if (info.fluid == null || info.fluid.getFluid() == null) return result;
                    tankInfo += info.fluid.getLocalizedName() + (i++ < fluidTankInfo.length ? ", " : "");
                }
            }
            if (tankInfo.isEmpty()) result += "\n" + StatCollector.translateToLocal("stevesaddons.idsucompat.isEmpty");
            else result += "\n" + StatCollector.translateToLocalFormatted("stevesaddons.idsucompat.contains", tankInfo);
        }
        return result;
    }

    private static String getLabel(TileEntity tileEntity) {
        BlockCoord coord = new BlockCoord(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        return NameRegistry.getSavedName(tileEntity.getWorldObj().provider.dimensionId, coord);
    }

    public static List updateItemSearch(ComponentMenuItem menu, String search, boolean showAll) {
        ScrollController searchController = ComponentHelper.getController(menu);
        Thread thread = new Thread(new SearchItems(search, searchController, showAll));
        thread.start();
        return searchController.getResult();
    }

    public static boolean containerAdvancedSearch(ConnectionBlock block, String search) {
        TileEntity tileEntity = block.getTileEntity();
        String toSearch = getLabel(tileEntity);
        Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
        return (toSearch != null && pattern.matcher(toSearch).find())
                || pattern.matcher(getContentString(tileEntity)).find();
    }

    public static void registerTicker(FlowComponent component, ComponentMenuTriggered menu) {
        if (!getRegistry(menu).containsEntry(component.getManager(), component)) {
            getRegistry(menu).put(component.getManager(), component);
        }
    }

    public static TileEntityManager tickTriggers(TileEntityManager manager) {
        tick(delayedRegistry.get(manager));
        return manager;
    }

    private static void tick(Collection<FlowComponent> triggers) {
        if (triggers != null) {
            for (Iterator<FlowComponent> itr = triggers.iterator(); itr.hasNext();) {
                ComponentMenuTriggered toTrigger = (ComponentMenuTriggered) itr.next().getMenus().get(6);
                if (toTrigger.isVisible()) {
                    toTrigger.tick();
                    if (toTrigger.remove()) itr.remove();
                } else {
                    itr.remove();
                }
            }
        }
    }

    private static Multimap<TileEntityManager, FlowComponent> getRegistry(ComponentMenuTriggered menu) {
        return delayedRegistry;
    }
}
