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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import stevesaddons.helpers.AEHelper;
import stevesaddons.registry.BlockRegistry;
import vswe.stevesfactory.blocks.ClusterMethodRegistration;
import vswe.stevesfactory.blocks.TileEntityClusterElement;

import java.util.EnumSet;

public class TileEntityAENode extends TileEntityClusterElement implements IGridHost, IActionHost
{
    private class GridBlock implements IGridBlock
    {
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
            return new DimensionalCoord(TileEntityAENode.this);
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
            return TileEntityAENode.this;
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

    private class AEFakeTank implements IFluidHandler
    {
        @Override
        public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
        {
            FluidStack toAdd = resource.copy();
            int stepSize = Math.max(toAdd.amount/10,1);
            while (!AEHelper.canInsert(TileEntityAENode.this.getNode(), toAdd) && toAdd.amount > 0)
            {
                toAdd.amount -= stepSize;
            }
            if (doFill) AEHelper.insert(TileEntityAENode.this.getNode(), toAdd, TileEntityAENode.this);
            return toAdd.amount;
        }

        @Override
        public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
        {
            if (doDrain) return AEHelper.extract(TileEntityAENode.this.getNode(), resource, TileEntityAENode.this);
            FluidStack result = AEHelper.find(TileEntityAENode.this.getNode(), resource);
            if (result!=null) result.amount = resource.amount;
            return result;
        }

        @Override
        public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
        {
            FluidStack stack = AEHelper.getItrFluids(TileEntityAENode.this.getNode()).next().getFluidStack();
            stack.amount = Math.min(maxDrain, stack.amount);
            return drain(from, stack, doDrain);
        }

        @Override
        public boolean canFill(ForgeDirection from, Fluid fluid)
        {
            return AEHelper.canInsert(TileEntityAENode.this.getNode(), new FluidStack(fluid, 1));
        }

        @Override
        public boolean canDrain(ForgeDirection from, Fluid fluid)
        {
            return AEHelper.find(TileEntityAENode.this.getNode(), new FluidStack(fluid, 1))!=null;
        }

        @Override
        public FluidTankInfo[] getTankInfo(ForgeDirection from)
        {
            return new FluidTankInfo[0];
        }
    }

    private GridBlock gridBlock;
    private IGridNode gridNode;
    private IFluidHandler tank;
    private boolean isReady;

    public TileEntityAENode()
    {
        this.gridBlock = new GridBlock();
        this.tank = new AEFakeTank();
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

    public IGridNode getNode()
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

    public IFluidHandler getTank()
    {
        return tank;
    }
}
