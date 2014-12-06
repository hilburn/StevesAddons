package stevesaddons.proxy;

import net.minecraft.world.World;


public class ClientProxy extends CommonProxy
{

    @Override
    public World getClientWorld()
    {
        return null;
    }

    @Override
    public void initRenderers()
    {
    }

}
