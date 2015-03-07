package stevesaddons.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import stevesaddons.items.ItemLabeler;
import stevesaddons.network.MessageHandler;
import stevesaddons.network.message.LabelSyncMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class GuiLabeler extends GuiContainer implements IVerticalScrollContainer
{
    private static Comparator<GuiTextEntry> ALPHABETICAL_ORDER = new Comparator<GuiTextEntry>() {
        @Override
        public int compare(GuiTextEntry o1, GuiTextEntry o2)
        {
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getText(), o2.getText());
            return res == 0? o1.getText().compareTo(o2.getText()): res;
        }
    };
    public static final ResourceLocation TEXTURE = new ResourceLocation("stevesaddons", "textures/gui/GuiLabeler.png");
    private static final int GUI_WIDTH = 140;
    private static final int GUI_HEIGHT = 200;
    private static final int SCROLL_Y = 50;
    private static final int SCROLL_X = 11;
    private static final int SCROLL_Y_MAX = 168;
    private static final int SCROLL_X_MAX = 109;
    private static final int ENTRY_HEIGHT=16;

    private List<GuiTextEntry> strings = new ArrayList<GuiTextEntry>();
    private List<GuiTextEntry> displayStrings;
    private GuiTextEntry selected = null;
    private ItemStack stack;
    private GuiTextField searchBar;
    private GuiVerticalScrollBar scrollBar;
    private EntityPlayer player;
    public int mouseX = 0;
    public int mouseY = 0;

    public GuiLabeler(ItemStack stack, EntityPlayer player)
    {
        super(new GuiEmptyContainer());
        this.stack = stack;
        for (String string : ItemLabeler.getSavedStrings(stack))
        {
            strings.add(new GuiTextEntry(string, ENTRY_HEIGHT, 98));
        }
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;
        scrollBar = new GuiVerticalScrollBar(this, 112, SCROLL_Y, SCROLL_Y_MAX-SCROLL_Y);
        searchBar = new GuiTextField(100, 12, 10, 28);
        searchBar.setText(ItemLabeler.getLabel(stack));
        searchBar.fixCursorPos();
        displayStrings = getSearchedStrings();
        this.player = player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glPushMatrix();
        int x = (width - GUI_WIDTH) / 2;
        int y = (height - GUI_HEIGHT) / 2;
        GL11.glTranslatef(x, y, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(TEXTURE);

        drawTexturedModalRect(0, 0, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        scrollBar.draw();
        searchBar.draw();
        drawDisplayStrings();
        GL11.glPopMatrix();
    }

    private int getListViewSize()
    {
        return (SCROLL_Y_MAX-SCROLL_Y)/ENTRY_HEIGHT;
    }

    private void drawDisplayStrings()
    {
        int i = 0;
        int startIndex = Math.round((displayStrings.size() - getListViewSize()) * scrollBar.getScrollValue());
        for (GuiTextEntry entry : displayStrings)
        {
            entry.setY(SCROLL_Y + (i-startIndex)*ENTRY_HEIGHT);
            entry.isVisible = !(entry.y<SCROLL_Y || entry.y + entry.height>SCROLL_Y_MAX);
            entry.draw();
            i++;
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode)
    {
        boolean reset = false;
        if (keyCode == 1)
        {
            this.mc.thePlayer.closeScreen();
        }
        else if (keyCode == 28 && !searchBar.getText().isEmpty())
        {
            if (!isEditing() && isNewEntry(searchBar.getText()))
            {
                strings.add(new GuiTextEntry(searchBar.getText(), ENTRY_HEIGHT, 98));
                Collections.sort(strings, ALPHABETICAL_ORDER);
            }
            reset = true;
        }
        searchBar.keyTyped(character, keyCode);
        if (isEditing()) this.selected.setText(searchBar.getText());
        if (reset)
        {
            searchBar.setText("");
            searchBar.fixCursorPos();
            if (this.selected!=null)
            {
                this.selected.isEditing = false;
                this.selected.isSelected = false;
                this.selected = null;
            }
        }
        displayStrings = getSearchedStrings();

        scrollBar.setYPos(0);
    }

    private boolean isEditing()
    {
        return this.selected != null && this.selected.isEditing;
    }

    private boolean isNewEntry(String string)
    {
        for (GuiTextEntry entry : displayStrings)
        {
            if (string.equals(entry.getText())) return false;
        }
        return true;
    }

    public static void bindTexture(ResourceLocation resource) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public List<GuiTextEntry> getSearchedStrings()
    {
        List<GuiTextEntry> result = new ArrayList<GuiTextEntry>();
        Pattern pattern = Pattern.compile(Pattern.quote(searchBar.getText()), Pattern.CASE_INSENSITIVE);
        for (GuiTextEntry entry : strings)
        {
            if (pattern.matcher(entry.getText()).find()) result.add(entry);
        }
        return result;
    }

    @Override
    public void onGuiClosed()
    {
        List<String> save = new ArrayList<String>();
        for (GuiTextEntry entry: strings) save.add(entry.getText());
        ItemLabeler.saveStrings(stack, save);
        ItemLabeler.setLabel(stack, searchBar.getText());
        MessageHandler.INSTANCE.sendToServer(new LabelSyncMessage(stack,player));
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        mouseX = i - (width - xSize) / 2;
        mouseY = j - (height - ySize) / 2;
        if (isScrollBarActive())
        {
            scrollBar.handleMouseInput();
        }
        if (!(mouseX<SCROLL_X || mouseX>SCROLL_X_MAX || mouseY<SCROLL_Y || mouseY>SCROLL_Y_MAX))
        {
            for (GuiTextEntry entry : displayStrings)
            {
                entry.handleMouseInput(mouseX, mouseY);
                if (entry.isSelected)
                {
                    selected = entry;
                    searchBar.setText(entry.getText());
                    searchBar.fixCursorPos();
                }
            }
        }
    }

    @Override
    public boolean isScrollBarActive()
    {
        return displayStrings.size() > getListViewSize();
    }

    @Override
    public int getScreenWidth()
    {
        return width;
    }

    @Override
    public int getScreenHeight()
    {
        return height;
    }

    @Override
    public int getGuiWidth()
    {
        return xSize;
    }

    @Override
    public int getGuiHeight()
    {
        return ySize;
    }

    @Override
    public int getScrollAmount()
    {
        return 5;
    }
}
