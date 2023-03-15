package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.core.BingoGamemode;
import io.github.steaf23.bingoreloaded.core.BingoGameManager;
import io.github.steaf23.bingoreloaded.core.BingoSettings;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.core.cards.CardSize;
import io.github.steaf23.bingoreloaded.util.GUIPreset5x9;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GamemodeOptionsMenu extends MenuInventory
{
    private final InventoryItem[] options;
    private final BingoGame game;
    private final BingoSettings settings;

    public GamemodeOptionsMenu(MenuInventory parent, BingoGame game, BingoSettings settings)
    {
        super(45, BingoReloaded.data().translationData.itemName("menu.options.mode"), parent);
        this.game = game;
        this.settings = settings;

        options = new InventoryItem[]{
                new InventoryItem(GUIPreset5x9.SIX_CENTER1.positions[0],
                        Material.LIME_CONCRETE, TITLE_PREFIX + "Regular 5x5"),
                new InventoryItem(GUIPreset5x9.SIX_CENTER1.positions[1],
                        Material.MAGENTA_CONCRETE, TITLE_PREFIX + "Lockout 5x5"),
                new InventoryItem(GUIPreset5x9.SIX_CENTER1.positions[2],
                        Material.LIGHT_BLUE_CONCRETE, TITLE_PREFIX + "Complete-All 5x5"),
                new InventoryItem(GUIPreset5x9.SIX_CENTER1.positions[3],
                        Material.GREEN_CONCRETE, TITLE_PREFIX + "Regular 3x3"),
                new InventoryItem(GUIPreset5x9.SIX_CENTER1.positions[4],
                        Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout 3x3"),
                new InventoryItem(GUIPreset5x9.SIX_CENTER1.positions[5],
                        Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All 3x3"),
        };
        fillOptions(options);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        //TODO: move messages to BingoSettings
        BingoGamemode chosenMode = BingoGamemode.REGULAR;
        CardSize chosenSize = CardSize.X5;

        String worldName = BingoGameManager.getWorldName(player.getWorld());

        if (slotClicked == options[0].getSlot() || slotClicked == options[3].getSlot())
        {
            new TranslatedMessage("game.settings.regular_selected").color(ChatColor.GOLD).sendAll(game);
        }
        else if (slotClicked == options[1].getSlot() || slotClicked == options[4].getSlot())
        {
            new TranslatedMessage("game.settings.lockout_selected").color(ChatColor.GOLD).sendAll(game);

            chosenMode = BingoGamemode.LOCKOUT;
        }
        else if (slotClicked == options[2].getSlot() || slotClicked == options[5].getSlot())
        {
            new TranslatedMessage("game.settings.complete_selected").color(ChatColor.GOLD).sendAll(game);
            chosenMode = BingoGamemode.COMPLETE;
        }

        if (slotClicked == options[0].getSlot() || slotClicked == options[1].getSlot() || slotClicked == options[2].getSlot())
        {
            new TranslatedMessage("game.settings.cardsize").color(ChatColor.GOLD).arg("5x5").sendAll(game);
        }
        else if (slotClicked == options[3].getSlot() || slotClicked == options[4].getSlot() || slotClicked == options[5].getSlot())
        {
            new TranslatedMessage("game.settings.cardsize").color(ChatColor.GOLD).arg("3x3").sendAll(game);
            chosenSize = CardSize.X3;
        }

        settings.mode = chosenMode;
        settings.cardSize = chosenSize;
        close(player);
    }
}
