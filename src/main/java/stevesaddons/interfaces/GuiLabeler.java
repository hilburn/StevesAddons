package stevesaddons.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import stevesaddons.items.ItemLabeler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class GuiLabeler extends GuiContainer implements IVerticalScrollContainer
{
    private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        public int compare(String str1, String str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            return res == 0? str1.compareTo(str2): res;
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

    private List<String> strings;
    private List<GuiTextEntry> displayStrings = new ArrayList<GuiTextEntry>();
    private GuiTextEntry selected = null;
    private ItemStack stack;
    private GuiTextField searchBar;
    private GuiVerticalScrollBar scrollBar;
    public int mouseX = 0;
    public int mouseY = 0;

    public GuiLabeler(ItemStack stack)
    {
        super(new GuiEmptyContainer());
        this.stack = stack;
        this.strings = ItemLabeler.getSavedStrings(stack);
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;
        scrollBar = new GuiVerticalScrollBar(this, 112, SCROLL_Y, SCROLL_Y_MAX-SCROLL_Y);
        searchBar = new GuiTextField(100, 12, 10, 28);
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
        if (keyCode == 1)
        {
            this.mc.thePlayer.closeScreen();
        }
        else if (keyCode == 28 && !searchBar.getText().isEmpty())
        {
            strings.add(searchBar.getText());
            Collections.sort(strings, ALPHABETICAL_ORDER);
        }
        searchBar.keyTyped(character, keyCode);
        displayStrings = getSearchedStrings();
        scrollBar.setYPos(0);
    }

    public static void bindTexture(ResourceLocation resource) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public List<GuiTextEntry> getSearchedStrings()
    {
        List<GuiTextEntry> result = new ArrayList<GuiTextEntry>();
        Pattern pattern = Pattern.compile(Pattern.quote(searchBar.getText()), Pattern.CASE_INSENSITIVE);
        for (int index = 0; index < strings.size(); index++)
        {
            String string = strings.get(index);
            if (pattern.matcher(string).find()) result.add(new GuiTextEntry(string, index, 16, 98));
        }
        return result;
    }

    @Override
    public void onGuiClosed()
    {
        ItemLabeler.saveStrings(stack, strings);
        return;
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
                    searchBar.setText(entry.getText());
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
