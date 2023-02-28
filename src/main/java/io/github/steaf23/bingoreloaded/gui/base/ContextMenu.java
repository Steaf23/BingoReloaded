package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ContextMenu extends MenuInventory
{
    public static final int MAX_ACTIONS = 7;
    private static final InventoryItem CLOSE = new InventoryItem(8, Material.REDSTONE, TITLE_PREFIX + TranslationData.translate("menu.exit"));
    private final List<Function<ClickType, Boolean>> actions;

    public ContextMenu(String title, MenuInventory parent)
    {
        super(9, title, parent);
        this.actions = new ArrayList<>();

        addOption(CLOSE);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == CLOSE.getSlot())
        {
            close(player);
        }
        else if (slotClicked < actions.size())
        {
            if (actions.get(slotClicked).apply(clickType))
            {
                close(player);
            }
        }
    }

    /**
     * Adds an option for players to select to this context menu.
     * Actions are carried out when the corresponding items are clicked by the player.
     * @param name Name of the action.
     * @param material Material to represent this action in the menu.
     * @param action Code to execute, return false if the inventory should not be closed by this context menu.
     *               This is useful when for example the menu gets closed by the caller instead.
     */
    public void addAction(String name, Material material, Function<ClickType, Boolean> action)
    {
        if (actions.size() >= MAX_ACTIONS)
            return;

        addOption(new InventoryItem(actions.size(), material, TITLE_PREFIX + name));
        actions.add(action);
    }

    public void clearActions()
    {
        actions.clear();
        inventory.clear();
        addOption(CLOSE);
    }
}
