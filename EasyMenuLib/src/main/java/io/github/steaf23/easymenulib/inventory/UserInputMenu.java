package io.github.steaf23.easymenulib.inventory;

import io.github.steaf23.easymenulib.EasyMenuLibrary;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import io.github.steaf23.easymenulib.util.EasyMenuTranslationKey;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Consumer;

public class UserInputMenu extends BasicMenu
{
    private final Consumer<String> result;

    public UserInputMenu(MenuBoard manager, String initialTitle, Consumer<String> result, String startingText) {
        super(manager, initialTitle, InventoryType.ANVIL);

        this.result = result;
    }

    public void handleTextChanged(String newText) {

    }
}
