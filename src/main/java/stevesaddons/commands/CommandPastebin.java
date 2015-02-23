package stevesaddons.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import stevesaddons.items.ItemSFMDrive;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
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
                    URL page = new URL("http://pastebin.com/api/api_post.php");
                    URLConnection conn = page.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    String val = "api_option=paste&api_paste_private=1&api_dev_key=" + apiKey;
                    if (arguments.length > 2) val += "&api_paste_name=" + URLEncoder.encode(arguments[2], "UTF-8");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    NBTTagCompound tagCompound = (NBTTagCompound)duplicator.getTagCompound().copy();
                    tagCompound.removeTag("x");
                    tagCompound.removeTag("y");
                    tagCompound.removeTag("z");
                    tagCompound.setString("Author", sender.getCommandSenderName());
                    CompressedStreamTools.write(stripBaseNBT(tagCompound), new DataOutputStream(baos));
                    val += "&api_paste_code=" + URLEncoder.encode(baos.toString("UTF-8"), "UTF-8");
                    writer.write(val);
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                    {
                        CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Manager saved to: "+ inputLine));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(inputLine), clippy);
                        CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Pastebin link loaded onto clipboard"));
                        break;
                    }
                    reader.close();
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
                String url = "http://pastebin.com/raw.php";
                URL page = new URL(url);
                URLConnection conn = page.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write("i="+name);
                writer.flush();
                NBTTagCompound tagCompound = CompressedStreamTools.read(new DataInputStream(conn.getInputStream())); //TODO: why doesn't this work...
                tagCompound = unstripBaseNBT(tagCompound);
                duplicator.setTagCompound(tagCompound);
                CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Manager loaded from: http://pastebin.com/"+name));
            }
            else
            {
                throw new CommandException("commands.generic.syntax");
            }
        }
        catch(IOException e)
        {
            throw new CommandException("commands.generic.syntax");
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
