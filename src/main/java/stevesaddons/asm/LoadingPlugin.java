package stevesaddons.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;
import net.minecraftforge.classloading.FMLForgePlugin;

@IFMLLoadingPlugin.TransformerExclusions({"stevesaddons.asm."})
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
public class LoadingPlugin implements IFMLLoadingPlugin {
    public static boolean runtimeDeobfEnabled = FMLForgePlugin.RUNTIME_DEOBF;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {getAccessTransformerClass()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return "stevesaddons.asm.StevesAddonsTransformer";
    }
}
