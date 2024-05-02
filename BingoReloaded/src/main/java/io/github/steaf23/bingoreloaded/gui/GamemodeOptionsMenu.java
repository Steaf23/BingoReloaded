package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.Menu;
import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GamemodeOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public GamemodeOptionsMenu(MenuBoard menuBoard, BingoSession session)
    {
        super(menuBoard, BingoTranslation.OPTIONS_GAMEMODE.translate(), 5);
        this.session = session;

        addAction(new MenuItem(1, 1,
                Material.LIME_CONCRETE, TITLE_PREFIX + "Regular 5x5"), player -> selectGamemode(player, BingoGamemode.REGULAR, CardSize.X5));
        addAction(new MenuItem(3, 1,
                Material.MAGENTA_CONCRETE, TITLE_PREFIX + "Lockout 5x5"), player -> selectGamemode(player, BingoGamemode.LOCKOUT, CardSize.X5));
        addAction(new MenuItem(5, 1,
                Material.LIGHT_BLUE_CONCRETE, TITLE_PREFIX + "Complete-All 5x5"), player -> selectGamemode(player, BingoGamemode.COMPLETE, CardSize.X5));
        addAction(new MenuItem(7, 1,
                Material.YELLOW_CONCRETE, TITLE_PREFIX + "HotSwap 5x5"), player -> selectGamemode(player, BingoGamemode.HOTSWAP, CardSize.X5));
        addAction(new MenuItem(1, 3,
                Material.GREEN_CONCRETE, TITLE_PREFIX + "Regular 3x3"), player -> selectGamemode(player, BingoGamemode.REGULAR, CardSize.X3));
        addAction( new MenuItem(3, 3,
                Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout 3x3"), player -> selectGamemode(player, BingoGamemode.LOCKOUT, CardSize.X3));
        addAction(new MenuItem(5, 3,
                Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All 3x3"), player -> selectGamemode(player, BingoGamemode.COMPLETE, CardSize.X3));
        addAction(new MenuItem(7, 3,
                Material.ORANGE_CONCRETE, TITLE_PREFIX + "HotSwap 3x3"), player -> selectGamemode(player, BingoGamemode.HOTSWAP, CardSize.X3));
    }

    public void selectGamemode(HumanEntity player, BingoGamemode chosenMode, CardSize chosenSize) {
        session.settingsBuilder.mode(chosenMode);
        session.settingsBuilder.cardSize(chosenSize);
        close(player);
        return;
    }
}
