package stevesaddons.helpers;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import stevesaddons.blocks.BlockCableRFCluster;
import stevesaddons.components.ComponentMenuRF;
import stevesaddons.components.ComponentMenuTargetRF;
import stevesaddons.tileentities.TileEntityRFNode;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.ClusterMethodRegistration;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.blocks.ItemCluster;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.interfaces.GuiManager;

public class StevesEnum
{
    private static final Class[][] localizationClasses = new Class[][]{{ Localization.class}};
    private static final Class[][] clusterMethodClasses = new Class[][]{{ ClusterMethodRegistration.class}};
    private static final Class[][] connectionTypeClasses = new Class[][]{{ConnectionBlockType.class, Localization.class, Class.class, boolean.class}};
    private static final Class[][] componentTypeClasses = new Class[][]{{ComponentType.class, int.class, Localization.class, Localization.class, ConnectionSet[].class, Class[].class}};
    public static final Localization TYPE_RF = EnumHelper.addEnum(localizationClasses, Localization.class,"TYPE_RF");
    public static final Localization RF_SHORT = EnumHelper.addEnum(localizationClasses, Localization.class,"RF_SHORT");
    public static final Localization RF_LONG = EnumHelper.addEnum(localizationClasses, Localization.class,"RF_LONG");
    public static final Localization NO_RF_ERROR = EnumHelper.addEnum(localizationClasses, Localization.class,"NO_RF_ERROR");
    public static final ConnectionBlockType RF = EnumHelper.addEnum(connectionTypeClasses, ConnectionBlockType.class, "RF", TYPE_RF, TileEntityRFNode.class, false);
    public static final ComponentType RF_COMPONENT = EnumHelper.addEnum(componentTypeClasses, ComponentType.class,"RF_INPUT",17,RF_SHORT,RF_LONG, new ConnectionSet[]{ConnectionSet.INPUT_NODE, ConnectionSet.OUTPUT_NODE},new Class[]{ComponentMenuRF.class, ComponentMenuTargetRF.class, ComponentMenuResult.class});
    public static final ClusterMethodRegistration CONNECT_ENERGY = EnumHelper.addEnum(clusterMethodClasses,ClusterMethodRegistration.class,"CONNECT_ENERGY");
    public static final ClusterMethodRegistration EXTRACT_ENERGY = EnumHelper.addEnum(clusterMethodClasses,ClusterMethodRegistration.class,"EXTRACT_ENERGY");
    public static final ClusterMethodRegistration RECEIVE_ENERGY = EnumHelper.addEnum(clusterMethodClasses,ClusterMethodRegistration.class,"RECEIVE_ENERGY");

    public StevesEnum()
    {
        ReflectionHelper.setPrivateStaticFinalField(GuiManager.class,"COMPONENTS",new ResourceLocation("stevesaddons", "textures/gui/FlowComponents.png"));
    }

    public static void replaceCluster()
    {
        BlockReplaceHelper.replaceBlock(ModBlocks.class, ModBlocks.blockCableCluster, BlockCableRFCluster.class, ItemCluster.class);
    }
}
