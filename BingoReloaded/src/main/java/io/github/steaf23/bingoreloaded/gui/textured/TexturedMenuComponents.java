package io.github.steaf23.bingoreloaded.gui.textured;

import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class TexturedMenuComponents {
    public static MenuItem itemButton(ItemModel model, String name, String... description) {
        MenuItem item = new MenuItem(Material.MAP, name, description);
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setCustomModelData(model.getData());
            item.setItemMeta(meta);
        }
        return item;
    }

    public static Collection<MenuItem> multiSlotButton(int topLeftSlot, int bottomRightSlot, String name, String description, Consumer<Integer> action) {
        MenuItem buttonItem = itemButton(ItemModel.INVISIBLE, name, description);

        int topLeftX = topLeftSlot % 9;
        int topLeftY = topLeftSlot / 9;
        int bottomRightX = bottomRightSlot % 9;
        int bottomRightY = bottomRightSlot / 9;

        Collection<MenuItem> items = new ArrayList<>();
        for (int y = topLeftY; y < bottomRightY; y++)
        {
            for (int x = topLeftX; x < bottomRightX; x++)
            {
                items.add(buttonItem.copyToSlot(x, y));
            }
        }

        return items;
    }
}
