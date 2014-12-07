package stevesaddons.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.blocks.TileEntityRFCluster;
import vswe.stevesfactory.blocks.BlockCableCluster;

public class BlockCableRFCluster extends BlockCableCluster
{
    public BlockCableRFCluster() {
        super();
    }


//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerBlockIcons(IIconRegister register) {
//        this.sideIcon = register.registerIcon("vswe.stevesfactory:cable_cluster");
//        this.frontIcon = register.registerIcon("vswe.stevesfactory:cable_cluster_front");
//        this.sideIconAdv = register.registerIcon("vswe.stevesfactory:cable_cluster_adv");
//        this.frontIconAdv = register.registerIcon("vswe.stevesfactory:cable_cluster_adv_front");
//    }
//
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(int side, int meta) {
//        return this.getIconFromSideAndMeta(side, this.addAdvancedMeta(3, meta));
//    }
//
//    @SideOnly(Side.CLIENT)
//    public IIcon getDefaultIcon(int side, int blockMeta, int camoMeta) {
//        return this.getIconFromSideAndMeta(side, blockMeta);
//    }
//
//    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta) {
//        ItemStack itemStack = this.getItemStack(world, x, y, z, oldMeta);
//        if(itemStack != null) {
//            this.dropBlockAsItem(world, x, y, z, itemStack);
//        }
//
//        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
//        if(this.isAdvanced(world.getBlockMetadata(x, y, z))) {
//            ModBlocks.blockCable.updateInventories(world, x, y, z);
//        }
//
//    }
//
//    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
//        ItemStack itemStack = this.getItemStack(world, x, y, z, world.getBlockMetadata(x, y, z));
//        return itemStack != null?itemStack:super.getPickBlock(target, world, x, y, z);
//    }
//
//    private ItemStack getItemStack(World world, int x, int y, int z, int meta) {
//        TileEntity te = world.getTileEntity(x, y, z);
//        if(te != null && te instanceof TileEntityCluster) {
//            TileEntityCluster cluster = (TileEntityCluster)te;
//            ItemStack itemStack = new ItemStack(ModBlocks.blockCableCluster, 1, this.func_149692_a(meta));
//            NBTTagCompound compound = new NBTTagCompound();
//            itemStack.setTagCompound(compound);
//            NBTTagCompound cable = new NBTTagCompound();
//            compound.setTag("Cable", cable);
//            cable.setByteArray("Types", cluster.getTypes());
//            return itemStack;
//        } else {
//            return null;
//        }
//    }
//
//    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
//        return new ArrayList();
//    }
//
//    @SideOnly(Side.CLIENT)
//    private IIcon getIconFromSideAndMeta(int side, int meta) {
//        return side == this.getSideMeta(meta) % ForgeDirection.VALID_DIRECTIONS.length?(this.isAdvanced(meta)?this.frontIconAdv:this.frontIcon):(this.isAdvanced(meta)?this.sideIconAdv:this.sideIcon);
//    }
//
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRFCluster();
    }
//
//    private TileEntityCluster getTe(IBlockAccess world, int x, int y, int z) {
//        TileEntity te = world.getTileEntity(x, y, z);
//        return te != null && te instanceof TileEntityCluster?(TileEntityCluster)te:null;
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
//        int meta = this.addAdvancedMeta(BlockPistonBase.determineOrientation(world, x, y, z, entity), itemStack.getItemDamage());
//        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        if(cluster != null) {
//            cluster.loadElements(itemStack);
//            cluster.onBlockPlacedBy(entity, itemStack);
//        }
//
//    }
//
//    @Override
//    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        if(cluster != null) {
//            cluster.onNeighborBlockChange(block);
//        }
//
//        if(this.isAdvanced(world.getBlockMetadata(x, y, z))) {
//            ModBlocks.blockCable.updateInventories(world, x, y, z);
//        }
//
//    }
//
//    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        return cluster != null?cluster.canConnectRedstone(side):false;
//    }
//
//    public void onBlockAdded(World world, int x, int y, int z) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        if(cluster != null) {
//            cluster.onBlockAdded();
//        }
//
//        if(this.isAdvanced(world.getBlockMetadata(x, y, z))) {
//            ModBlocks.blockCable.updateInventories(world, x, y, z);
//        }
//
//    }
//
//    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        return cluster != null?cluster.shouldCheckWeakPower(side):false;
//    }
//
//    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        return cluster != null?cluster.isProvidingWeakPower(side):0;
//    }
//
//    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        return cluster != null?cluster.isProvidingStrongPower(side):0;
//    }
//
//    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
//        TileEntityCluster cluster = this.getTe(world, x, y, z);
//        return cluster != null?cluster.onBlockActivated(player, side, hitX, hitY, hitZ):false;
//    }
//
//    public void func_149666_a(Item item, CreativeTabs tabs, List list) {
//        list.add(new ItemStack(item, 1, 0));
//        list.add(new ItemStack(item, 1, 8));
//    }
//
//    public boolean isAdvanced(int meta) {
//        return (meta & 8) != 0;
//    }
//
//    public int getSideMeta(int meta) {
//        return meta & 7;
//    }
//
//    private int addAdvancedMeta(int meta, int advancedMeta) {
//        return meta | advancedMeta & 8;
//    }
//
//    private int getAdvancedMeta(int meta) {
//        return this.addAdvancedMeta(0, meta);
//    }
//
//    public int func_149692_a(int meta) {
//        return this.getAdvancedMeta(meta);
//    }
}
