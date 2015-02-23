package stevesaddons.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandLoad extends CommandDuplicator
{
    public static CommandLoad instance = new CommandLoad();

    @Override
    public void doCommand(ItemStack duplicator, ICommandSender sender, String[] arguments)
    {
        try
        {
            String name = arguments.length == 2 ? arguments[1] : sender.getCommandSenderName();
            File file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath() + "\\managers\\" + name + ".nbt");
            if (!file.exists())
            {
                throw new CommandException("Couldn't access file: " + name + ".nbt");
            }
            NBTTagCompound tagCompound = CompressedStreamTools.read(file);
            duplicator.setTagCompound(unstripBaseNBT(tagCompound));
        } catch (IOException e)
        {
            throw new CommandException("stevesaddons.command.loadFailed");
        }
    }

    @Override
    public String getCommandName()
    {
        return "load";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }
}
