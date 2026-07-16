package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ItemRect;
import io.github.steaf23.bingoreloaded.lib.inventory.group.SelectionModel;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeamSelectionMenu extends PaginatedDataMenu<String>
{
    private final BingoSession session;
    private final TeamManager teamManager;

    private PlayerHandle cachedPlayer = null;
    private Map<String, TeamData.TeamTemplate> allTeamsCache = new HashMap<>();

    private static final Component PLAYER_PREFIX = ComponentUtils.MINI_BUILDER.deserialize("<gray><bold> ┗ </bold></gray><white>");

    public TeamSelectionMenu(MenuBoard manager, BingoSession session) {
        super(manager, BingoMessage.OPTIONS_TEAM.asPhrase(), new ArrayList<>(), List.of(FilterType.NONE), new ItemRect(0, 1, 9, 4), SelectionModel.SelectMode.NONE);
        this.session = session;
        this.teamManager = session.teamManager;
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        // Create auto team item and add description.
        ItemTemplate autoItem = new ItemTemplate(ItemTypePaper.of(Material.NETHER_STAR), BingoMessage.TEAM_AUTO.asPhrase().color(TextColor.fromHexString("#fdffa8")).decorate(TextDecoration.BOLD, TextDecoration.ITALIC))
                .setCompareKey("item_auto");

        Optional<BingoTeam> autoTeamOpt = teamManager.getActiveTeams().getTeams().stream()
                .filter(t -> t.getIdentifier().equals("auto")).findAny();

        if (autoTeamOpt.isEmpty()) {
            ConsoleMessenger.error("Cannot find any teams to join! Wait for the game to re-open (if it still happens after the game is re-opened, Please report!)");
            return;
        }

        BingoTeam autoTeam = autoTeamOpt.get();

        boolean playerInAutoTeam = autoTeam.hasMember(player.uniqueId());
        int autoTeamMemberCount = autoTeam.getMembers().size();
        List<Component> description = new ArrayList<>();
        if (playerInAutoTeam) {
            description.add(PLAYER_PREFIX.append(player.displayName()));
            description.add(Component.text(" "));
            description.add(BingoMessage.COUNT_MORE.asPhrase(Component.text(Integer.toString(autoTeamMemberCount - 1))).color(NamedTextColor.GRAY));
        }
        else {
            description.add(BingoMessage.COUNT_MORE.asPhrase(Component.text(Integer.toString(autoTeamMemberCount))).color(NamedTextColor.GRAY));
        }

        autoItem.setLore(description.toArray(Component[]::new));
        ItemTemplate leaveItem = new ItemTemplate(ItemTypePaper.of(Material.TNT), BingoMessage.OPTIONS_LEAVE.asPhrase().color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD, TextDecoration.ITALIC))
                .setGlowing(true).setCompareKey("item_leave");

        addAction(autoItem.setSlot(0, 0), args -> {
            PlayerHandle p = args.player();
            BingoParticipant participant = teamManager.getPlayerAsParticipant(p);
            if (participant == null)
            {
                participant = new BingoPlayer(p, session);
            }

            teamManager.addMemberToTeam(participant, "auto");
            reopen(p);
        });
        addAction(leaveItem.setSlot(1, 0), args -> {
            PlayerHandle p = args.player();
            BingoParticipant participant = teamManager.getPlayerAsParticipant(p);
            if (participant == null)
            {
                participant = new BingoPlayer(p, session);
            }

            teamManager.removeMemberFromTeam(participant);
            reopen(p);
        });

        allTeamsCache = teamManager.getJoinableTeams();
        cachedPlayer = player;
        setData(allTeamsCache.keySet());
    }

    @Override
    public void onOptionClickedDelegate(MenuAction.ActionArguments args, String clickedTeam) {
        PlayerHandle player = args.player();
        BingoParticipant participant = teamManager.getPlayerAsParticipant(player);
        if (participant == null)
        {
            participant = new BingoPlayer(player, session);
        }

        teamManager.addMemberToTeam(participant, clickedTeam);
        reopen(player);
    }

    @Override
    public Material material(String s, boolean selected) {
        return Material.LEATHER_HELMET;
    }

    @Override
    public Component displayName(String teamKey, boolean selected) {
        TeamData.TeamTemplate teamTemplate = allTeamsCache.get(teamKey);
        return teamTemplate.nameComponent().color(teamTemplate.color()).decorate(TextDecoration.BOLD);
    }

    @Override
    public ItemTemplate editItem(ItemTemplate item, String teamKey, boolean selected) {
        TeamData.TeamTemplate teamTemplate = allTeamsCache.get(teamKey);
        boolean playersTeam = false;

        boolean teamIsFull = false;
        List<Component> players = new ArrayList<>();

        for (BingoTeam team : teamManager.getActiveTeams()) {
            if (!team.getIdentifier().equals(teamKey))
                continue;

            for (BingoParticipant participant : team.getMembers()) {
                players.add(PLAYER_PREFIX.append(participant.getDisplayName()));
                if (participant.getId().equals(cachedPlayer.uniqueId())) {
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

        return item
                .setLeatherColor(teamTemplate.color())
                .setLore(players.toArray(Component[]::new))
                .setGlowing(playersTeam)
                .addDescription("status", 1, teamStatus);
    }
}
