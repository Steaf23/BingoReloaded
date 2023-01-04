package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GamemodeOptionsUI extends AbstractGUIInventory
{
    private final BingoGame game;
    private final InventoryItem[] options;

    public GamemodeOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(45, TranslationData.itemName("menu.options.mode"), parent);
        this.game = game;
        options = new InventoryItem[]{
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[0],
                        Material.LIME_CONCRETE, TITLE_PREFIX + "Regular 5x5"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[1],
                        Material.MAGENTA_CONCRETE, TITLE_PREFIX + "Lockout 5x5"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[2],
                        Material.LIGHT_BLUE_CONCRETE, TITLE_PREFIX + "Complete-All 5x5"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[3],
                        Material.PINK_CONCRETE, TITLE_PREFIX + "Countdown 5x5"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[4],
                        Material.GREEN_CONCRETE, TITLE_PREFIX + "Regular 3x3"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[5],
                        Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout 3x3"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[6],
                        Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All 3x3"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.EIGHT_CENTER1.positions[7],
                        Material.RED_CONCRETE, TITLE_PREFIX + "Countdown 3x3"),
        };
        fillOptions(options);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        //TODO: move messages to BingoSettings
        BingoGamemode chosenMode = BingoGamemode.REGULAR;
        CardSize chosenSize = CardSize.X5;

        if (slotClicked == options[0].getSlot() || slotClicked == options[4].getSlot())
        {
            new Message("game.settings.regular_selected").color(ChatColor.GOLD).sendAll();
        }
        else if (slotClicked == options[1].getSlot() || slotClicked == options[5].getSlot())
        {
            new Message("game.settings.lockout_selected").color(ChatColor.GOLD).sendAll();

            chosenMode = BingoGamemode.LOCKOUT;
        }
        else if (slotClicked == options[2].getSlot() || slotClicked == options[6].getSlot())
        {
            new Message("game.settings.complete_selected").color(ChatColor.GOLD).sendAll();
            chosenMode = BingoGamemode.COMPLETE;
        }
        else if (slotClicked == options[3].getSlot() || slotClicked == options[7].getSlot())
        {
            new Message("game.settings.countdown_selected").color(ChatColor.GOLD).sendAll();
            chosenMode = BingoGamemode.COUNTDOWN;
        }

        if (slotClicked == options[0].getSlot() || slotClicked == options[1].getSlot() || slotClicked == options[2].getSlot() || slotClicked == options[3].getSlot())
        {
            new Message("game.settings.cardsize").color(ChatColor.GOLD).arg("5x5").sendAll();
        }
        else if (slotClicked == options[4].getSlot() || slotClicked == options[5].getSlot() || slotClicked == options[6].getSlot() || slotClicked == options[7].getSlot())
        {
            new Message("game.settings.cardsize").color(ChatColor.GOLD).arg("3x3").sendAll();
            chosenSize = CardSize.X3;
        }

        game.getSettings().mode = chosenMode;
        game.getSettings().cardSize = chosenSize;
        close(player);
    }
}
