package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.inventory.FilterType;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.PaginatedSelectionMenu;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamSelectionMenu extends PaginatedSelectionMenu
{
    private final BingoSession session;
    private final TeamManager teamManager;

    private static final Component PLAYER_PREFIX = PlayerDisplay.MINI_BUILDER.deserialize("<gray><bold> ┗ </bold></gray><white>");

    public TeamSelectionMenu(MenuBoard manager, BingoSession session) {
        super(manager, BingoMessage.OPTIONS_TEAM.asPhrase(), new ArrayList<>(), FilterType.NONE);
        this.session = session;
        this.teamManager = session.teamManager;
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant((Player) player);
        if (participant == null)
        {
            participant = new BingoPlayer((Player) player, session);
        }

        session.participantMap.put(participant.getId(), participant);

        if (clickedOption.getCompareKey().equals("item_auto")) {
            teamManager.addMemberToTeam(participant, "auto");
            reopen(player);
            return;
        } else if (clickedOption.getCompareKey().equals("item_leave")) {
            teamManager.removeMemberFromTeam(participant);
            reopen(player);
            return;
        }

        teamManager.addMemberToTeam(participant, clickedOption.getCompareKey());
        reopen(player);
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);

        List<ItemTemplate> optionItems = new ArrayList<>();
        ItemTemplate autoItem = new ItemTemplate(Material.NETHER_STAR, BingoMessage.TEAM_AUTO.asPhrase().color(TextColor.fromHexString("#fdffa8")).decorate(TextDecoration.BOLD, TextDecoration.ITALIC))
                .setCompareKey("item_auto");
        if (player instanceof Player gamePlayer) {
            Optional<BingoTeam> autoTeamOpt = teamManager.getActiveTeams().getTeams().stream()
                    .filter(t -> t.getIdentifier().equals("auto")).findAny();

            if (autoTeamOpt.isEmpty()) {
                ConsoleMessenger.error("Cannot find any teams to join! Wait for the game to re-open (if it still happens after the game is re-opened, Please report!)");
                return;
            }

            BingoTeam autoTeam = autoTeamOpt.get();

            boolean playerInAutoTeam = autoTeam.hasMember(player.getUniqueId());
            int autoTeamMemberCount = autoTeam.getMembers().size();
            List<Component> description = new ArrayList<>();
            if (playerInAutoTeam) {
                description.add(PLAYER_PREFIX.append(gamePlayer.displayName()));
                description.add(Component.text(" "));
                description.add(BingoMessage.COUNT_MORE.asPhrase(Component.text(Integer.toString(autoTeamMemberCount - 1))).color(NamedTextColor.GRAY));
            }
            else {
                description.add(BingoMessage.COUNT_MORE.asPhrase(Component.text(Integer.toString(autoTeamMemberCount))).color(NamedTextColor.GRAY));
            }
            autoItem.setLore(description.toArray(Component[]::new));
        }
        optionItems.add(autoItem);
        optionItems.add(new ItemTemplate(Material.TNT, BingoMessage.OPTIONS_LEAVE.asPhrase().color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD, TextDecoration.ITALIC))
                .setGlowing(true).setCompareKey("item_leave"));

        var allTeams = teamManager.getJoinableTeams();
        for (String teamId : allTeams.keySet()) {
            boolean playersTeam = false;
            TeamData.TeamTemplate teamTemplate = allTeams.get(teamId);

            boolean teamIsFull = false;
            List<Component> players = new ArrayList<>();

            for (BingoTeam team : teamManager.getActiveTeams()) {
                if (!team.getIdentifier().equals(teamId))
                    continue;

                for (BingoParticipant participant : team.getMembers()) {
                    players.add(PLAYER_PREFIX.append(participant.getDisplayName()));
                    if (participant.getId().equals(player.getUniqueId())) {
                        playersTeam = true;
                    }
                }

                if (teamManager.getMaxTeamSize() == team.getMembers().size()) {
                    teamIsFull = true;
                }
            }

            Component teamStatus;
            if (teamIsFull) {
                teamStatus = BingoMessage.FULL_TEAM_DESC.asPhrase().color(NamedTextColor.RED);
            } else {
                teamStatus = BingoMessage.JOIN_TEAM_DESC.asPhrase().color(NamedTextColor.GREEN);
            }

            optionItems.add(ItemTemplate.createColoredLeather(teamTemplate.color(), Material.LEATHER_HELMET)
                    .setName(teamTemplate.nameComponent().color(teamTemplate.color()).decorate(TextDecoration.BOLD))
                    .setLore(players.toArray(Component[]::new))
                    .setCompareKey(teamId)
                    .setGlowing(playersTeam)
                    .addDescription("status", 1, teamStatus));
        }

        clearItems();
        addItemsToSelect(optionItems);
    }
}
