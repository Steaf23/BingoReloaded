package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.data.MessageSender;
import me.steven.bingoreloaded.gui.cards.CardSize;
import me.steven.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class GamemodeOptionsUI extends AbstractGUIInventory
{
    private static final String DESCRIPTION = "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + "Click to select this gamemode.";
    private final BingoGame game;
    private final InventoryItem[] options;

    public GamemodeOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(45, "Choose Gamemode", parent);
        this.game = game;
        options = new InventoryItem[]{
                new InventoryItem(GUIBuilder5x9.OptionPositions.SIX_CENTER1.positions[0],
                        Material.LIME_CONCRETE, TITLE_PREFIX + "Regular 5x5", DESCRIPTION),
                new InventoryItem(GUIBuilder5x9.OptionPositions.SIX_CENTER1.positions[1],
                        Material.MAGENTA_CONCRETE, TITLE_PREFIX + "Lockout 5x5", DESCRIPTION),
                new InventoryItem(GUIBuilder5x9.OptionPositions.SIX_CENTER1.positions[2],
                        Material.LIGHT_BLUE_CONCRETE, TITLE_PREFIX + "Complete-All 5x5", DESCRIPTION),
                new InventoryItem(GUIBuilder5x9.OptionPositions.SIX_CENTER1.positions[3],
                        Material.GREEN_CONCRETE, TITLE_PREFIX + "Regular 3x3", DESCRIPTION),
                new InventoryItem(GUIBuilder5x9.OptionPositions.SIX_CENTER1.positions[4],
                        Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout 3x3", DESCRIPTION),
                new InventoryItem(GUIBuilder5x9.OptionPositions.SIX_CENTER1.positions[5],
                        Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All 3x3", DESCRIPTION),
        };
        fillOptions(options);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        BingoGameMode chosenMode = BingoGameMode.REGULAR;
        CardSize chosenSize = CardSize.X5;

        if (slotClicked == options[0].getSlot() || slotClicked == options[3].getSlot())
        {
            MessageSender.send("game.settings.regular_selected", List.of(BingoGameMode.REGULAR.name), ChatColor.GREEN);
        }
        else if (slotClicked == options[1].getSlot() || slotClicked == options[4].getSlot())
        {
            MessageSender.send("game.settings.lockout_selected", List.of(BingoGameMode.REGULAR.name), ChatColor.GREEN);
            chosenMode = BingoGameMode.LOCKOUT;
        }
        else if (slotClicked == options[2].getSlot() || slotClicked == options[5].getSlot())
        {
            MessageSender.send("game.settings.complete_selected", List.of(BingoGameMode.REGULAR.name), ChatColor.GREEN);
            chosenMode = BingoGameMode.COMPLETE;
        }

        if (slotClicked == options[0].getSlot() || slotClicked == options[1].getSlot() || slotClicked == options[2].getSlot())
        {
            MessageSender.send("game.settings.cardsize", List.of("5x5"), ChatColor.GREEN);
        }
        else if (slotClicked == options[3].getSlot() || slotClicked == options[4].getSlot() || slotClicked == options[5].getSlot())
        {
            MessageSender.send("game.settings.cardsize", List.of("3x3"), ChatColor.GREEN);
            chosenSize = CardSize.X3;
        }

        game.setCardSettings(chosenMode, chosenSize);
        close(player);
    }
}
