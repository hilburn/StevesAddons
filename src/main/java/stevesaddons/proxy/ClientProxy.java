package stevesaddons.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import stevesaddons.threading.ThreadSafeHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void initRenderers() {}

    @Override
    public void initHandlers() {
        MinecraftForge.EVENT_BUS.register(new ThreadSafeHandler());
    }
}
