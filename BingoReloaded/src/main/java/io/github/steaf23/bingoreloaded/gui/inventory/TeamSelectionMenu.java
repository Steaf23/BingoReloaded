package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.inventory.FilterType;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.PaginatedSelectionMenu;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class TeamSelectionMenu extends PaginatedSelectionMenu
{
    private final BingoSession session;
    private final TeamManager teamManager;

    public TeamSelectionMenu(MenuBoard manager, BingoSession session) {
        super(manager, BingoTranslation.OPTIONS_TEAM.translate(), new ArrayList<>(), FilterType.DISPLAY_NAME);
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
        ItemTemplate autoItem = new ItemTemplate(Material.NETHER_STAR, "" + ChatColor.BOLD + ChatColor.ITALIC + BingoTranslation.TEAM_AUTO.translate())
                .setCompareKey("item_auto");
        if (player instanceof Player gamePlayer) {
            Optional<BingoTeam> autoTeamOpt = teamManager.getActiveTeams().getTeams().stream()
                    .filter(t -> t.getIdentifier().equals("auto")).findAny();

            if (autoTeamOpt.isEmpty()) {
                Message.error("Cannot find any teams to join! Wait for the game to re-open (if it still happens after the game is re-opened, Please report!)");
                return;
            }

            BingoTeam autoTeam = autoTeamOpt.get();

            boolean playerInAutoTeam = false;
            if (autoTeam != null && autoTeam.hasMember(player.getUniqueId())) {
                playerInAutoTeam = true;
            }
            int autoTeamMemberCount = autoTeam == null ? 0 : autoTeam.getMembers().size();
            List<String> description = new ArrayList<>();
            if (playerInAutoTeam) {
                description.add("" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + ChatColor.WHITE + gamePlayer.getDisplayName());
                description.add(" ");
                description.add("" + ChatColor.GRAY + BingoTranslation.COUNT_MORE.translate(Integer.toString(autoTeamMemberCount - 1)));
            }
            else {
                description.add("" + ChatColor.GRAY + BingoTranslation.COUNT_MORE.translate(Integer.toString(autoTeamMemberCount)));
            }
            autoItem.addDescription("joined", 1, description.toArray(new String[]{}));
        }
        optionItems.add(autoItem);
        optionItems.add(new ItemTemplate(Material.TNT, "" + ChatColor.BOLD + ChatColor.ITALIC + BingoTranslation.OPTIONS_LEAVE.translate())
                .setGlowing(true).setCompareKey("item_leave"));

        var allTeams = teamManager.getJoinableTeams();
        for (String teamId : allTeams.keySet()) {
            boolean playersTeam = false;
            TeamData.TeamTemplate teamTemplate = allTeams.get(teamId);

            boolean teamIsFull = false;
            List<String> description = new ArrayList<>();

            for (BingoTeam team : teamManager.getActiveTeams()) {
                if (!team.getIdentifier().equals(teamId))
                    continue;

                for (BingoParticipant participant : team.getMembers()) {
                    description.add("" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + ChatColor.WHITE + participant.getDisplayName());
                    if (participant.getId().equals(player.getUniqueId())) {
                        playersTeam = true;
                    }
                }

                if (teamManager.getMaxTeamSize() == team.getMembers().size()) {
                    teamIsFull = true;
                }
            }

            description.add(" ");
            if (teamIsFull) {
                description.add(ChatColor.RED + BingoTranslation.FULL_TEAM_DESC.translate());
            } else {
                description.add(ChatColor.GREEN + BingoTranslation.JOIN_TEAM_DESC.translate());
            }

            optionItems.add(ItemTemplate.createColoredLeather(teamTemplate.color(), Material.LEATHER_HELMET)
                    .setName(ChatComponentUtils.convert("" + teamTemplate.color() + ChatColor.BOLD + teamTemplate.name()))
                    .setLore(ChatComponentUtils.createComponentsFromString(description.toArray(new String[]{})))
                    .setCompareKey(teamId)
                    .setGlowing(playersTeam));
        }

        clearItems();
        addItemsToSelect(optionItems);
    }
}
