package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerBingoMenu extends BasicMenu
{
    private static final MenuItem JOIN = new MenuItem(2, 1,
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + BingoTranslation.OPTIONS_TEAM.translate());

    private static final MenuItem LEAVE = new MenuItem(6, 1,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.OPTIONS_LEAVE.translate());

    public PlayerBingoMenu(MenuManager manager, BingoSession session) {
        super(manager, BingoTranslation.OPTIONS_TITLE.translate(), 3);

        addAction(JOIN, p -> {
            session.teamManager.openTeamSelector(getMenuManager(), (Player) p);
        });
        addAction(LEAVE, p -> {
            BingoParticipant gamePlayer = session.teamManager.getBingoParticipant((Player) p);
            if (gamePlayer != null)
                session.removeParticipant(gamePlayer);
        });
    }
}
