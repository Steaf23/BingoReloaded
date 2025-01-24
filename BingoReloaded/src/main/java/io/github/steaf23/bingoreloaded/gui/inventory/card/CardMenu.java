package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamCardSelectMenu;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.inventory.item.action.MenuAction;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface CardMenu
{
    void setInfo(Component title, Component... description);
    void updateTasks(List<GameTask> tasks);
    void open(HumanEntity entity);
    CardMenu copy();
    boolean allowViewingOtherCards();

    static ItemTemplate createTeamEditItem() {
        return new ItemTemplate(Material.BUNDLE, BasicMenu.applyTitleFormat(BingoMessage.SHOW_TEAM_CARD_TITLE.asPhrase())).setAction(new MenuAction()
        {
            @Override
            public void use(ActionArguments arguments) {
                //FIXME: ugly AF code, but by far the easiest method to implement this feature.

                // The reason to not use getSessionOfPlayer is that not all players that execute this command have to be active bingo players.
                BingoSession session = BingoReloaded.getInstance().getGameManager().getSessionFromWorld(arguments.player().getWorld());
                if (session == null) {
                    return;
                }

                if (!BingoReloaded.getInstance().config().getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
                    return;
                }

                arguments.menu().setOpenOnce(true);

                MenuBoard board = arguments.menu().getMenuBoard();
                new TeamCardSelectMenu(board, session).open(arguments.player());
            }
        });
    }
}
