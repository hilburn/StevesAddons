package stevesaddons.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class LabelSyncMessage  implements IMessage, IMessageHandler<LabelSyncMessage, IMessage>
{
    ItemStack stack;
    EntityPlayer player;
    public LabelSyncMessage()
    {
    }

    public LabelSyncMessage(ItemStack stack, EntityPlayer player)
    {
        this.stack = stack;
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.stack = ByteBufUtils.readItemStack(buf);

    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public IMessage onMessage(LabelSyncMessage message, MessageContext ctx)
    {
        if (message.player!=null)
        {
            message.player.inventory.setInventorySlotContents(message.player.inventory.currentItem, message.stack);
        }
        return null;
    }
}
