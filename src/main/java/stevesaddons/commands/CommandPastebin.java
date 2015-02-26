package stevesaddons.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import stevesaddons.helpers.HttpPost;
import stevesaddons.items.ItemSFMDrive;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

public class CommandPastebin extends CommandDuplicator
{
    private static String apiKey = "367773fafa3565615286cf270e73f3de";
    private static ClipboardOwner clippy = new ClipboardOwner()
    {
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {

        }
    };
    public static CommandPastebin instance = new CommandPastebin();
    @Override
    public void doCommand(ItemStack duplicator, ICommandSender sender, String[] arguments)
    {
        if (arguments.length<2)
        {
            throw new CommandException("commands.generic.syntax");
        }
        try
        {

            if (arguments[1].equals("put"))
            {
                if (ItemSFMDrive.validateNBT(duplicator) && duplicator.hasTagCompound())
                {
                    HttpPost httpPost = new HttpPost("http://pastebin.com/api/api_post.php");
                    httpPost.put("api_option", "paste");
                    httpPost.put("api_paste_private","1");
                    httpPost.put("api_dev_key", apiKey);
                    if (arguments.length > 2) httpPost.put("api_paste_name" , arguments[2]);
                    NBTTagCompound tagCompound = (NBTTagCompound)duplicator.getTagCompound().copy();
                    tagCompound.removeTag("x");
                    tagCompound.removeTag("y");
                    tagCompound.removeTag("z");
                    tagCompound.setString("Author", sender.getCommandSenderName());
                    httpPost.put("api_paste_code", tagCompound.toString());
                    String inputLine = httpPost.getContents();
                    CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Manager saved to: "+ inputLine));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(inputLine), clippy);
                    CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Pastebin link loaded onto clipboard"));
                }
                else
                {
                    throw new CommandException("stevesaddons.command.nothingToSave");
                }
            } else if (arguments[1].equals("get"))
            {
                if (arguments.length < 3)
                {
                    throw new CommandException("commands.generic.syntax");
                }
                String name = arguments[2];
                name = name.replaceAll("http:\\/\\/pastebin.com\\/(.*)","$1");
                name = name.replaceAll("pastebin.com\\/(.*)","$1");
                HttpPost httpPost = new HttpPost("http://pastebin.com/raw.php?i="+ URLEncoder.encode(name, "UTF-8"));
                NBTBase nbtBase = JsonToNBT.func_150315_a(httpPost.getContents());
                if (nbtBase instanceof NBTTagCompound)
                {
                    NBTTagCompound tagCompound = (NBTTagCompound) nbtBase;
                    tagCompound = unstripBaseNBT(tagCompound);
                    duplicator.setTagCompound(tagCompound);
                    CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Manager loaded from: http://pastebin.com/" + name));
                }
            }
            else
            {
                throw new CommandException("commands.generic.syntax");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName()
    {
        return "pastebin";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }
}
