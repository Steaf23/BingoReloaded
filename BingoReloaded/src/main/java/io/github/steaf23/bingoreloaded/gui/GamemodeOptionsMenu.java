package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.game.BingoSession;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GamemodeOptionsMenu extends MenuInventory
{
    private final MenuItem[] options;
    private final BingoSession session;

    public GamemodeOptionsMenu(MenuInventory parent, BingoSession session)
    {
        super(45, BingoTranslation.OPTIONS_GAMEMODE.translate(), parent);
        this.session = session;

        options = new MenuItem[]{
                new MenuItem(2, 1,
                        Material.LIME_CONCRETE, TITLE_PREFIX + "Regular 5x5"),
                new MenuItem(4, 1,
                        Material.MAGENTA_CONCRETE, TITLE_PREFIX + "Lockout 5x5"),
                new MenuItem(6, 1,
                        Material.LIGHT_BLUE_CONCRETE, TITLE_PREFIX + "Complete-All 5x5"),
                new MenuItem(2, 3,
                        Material.GREEN_CONCRETE, TITLE_PREFIX + "Regular 3x3"),
                new MenuItem(4, 3,
                        Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout 3x3"),
                new MenuItem(6, 3,
                        Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All 3x3"),
        };
        addItems(options);
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        //TODO: move messages to BingoSettings
        BingoGamemode chosenMode = BingoGamemode.REGULAR;
        CardSize chosenSize = CardSize.X5;

        String worldName = BingoReloaded.getWorldNameOfDimension(player.getWorld());

        if (slotClicked == options[0].getSlot() || slotClicked == options[3].getSlot())
        {
            new TranslatedMessage(BingoTranslation.REGULAR_SELECTED).color(ChatColor.GOLD).sendAll(session);
        }
        else if (slotClicked == options[1].getSlot() || slotClicked == options[4].getSlot())
        {
            new TranslatedMessage(BingoTranslation.LOCKOUT_SELECTED).color(ChatColor.GOLD).sendAll(session);

            chosenMode = BingoGamemode.LOCKOUT;
        }
        else if (slotClicked == options[2].getSlot() || slotClicked == options[5].getSlot())
        {
            new TranslatedMessage(BingoTranslation.COMPLETE_SELECTED).color(ChatColor.GOLD).sendAll(session);
            chosenMode = BingoGamemode.COMPLETE;
        }

        if (slotClicked == options[0].getSlot() || slotClicked == options[1].getSlot() || slotClicked == options[2].getSlot())
        {
            new TranslatedMessage(BingoTranslation.CARDSIZE_SELECTED).color(ChatColor.GOLD).arg("5x5").sendAll(session);
        }
        else if (slotClicked == options[3].getSlot() || slotClicked == options[4].getSlot() || slotClicked == options[5].getSlot())
        {
            new TranslatedMessage(BingoTranslation.CARDSIZE_SELECTED).color(ChatColor.GOLD).arg("3x3").sendAll(session);
            chosenSize = CardSize.X3;
        }

        session.settingsBuilder.mode(chosenMode);
        session.settingsBuilder.cardSize(chosenSize);
        close(player);
    }
}
