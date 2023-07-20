package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.util.Message;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UserInputMenu implements Menu
{
    record MenuTemplate(String title, Consumer<String> result, String startingText, HumanEntity player) {}

    private final MenuTemplate template;
    private AnvilGUI gui;
    private final MenuManager manager;
    private static final MenuItem EMPTY = new MenuItem(Material.ELYTRA, "" + ChatColor.GRAY + ChatColor.BOLD + BingoTranslation.MENU_CLEAR_FILTER.translate(), "");
    private static final MenuItem ACCEPT = new MenuItem(Material.DIAMOND, "" + ChatColor.AQUA + ChatColor.BOLD + BingoTranslation.MENU_ACCEPT.translate(), "");

    public UserInputMenu(MenuManager manager, String title, Consumer<String> result, HumanEntity player, String startingText) {
        this.manager = manager;
        this.template = new MenuTemplate(title, result, startingText, player);
        this.manager.open(this, player);
    }

    private AnvilGUI openAnvilUI(String title,  Consumer<String> result, String startingText, HumanEntity player) {
        return new AnvilGUI.Builder()
                .onClose(state -> {
                    manager.close(this, state.getPlayer());
                })
                .title(Message.PREFIX_STRING_SHORT + " " + ChatColor.DARK_RED + title)
                .text(startingText.isEmpty() ? "name" : startingText)
                .itemRight(EMPTY)
                .itemLeft(new ItemStack(Material.ELYTRA))
                .onClick((slot, state) -> {
                    if (slot == AnvilGUI.Slot.INPUT_RIGHT) {
                        manager.close(this, state.getPlayer());
                        result.accept("");
                        return Collections.emptyList();
                    }
                    else if (slot == AnvilGUI.Slot.OUTPUT) {
                        manager.close(this, state.getPlayer());
                        result.accept(state.getText());
                        return Collections.emptyList();
                    }
                    return Collections.emptyList();
                })
                .itemOutput(ACCEPT)
                .plugin(BingoReloaded.getPlugin(BingoReloaded.class))
                .open((Player) player);
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        this.gui = openAnvilUI(template.title, template.result, template.startingText, template.player);
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, MenuItem clickedItem, ClickType clickType) {
        return false;
    }

    @Override
    public boolean onDrag(InventoryDragEvent event) {
        return false;
    }

    @Override
    public void beforeClosing(HumanEntity player) {
    }

    @Override
    public Inventory getInventory() {
        return gui == null ? null : gui.getInventory();
    }

    @Override
    public void openInventory(HumanEntity player) {
    }

    @Override
    public void closeInventory(HumanEntity player) {
    }

    @Override
    public boolean openOnce() {
        return true;
    }
}
