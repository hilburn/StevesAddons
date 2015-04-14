package stevesaddons.components;

import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.blocks.ConnectionBlock;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.components.ComponentMenuContainer;
import vswe.stevesfactory.components.FlowComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ComponentMenuRF extends ComponentMenuContainer
{
    public ComponentMenuRF(FlowComponent parent, ConnectionBlockType validType)
    {
        super(parent, validType);
    }

    public boolean isSelected(TileEntityRFNode node)
    {
        for (ConnectionBlock block : getParent().getManager().getConnectedInventories())
        {
            if (block.getTileEntity() == node) return true;
        }
        return false;
    }

    public void updateConnectedNodes()
    {
        List<ConnectionBlock> connections = getParent().getManager().getConnectedInventories();
        Map<Integer, ConnectionBlock> connectionBlocks = new HashMap<Integer, ConnectionBlock>();
        for (ConnectionBlock connection:connections) connectionBlocks.put(connection.getId(), connection);
        for (int i : getSelectedInventories())
        {
            ConnectionBlock block = connectionBlocks.get(i);
            if (block.getTileEntity() instanceof TileEntityRFNode) ((TileEntityRFNode)block.getTileEntity()).update(getParent());
        }
    }
}
