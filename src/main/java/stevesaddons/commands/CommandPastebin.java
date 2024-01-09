package stevesaddons.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.FMLCommonHandler;
import stevesaddons.helpers.Threaded;
import stevesaddons.items.ItemSFMDrive;

public class CommandPastebin extends CommandDuplicator {

    public static final Set<String> usernameWhitelist = new HashSet<String>();

    public static CommandPastebin instance = new CommandPastebin();

    @Override
    public void doCommand(ItemStack duplicator, EntityPlayerMP sender, String[] arguments) {
        if (arguments.length < 2) {
            throw new WrongUsageException("stevesaddons.command." + getCommandName() + ".syntax");
        }
        try {

            if (arguments[1].equals("put")) {
                if (ItemSFMDrive.validateNBT(duplicator) && duplicator.hasTagCompound()) {
                    new Thread(new Threaded.Put(duplicator, sender, arguments)).start();
                } else {
                    throw new CommandException("stevesaddons.command.nothingToSave");
                }
            } else if (arguments[1].equals("get")) {
                if (arguments.length < 3) {
                    throw new WrongUsageException("stevesaddons.command." + getCommandName() + ".syntax");
                }
                new Thread(new Threaded.Set(duplicator, sender, arguments)).start();
            } else {
                throw new WrongUsageException("stevesaddons.command." + getCommandName() + ".syntax");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName() {
        return "pastebin";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return isVisible(sender) ? Arrays.asList("put", "get") : null;
    }

    @Override
    public boolean isVisible(ICommandSender sender) {
        return usernameWhitelist.contains(sender.getCommandSenderName())
                || !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
    }
}
