package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TeamCardSelectMenu extends PaginatedDataMenu<BingoTeam>
{
    private final BingoSession session;

    public TeamCardSelectMenu(MenuBoard board, BingoSession session) {
        super(board, BingoMessage.SHOW_TEAM_CARD_TITLE.asPhrase(), session.teamManager.getActiveTeams().getTeams(), FilterType.DISPLAY_NAME);
        this.session = session;
    }

    @Override
    public boolean openOnce() {
        return true;
    }


    @Override
    public void onOptionClickedDelegate(MenuAction.ActionArguments args, BingoTeam clickedTeam) {
        if (!session.canPlayersViewCard()) {
            return;
        }

        if (clickedTeam.getCard().isPresent()) {
            clickedTeam.getCard().get().showInventory(args.player());
        }
    }

    @Override
    public Material material(BingoTeam bingoTeam, boolean selected) {
        return Material.LEATHER_CHESTPLATE;
    }

    @Override
    public Component displayName(BingoTeam bingoTeam, boolean selected) {
        return BingoReloaded.applyTitleFormat(BingoMessage.SHOW_TEAM_CARD_NAME.asPhrase(bingoTeam.getColoredName()));
    }

    @Override
    public ItemTemplate editItem(ItemTemplate item, BingoTeam bingoTeam, boolean selected) {
        return item.setLore(INPUT_LEFT_CLICK.append(BingoMessage.SHOW_TEAM_CARD_DESC.asPhrase()))
                .setLeatherColor(bingoTeam.getColor());
    }
}
