package stevesaddons.registry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import stevesaddons.commands.CommandClear;
import stevesaddons.commands.CommandLoad;
import stevesaddons.commands.CommandSave;
import stevesaddons.commands.ISubCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry extends CommandBase
{
    private static Map<String, ISubCommand> commands = new HashMap<String, ISubCommand>();

    static
    {
        register(CommandSave.instance);
        register(CommandLoad.instance);
        register(CommandClear.instance);
    }

    public static void register(ISubCommand command)
    {
        commands.put(command.getCommandName(), command);
    }

    @Override
    public String getCommandName()
    {
        return "stevesaddons ";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + getCommandName() + " help";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1) {
            args = new String[]{"help"};
        }
        ISubCommand command = commands.get(args[0]);
        if (command != null) {
            if (sender.canCommandSenderUseCommand(command.getPermissionLevel(), "stevesaddons " + command.getCommandName()) ||
                    (sender instanceof EntityPlayerMP && command.getPermissionLevel() <= 0)) {
                command.handleCommand(sender, args);
                return;
            }
            throw new CommandException("commands.generic.permission");
        }
        throw new CommandNotFoundException("stevesaddons.command.notFound");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {

        if (args.length == 1) {
            return getListOfStringsFromIterableMatchingLastWord(args, commands.keySet());
        } else if (commands.containsKey(args[0])) {
            return commands.get(args[0]).addTabCompletionOptions(sender, args);
        }
        return null;
    }

}
