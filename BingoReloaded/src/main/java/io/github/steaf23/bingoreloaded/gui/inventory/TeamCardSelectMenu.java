package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.FilterType;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.PaginatedSelectionMenu;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamCardSelectMenu extends PaginatedSelectionMenu
{
    private final BingoSession session;

    public TeamCardSelectMenu(MenuBoard board, BingoSession session) {
        super(board, BingoMessage.SHOW_TEAM_CARD_TITLE.asPhrase(), buildTeamOptions(session), FilterType.DISPLAY_NAME);
        this.session = session;
    }

    @Override
    public boolean openOnce() {
        return true;
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player) {
        if (!session.isRunning()) {
            return;
        }

        if (!(player instanceof Player actualPlayer)) {
            ConsoleMessenger.bug("entity is not a player, cannot open menu", this);
            return;
        }

        Optional<BingoTeam> team = session.teamManager.getActiveTeams().getById(clickedOption.getCompareKey());
        if (team.isPresent() && team.get().getCard().isPresent()) {
            team.get().getCard().get().showInventory(actualPlayer);
        }
    }

    public static List<ItemTemplate> buildTeamOptions(BingoSession session) {
        List<ItemTemplate> result = new ArrayList<>();
        for (BingoTeam team : session.teamManager.getActiveTeams()) {
            team.getCard().ifPresent(card -> {
                ItemTemplate item = new ItemTemplate(Material.LEATHER_CHESTPLATE,
                        BasicMenu.applyTitleFormat(BingoMessage.SHOW_TEAM_CARD_NAME.asPhrase(team.getColoredName())),
                        INPUT_LEFT_CLICK.append(BingoMessage.SHOW_TEAM_CARD_DESC.asPhrase()))
                        .setLeatherColor(team.getColor())
                        .setCompareKey(team.getIdentifier());
                result.add(item);
            });
        }

        return result;
    }
}
