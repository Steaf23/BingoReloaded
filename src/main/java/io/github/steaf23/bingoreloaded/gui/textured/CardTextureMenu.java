package io.github.steaf23.bingoreloaded.gui.textured;

import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.gui.CardMenu;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class CardTextureMenu extends CardMenu implements Textured
{
    public CardTextureMenu(CardSize size, String title)
    {
        super(size, title);
    }

    @Override
    public String getTexture()
    {
        return "ㇺ";
    }

    @Override
    public int getHorizontalImageOffset()
    {
        return 48;
    }

    @Override
    public void setInfo(String name, String... description)
    {
        infoItem = TextureMenuComponents.buttonItem(ItemModel.INFO, name, description);
        addOption(infoItem);
    }

    @Override
    public void handleOpen(InventoryOpenEvent event)
    {
        updateCounts();
        super.handleOpen(event);
    }

    public void updateCounts()
    {
        addOption(TextureMenuComponents.buttonItem(ItemModel.NUMBER_1, "You Completed 1 task in this line", "").inSlot(1, 0));
        addOption(TextureMenuComponents.buttonItem(ItemModel.NUMBER_1, " ", "").inSlot(1, 1));
        addOption(TextureMenuComponents.buttonItem(ItemModel.NUMBER_1, " ", "").inSlot(1, 2));
    }
}
