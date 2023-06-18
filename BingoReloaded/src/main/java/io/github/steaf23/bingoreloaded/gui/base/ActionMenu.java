package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ActionMenu extends MenuInventory {
    private final Map<String, Consumer<Player>> options;

    public ActionMenu(int size, String title, MenuInventory parent) {
        super(size, title, parent);
        this.options = new HashMap<>();
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType) {
        MenuItem clickedItem = new MenuItem(slotClicked, event.getCurrentItem());
        if (options.containsKey(clickedItem.getCompareKey())) {
            if (clickedItem.getCompareKey().endsWith("_close")) {
                close(player);
            }
            Consumer<Player> action = options.get(clickedItem.getCompareKey());

            if (action == null)
                return;

            action.accept(player);
        }
    }

    public String addAction(MenuItem button, Consumer<Player> whenClicked) {
        return addAction(button, whenClicked, true);
    }

    public String addAction(MenuItem button, Consumer<Player> whenClicked, boolean closeWhenClicked) {
        String id = "action_" + options.size() + (closeWhenClicked ? "_close" : "");
        button.setCompareKey(id);
        addItem(button);
        options.put(id, whenClicked);
        return id;
    }

    public String addCloseAction(int slot) {
        if (slot >= internalInventory().getSize()) {
            slot = internalInventory().getSize() - 1;
        }

        return addAction(new MenuItem(slot, Material.BARRIER,
                "" + ChatColor.RED + ChatColor.BOLD + BingoTranslation.MENU_EXIT.translate()), (p) -> {}, true);
    }
}
