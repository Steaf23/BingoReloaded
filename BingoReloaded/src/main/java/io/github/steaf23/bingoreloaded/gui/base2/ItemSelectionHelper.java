package io.github.steaf23.bingoreloaded.gui.base2;

import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectionHelper
{
    public static List<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir()) {
                items.add(new MenuItem(m, ""));
            }
        }
        return items;
    }
}
