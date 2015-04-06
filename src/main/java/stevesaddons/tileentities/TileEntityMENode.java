package stevesaddons.tileentities;

import appeng.api.AEApi;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import stevesaddons.registry.BlockRegistry;
import vswe.stevesfactory.blocks.ClusterMethodRegistration;
import vswe.stevesfactory.blocks.TileEntityClusterElement;

import java.util.EnumSet;

public class TileEntityMENode extends TileEntityClusterElement implements IGridHost, IActionHost
{
    private class GridBlock implements IGridBlock
    {
        private TileEntityMENode te;

        private GridBlock(TileEntityMENode te)
        {
            this.te = te;
        }

        @Override
        public double getIdlePowerUsage()
        {
            return 1;
        }

        @Override
        public EnumSet<GridFlags> getFlags()
        {
            return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
        }

        @Override
        public boolean isWorldAccessible()
        {
            return true;
        }

        @Override
        public DimensionalCoord getLocation()
        {
            return new DimensionalCoord(this.te);
        }

        @Override
        public AEColor getGridColor()
        {
            return AEColor.Transparent;
        }

        @Override
        public void onGridNotification(GridNotification gridNotification)
        {

        }

        @Override
        public void setNetworkStatus(IGrid iGrid, int i)
        {

        }

        @Override
        public EnumSet<ForgeDirection> getConnectableSides()
        {
            return EnumSet.allOf(ForgeDirection.class);
        }

        @Override
        public IGridHost getMachine()
        {
            return this.te;
        }

        @Override
        public void gridChanged()
        {

        }

        @Override
        public ItemStack getMachineRepresentation()
        {
            return new ItemStack(BlockRegistry.cableMENode);
        }
    }

    private GridBlock gridBlock;
    private IGridNode gridNode;
    private boolean isReady;

    public TileEntityMENode()
    {
        this.gridBlock = new GridBlock(this);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.isReady = true;
        getNode();
    }

    @Override
    public boolean canUpdate()
    {
        return !this.isReady;
    }

    private IGridNode getNode()
    {
        if( this.gridNode == null && FMLCommonHandler.instance().getEffectiveSide().isServer() && this.isReady)
        {
            this.gridNode = AEApi.instance().createGridNode(this.gridBlock);
            this.gridNode.updateState();
        }

        return this.gridNode;
    }

    @Override
    public IGridNode getActionableNode()
    {
        return getNode();
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return null;
    }

    @Override
    public IGridNode getGridNode(ForgeDirection forgeDirection)
    {
        return getNode();
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection forgeDirection)
    {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak()
    {
        this.worldObj.func_147480_a(this.xCoord, this.yCoord, this.zCoord, true);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (this.gridNode != null)
        {
            this.gridNode.destroy();
            this.gridNode = null;
        }
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        if (this.gridNode != null)
        {
            this.gridNode.destroy();
            this.gridNode = null;
        }
    }
}
