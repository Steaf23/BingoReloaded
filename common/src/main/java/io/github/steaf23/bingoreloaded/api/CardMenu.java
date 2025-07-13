package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CardMenu
{
    void setInfo(Component title, Component... description);
    void updateTasks(List<GameTask> tasks);
    void open(PlayerHandle entity);

    /**
     * FIXME: when menus can have changeable titles (i.e. when menu builders get added)
     */
    CardMenu copy(@Nullable Component alternateTitle);
    boolean allowViewingOtherCards();

    //FIXME: REFACTOR reimplement this in paper
//    static ItemTemplate createTeamViewItem(BingoReloaded bingo) {
//        return new ItemTemplate(ItemType.of("bundle"), bingo.applyTitleFormat(BingoMessage.SHOW_TEAM_CARD_TITLE.asPhrase())).setAction(new MenuAction()
//        {
//            @Override
//            public void use(MenuAction.ActionArguments arguments) {
//                //FIXME: ugly AF code, but by far the easiest method to implement this feature.
//
//                // The reason to not use getSessionOfPlayer is that not all players that execute this command have to be active bingo players.
//                BingoSession session = bingo.getGameManager().getSessionFromWorld(arguments.player().world());
//                if (session == null) {
//                    return;
//                }
//
//                if (!bingo.config().getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
//                    return;
//                }
//
//                arguments.menu().setOpenOnce(true);
//
//                MenuBoard board = arguments.menu().getMenuBoard();
//                new TeamCardSelectMenu(board, session).open(arguments.player());
//            }
//        });
//    }
}
