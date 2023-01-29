package stevesaddons.components;

import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.blocks.ConnectionBlock;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.components.ComponentMenuContainer;
import vswe.stevesfactory.components.FlowComponent;

public abstract class ComponentMenuRF extends ComponentMenuContainer {

    public ComponentMenuRF(FlowComponent parent, ConnectionBlockType validType) {
        super(parent, validType);
    }

    public boolean isSelected(TileEntityRFNode node) {
        for (ConnectionBlock block : getParent().getManager().getConnectedInventories()) {
            if (block.getTileEntity() == node) return getSelectedInventories().contains(block.getId());
        }
        return false;
    }

    public void updateConnectedNodes() {
        if (!getParent().getManager().getWorldObj().isRemote) {
            for (ConnectionBlock connection : getParent().getManager().getConnectedInventories()) {
                if (connection.getTileEntity() instanceof TileEntityRFNode)
                    ((TileEntityRFNode) connection.getTileEntity()).update(getParent());
            }
        }
    }
}
