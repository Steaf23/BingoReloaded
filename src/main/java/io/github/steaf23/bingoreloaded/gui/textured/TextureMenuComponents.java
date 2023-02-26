package io.github.steaf23.bingoreloaded.gui.textured;

import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextureMenuComponents
{
    public static InventoryItem[] createButton(int topLeftSlot, int bottomRightSlot, String name, String description, Consumer<Integer> action)
    {
        return createButton(topLeftSlot % 9, topLeftSlot / 9,
                bottomRightSlot % 9, bottomRightSlot / 9,
                name, description, action);
    }

    public static InventoryItem[] createButton(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY,
                                        String name, String description, Consumer<Integer> action)
    {
        InventoryItem buttonItem = buttonItem(ItemModel.INVISIBLE, name, description);

        List<InventoryItem> items = new ArrayList<>();
        for (int y = topLeftY; y < bottomRightY; y++)
        {
            for (int x = topLeftX; x < bottomRightX; x++)
            {
                items.add(buttonItem.inSlot(9 * y + x));
            }
        }

        return items.toArray(new InventoryItem[]{});
    }

    public static InventoryItem buttonItem(ItemModel model, String name, String... description)
    {
        InventoryItem item = new InventoryItem(Material.MAP, name, description);
        item.setModelData(model.data);
        return item;
    }
}
