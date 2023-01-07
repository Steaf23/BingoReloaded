package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.swing.table.TableRowSorter;

public class KitOptionsUI extends AbstractGUIInventory
{
    private final BingoSettings settings;
    private static final InventoryItem HARDCORE = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[0],
            Material.RED_CONCRETE, TITLE_PREFIX + PlayerKit.HARDCORE.displayName,
            TranslationData.itemDescription("menu.kits.hardcore"));
    private static final InventoryItem NORMAL = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[1],
            Material.YELLOW_CONCRETE, TITLE_PREFIX + PlayerKit.NORMAL.displayName,
            TranslationData.itemDescription("menu.kits.normal"));
    private static final InventoryItem OVERPOWERED = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[2],
            Material.PURPLE_CONCRETE, TITLE_PREFIX + PlayerKit.OVERPOWERED.displayName,
            TranslationData.itemDescription("menu.kits.overpowered"));
    private static final InventoryItem RELOADED = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[3],
            Material.CYAN_CONCRETE, TITLE_PREFIX + PlayerKit.RELOADED.displayName,
            TranslationData.itemDescription("menu.kits.reloaded"));

    public KitOptionsUI(AbstractGUIInventory parent, BingoSettings settings)
    {
        super(45, TranslationData.itemName("menu.options.kit"), parent);
        this.settings = settings;

        fillOptions(new InventoryItem[]{HARDCORE, NORMAL, OVERPOWERED, RELOADED});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == HARDCORE.getSlot())
        {
            settings.setKit(PlayerKit.HARDCORE);
        }
        else if (slotClicked == NORMAL.getSlot())
        {
            settings.setKit(PlayerKit.NORMAL);
        }
        else if (slotClicked == OVERPOWERED.getSlot())
        {
            settings.setKit(PlayerKit.OVERPOWERED);
        }
        else if (slotClicked == RELOADED.getSlot())
        {
            settings.setKit(PlayerKit.RELOADED);
        }
        close(player);
    }
}
