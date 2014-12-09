package stevesaddons.helpers;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.util.EnumHelper;
import stevesaddons.blocks.BlockCableRFCluster;
import stevesaddons.blocks.BlockRFManager;
import stevesaddons.components.ComponentMenuRF;
import stevesaddons.components.ComponentMenuRFCondition;
import stevesaddons.components.ComponentMenuTargetRF;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.ClusterMethodRegistration;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.blocks.ItemCluster;
import vswe.stevesfactory.blocks.ModBlocks;
import vswe.stevesfactory.components.ComponentMenuResult;
import vswe.stevesfactory.components.ComponentType;
import vswe.stevesfactory.components.ConnectionSet;

public class StevesEnum
{
    private static final Class[][] localizationClasses = new Class[][]{{Localization.class}};
    private static final Class[][] clusterMethodClasses = new Class[][]{{ClusterMethodRegistration.class}};
    private static final Class[][] connectionTypeClasses = new Class[][]{{ConnectionBlockType.class, Localization.class, Class.class, boolean.class}};
    private static final Class[][] componentTypeClasses = new Class[][]{{ComponentType.class, int.class, Localization.class, Localization.class, ConnectionSet[].class, Class[].class}};
    public static final Localization TYPE_RF = EnumHelper.addEnum(localizationClasses, Localization.class, "TYPE_RF");
    public static final Localization RF_INPUT_SHORT = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_INPUT_SHORT");
    public static final Localization RF_INPUT_LONG = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_INPUT_LONG");
    public static final Localization RF_OUTPUT_SHORT = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_OUTPUT_SHORT");
    public static final Localization RF_OUTPUT_LONG = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_OUTPUT_LONG");
    public static final Localization RF_CONDITION_SHORT = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_CONDITION_SHORT");
    public static final Localization RF_CONDITION_LONG = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_CONDITION_LONG");
    public static final Localization RF_CONDITION_MENU = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_CONDITION_MENU");
    public static final Localization RF_CONDITION_INFO = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_CONDITION_INFO");
    public static final Localization RF_CONDITION_ERROR = EnumHelper.addEnum(localizationClasses, Localization.class, "RF_CONDITION_ERROR");
    public static final Localization NO_RF_ERROR = EnumHelper.addEnum(localizationClasses, Localization.class, "NO_RF_ERROR");
    public static final Localization BELOW = EnumHelper.addEnum(localizationClasses, Localization.class, "BELOW");
    public static final ConnectionBlockType RF_HANDLER = EnumHelper.addEnum(connectionTypeClasses, ConnectionBlockType.class, "RF_HANDLER", TYPE_RF, IEnergyHandler.class, false);
    public static final ComponentType RF_INPUT = EnumHelper.addEnum(componentTypeClasses, ComponentType.class, "RF_INPUT", 17, RF_INPUT_SHORT, RF_INPUT_LONG, new ConnectionSet[]{ConnectionSet.STANDARD}, new Class[]{ComponentMenuRF.class, ComponentMenuTargetRF.class, ComponentMenuResult.class});
    public static final ComponentType RF_OUTPUT = EnumHelper.addEnum(componentTypeClasses, ComponentType.class, "RF_OUTPUT", 18, RF_OUTPUT_SHORT, RF_OUTPUT_LONG, new ConnectionSet[]{ConnectionSet.STANDARD}, new Class[]{ComponentMenuRF.class, ComponentMenuTargetRF.class, ComponentMenuResult.class});
    public static final ComponentType RF_CONDITION = EnumHelper.addEnum(componentTypeClasses, ComponentType.class, "RF_CONDITION", 19, RF_CONDITION_SHORT, RF_CONDITION_LONG, new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION}, new Class[]{ComponentMenuRF.class, ComponentMenuTargetRF.class, ComponentMenuRFCondition.class, ComponentMenuResult.class});
    public static final ClusterMethodRegistration CONNECT_ENERGY = EnumHelper.addEnum(clusterMethodClasses, ClusterMethodRegistration.class, "CONNECT_ENERGY");
    public static final ClusterMethodRegistration EXTRACT_ENERGY = EnumHelper.addEnum(clusterMethodClasses, ClusterMethodRegistration.class, "EXTRACT_ENERGY");
    public static final ClusterMethodRegistration RECEIVE_ENERGY = EnumHelper.addEnum(clusterMethodClasses, ClusterMethodRegistration.class, "RECEIVE_ENERGY");

    public static void replaceBlocks()
    {
        BlockReplaceHelper.replaceBlock(ModBlocks.class, ModBlocks.blockCableCluster, BlockCableRFCluster.class, ItemCluster.class);
        BlockReplaceHelper.replaceBlock(ModBlocks.class, ModBlocks.blockManager, BlockRFManager.class, ItemBlock.class);
    }
}
