package stevesaddons.helpers;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.me.cache.P2PCache;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Iterator;

public class MEHelper
{
    public static IGrid getGrid(IGridNode node) throws GridAccessException
    {
        if( node == null )
            throw new GridAccessException();
        IGrid grid = node.getGrid();
        if( grid == null )
            throw new GridAccessException();
        return grid;
    }

    public ITickManager getTick(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
        if( grid == null )
            throw new GridAccessException();
        ITickManager pg = grid.getCache(ITickManager.class);
        if( pg == null )
            throw new GridAccessException();
        return pg;
    }

    public IStorageGrid getStorage(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
        if( grid == null )
            throw new GridAccessException();

        IStorageGrid pg = grid.getCache(IStorageGrid.class);

        if( pg == null )
            throw new GridAccessException();

        return pg;
    }

    public P2PCache getP2P(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
        if( grid == null )
            throw new GridAccessException();

        P2PCache pg = grid.getCache( P2PCache.class );

        if( pg == null )
            throw new GridAccessException();

        return pg;
    }

    public ISecurityGrid getSecurity(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
        if( grid == null )
            throw new GridAccessException();

        ISecurityGrid sg = grid.getCache( ISecurityGrid.class );

        if( sg == null )
            throw new GridAccessException();

        return sg;
    }

    public ICraftingGrid getCrafting(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
        if( grid == null )
            throw new GridAccessException();

        ICraftingGrid sg = grid.getCache( ICraftingGrid.class );

        if( sg == null )
            throw new GridAccessException();

        return sg;
    }

    public boolean canInsert(IGridNode node, ItemStack stack)
    {
        try
        {
            return getStorage(node).getItemInventory().canAccept(AEItemStack.create(stack));
        } catch (GridAccessException e)
        {
            return false;
        }
    }

    public boolean insert(IGridNode node, ItemStack stack, IActionHost host)
    {
        if (canInsert(node, stack))
        {
            try
            {
                getStorage(node).getItemInventory().injectItems(AEItemStack.create(stack), Actionable.MODULATE, new MachineSource(host));
            } catch (GridAccessException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public ItemStack extract(IGridNode node, ItemStack stack, IActionHost host)
    {
        try
        {
            return getStorage(node).getItemInventory().extractItems(AEItemStack.create(stack), Actionable.MODULATE, new MachineSource(host)).getItemStack();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    public boolean canInsert(IGridNode node, FluidStack stack)
    {
        try
        {
            return getStorage(node).getFluidInventory().canAccept(AEFluidStack.create(stack));
        } catch (GridAccessException e)
        {
            return false;
        }
    }

    public boolean insert(IGridNode node, FluidStack stack, IActionHost host)
    {
        if (canInsert(node, stack))
        {
            try
            {
                getStorage(node).getFluidInventory().injectItems(AEFluidStack.create(stack), Actionable.MODULATE, new MachineSource(host));
            } catch (GridAccessException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public FluidStack extract(IGridNode node, FluidStack stack, IActionHost host)
    {
        try
        {
            return getStorage(node).getFluidInventory().extractItems(AEFluidStack.create(stack), Actionable.MODULATE, new MachineSource(host)).getFluidStack();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    public ItemStack find(IGridNode node, ItemStack stack)
    {
        try
        {
            return getStorage(node).getItemInventory().getStorageList().findPrecise(AEItemStack.create(stack)).getItemStack();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    public FluidStack find(IGridNode node, FluidStack stack)
    {
        try
        {
            return getStorage(node).getFluidInventory().getStorageList().findPrecise(AEFluidStack.create(stack)).getFluidStack();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    public Iterator<IAEItemStack> getItrItems(IGridNode node)
    {
        try
        {
            return getStorage(node).getItemInventory().getStorageList().iterator();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    public Iterator<IAEFluidStack> getItrFluids(IGridNode node)
    {
        try
        {
            return getStorage(node).getFluidInventory().getStorageList().iterator();
        } catch (GridAccessException e)
        {
            return null;
        }
    }
}
