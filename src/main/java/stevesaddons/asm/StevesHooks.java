package stevesaddons.asm;

import stevesaddons.threading.SearchItems;
import vswe.stevesfactory.components.*;

import java.util.*;

public class StevesHooks
{

    public static List updateItemSearch(ComponentMenuItem menu, String search, boolean showAll)
    {
        ScrollController searchController = ComponentHelper.getController(menu);
        Thread thread = new Thread(new SearchItems(search, searchController, showAll));
        thread.start();
        return searchController.getResult();
    }
}
