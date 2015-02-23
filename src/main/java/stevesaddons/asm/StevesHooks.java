package stevesaddons.asm;

import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.ComponentType;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.settings.Settings;

import java.util.Iterator;
import java.util.List;

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
                if(Settings.isLimitless(manager) || manager.getFlowItems().size() < 511)
                {
                    int id = dataReader.readComponentId();
                    Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                    FlowComponent item;
                    do {
                        if(!itr.hasNext()) return;
                        item = itr.next();
                    } while(item.getId()!=id);
                    FlowComponent newComponent = item.copy();
                    newComponent.clearConnections();
                    newComponent.resetPosition();
                    manager.getFlowItems().add(newComponent);
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
                do {
                    if(!itr.hasNext()) return false;
                    item = itr.next();
                } while(!item.isBeingMoved());
                dataWriter.writeComponentId(manager,item.getId());
                return true;
            }

            @Override
            public String getMouseOver()
            {
                return !Settings.isLimitless(manager) && manager.getFlowItems().size() == 511?Localization.MAXIMUM_COMPONENT_ERROR.toString(): super.getMouseOver();
            }
        });
        return;
    }

    private static int getAfterDelete(List<TileEntityManager.Button> buttons)
    {
        return ComponentType.values().length + 1;
    }
}
