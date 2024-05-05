package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerBingoMenu extends BasicMenu
{
    private static final MenuItem JOIN = new MenuItem(2, 1,
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + BingoTranslation.OPTIONS_TEAM.translate());

    private static final MenuItem LEAVE = new MenuItem(6, 1,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.OPTIONS_LEAVE.translate());

    public PlayerBingoMenu(MenuBoard manager, BingoSession session) {
        super(manager, BingoTranslation.OPTIONS_TITLE.translate(), 3);

        addAction(JOIN, args -> {
            TeamSelectionMenu teamSelection = new TeamSelectionMenu(manager, session.teamManager);
            teamSelection.open(args.player());
        });
        addAction(LEAVE, args -> {
            BingoParticipant gamePlayer = session.teamManager.getPlayerAsParticipant((Player) args.player());
            if (gamePlayer != null)
                session.removeParticipant(gamePlayer);
        });
    }
}
