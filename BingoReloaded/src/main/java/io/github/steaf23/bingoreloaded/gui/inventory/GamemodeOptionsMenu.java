package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public class GamemodeOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public GamemodeOptionsMenu(MenuBoard menuBoard, BingoSession session)
    {
        super(menuBoard, BingoMessage.OPTIONS_GAMEMODE.asPhrase(), 5);
        this.session = session;

        addAction(new ItemTemplate(1, 1,
                Material.LIME_CONCRETE, BasicMenu.applyTitleFormat("Regular 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.REGULAR, CardSize.X5));
        addAction(new ItemTemplate(3, 1,
                Material.MAGENTA_CONCRETE, BasicMenu.applyTitleFormat("Lockout 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.LOCKOUT, CardSize.X5));
        addAction(new ItemTemplate(5, 1,
                Material.LIGHT_BLUE_CONCRETE, BasicMenu.applyTitleFormat("Complete-All 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.COMPLETE, CardSize.X5));
        addAction(new ItemTemplate(7, 1,
                Material.YELLOW_CONCRETE, BasicMenu.applyTitleFormat("HotSwap 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.HOTSWAP, CardSize.X5));
        addAction(new ItemTemplate(1, 3,
                Material.GREEN_CONCRETE, BasicMenu.applyTitleFormat("Regular 3x3")), arguments -> selectGamemode(arguments.player(), BingoGamemode.REGULAR, CardSize.X3));
        addAction( new ItemTemplate(3, 3,
                Material.PURPLE_CONCRETE, BasicMenu.applyTitleFormat("Lockout 3x3")), arguments -> selectGamemode(arguments.player(), BingoGamemode.LOCKOUT, CardSize.X3));
        addAction(new ItemTemplate(5, 3,
                Material.CYAN_CONCRETE, BasicMenu.applyTitleFormat("Complete-All 3x3")), arguments -> selectGamemode(arguments.player(), BingoGamemode.COMPLETE, CardSize.X3));
        addAction(new ItemTemplate(7, 3,
                Material.ORANGE_CONCRETE, BasicMenu.applyTitleFormat("HotSwap 3x3")), arguments -> selectGamemode(arguments.player(), BingoGamemode.HOTSWAP, CardSize.X3));
    }

    public void selectGamemode(HumanEntity player, BingoGamemode chosenMode, CardSize chosenSize) {
        session.settingsBuilder.mode(chosenMode);
        session.settingsBuilder.cardSize(chosenSize);
        close(player);
    }
}
