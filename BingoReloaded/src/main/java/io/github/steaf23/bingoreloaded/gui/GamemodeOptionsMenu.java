package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import org.bukkit.Material;

public class GamemodeOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public GamemodeOptionsMenu(MenuBoard menuBoard, BingoSession session)
    {
        super(menuBoard, BingoTranslation.OPTIONS_GAMEMODE.translate(), 5);
        this.session = session;

        addAction(new ItemTemplate(1, 1,
                Material.LIME_CONCRETE, TITLE_PREFIX + "Regular 5x5"), arguments -> selectGamemode(arguments, BingoGamemode.REGULAR, CardSize.X5));
        addAction(new ItemTemplate(3, 1,
                Material.MAGENTA_CONCRETE, TITLE_PREFIX + "Lockout 5x5"), arguments -> selectGamemode(arguments, BingoGamemode.LOCKOUT, CardSize.X5));
        addAction(new ItemTemplate(5, 1,
                Material.LIGHT_BLUE_CONCRETE, TITLE_PREFIX + "Complete-All 5x5"), arguments -> selectGamemode(arguments, BingoGamemode.COMPLETE, CardSize.X5));
        addAction(new ItemTemplate(7, 1,
                Material.YELLOW_CONCRETE, TITLE_PREFIX + "HotSwap 5x5"), arguments -> selectGamemode(arguments, BingoGamemode.HOTSWAP, CardSize.X5));
        addAction(new ItemTemplate(1, 3,
                Material.GREEN_CONCRETE, TITLE_PREFIX + "Regular 3x3"), arguments -> selectGamemode(arguments, BingoGamemode.REGULAR, CardSize.X3));
        addAction( new ItemTemplate(3, 3,
                Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout 3x3"), arguments -> selectGamemode(arguments, BingoGamemode.LOCKOUT, CardSize.X3));
        addAction(new ItemTemplate(5, 3,
                Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All 3x3"), arguments -> selectGamemode(arguments, BingoGamemode.COMPLETE, CardSize.X3));
        addAction(new ItemTemplate(7, 3,
                Material.ORANGE_CONCRETE, TITLE_PREFIX + "HotSwap 3x3"), arguments -> selectGamemode(arguments, BingoGamemode.HOTSWAP, CardSize.X3));
    }

    public void selectGamemode(ActionArguments arguments, BingoGamemode chosenMode, CardSize chosenSize) {
        session.settingsBuilder.mode(chosenMode);
        session.settingsBuilder.cardSize(chosenSize);
        close(arguments.player());
        return;
    }
}
