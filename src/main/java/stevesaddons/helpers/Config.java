package stevesaddons.helpers;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stevesaddons.commands.CommandPastebin;

import java.io.File;
import java.util.Arrays;

public class Config
{
    public static boolean wailaIntegration = true;

    public static void init(File file)
    {
        Configuration config = new Configuration(file);

        Property whitelist = config.get("General", "pastebin_whitelist", new String[]{"hilburn"});
        whitelist.comment = "Add player names permitted to use Pastebin";
        CommandPastebin.usernameWhitelist.addAll(Arrays.asList(whitelist.getStringList()));

        Property waila = config.get("General", "waila integration", wailaIntegration);
        whitelist.comment = "Show set labels in WAILA tags";
        wailaIntegration = waila.getBoolean();

        config.save();
    }
}
