package stevesaddons.registry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import stevesaddons.helpers.LocalizationHelper;

import java.util.List;

public class CommandRegistry extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "stevesaddons";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + getCommandName();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        sender.addChatMessage(new ChatComponentText(LocalizationHelper.translate("stevesaddons.command.notAvailableOnClient")));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

}
