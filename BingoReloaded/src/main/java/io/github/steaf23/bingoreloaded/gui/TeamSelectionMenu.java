package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class TeamSelectionMenu extends PaginatedSelectionMenu
{
    private final TeamManager teamManager;

    public TeamSelectionMenu(MenuBoard manager, TeamManager teamManager) {
        super(manager, BingoTranslation.OPTIONS_TEAM.translate(), new ArrayList<>(), FilterType.DISPLAY_NAME);
        this.teamManager = teamManager;
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant((Player) player);
        if (participant == null)
        {
            participant = new BingoPlayer((Player) player, teamManager.getSession());
        }

        if (clickedOption.getCompareKey().equals("item_auto")) {
            teamManager.addMemberToTeam(participant, "auto");
            close(player);
            open(player);
            return;
        } else if (clickedOption.getCompareKey().equals("item_leave")) {
            teamManager.removeMemberFromTeam(participant);
            close(player);
            open(player);
            return;
        }

        teamManager.addMemberToTeam(participant, clickedOption.getCompareKey());
        close(player);
        open(player);
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);

        List<MenuItem> optionItems = new ArrayList<>();
        optionItems.add(new MenuItem(Material.NETHER_STAR, "" + ChatColor.BOLD + ChatColor.ITALIC + BingoTranslation.TEAM_AUTO.translate())
                .setCompareKey("item_auto"));
        optionItems.add(new MenuItem(Material.TNT, "" + ChatColor.BOLD + ChatColor.ITALIC + BingoTranslation.OPTIONS_LEAVE.translate())
                .setGlowing(true).setCompareKey("item_leave"));

        var allTeams = teamManager.getTeamData().getTeams();
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

            optionItems.add(MenuItem.createColoredLeather(teamTemplate.color(), Material.LEATHER_HELMET)
                    .setName("" + teamTemplate.color() + ChatColor.BOLD + teamTemplate.name())
                    .setDescription(description.toArray(new String[]{})).setCompareKey(teamId)
                    .setGlowing(playersTeam));
        }

        clearItems();
        addItemsToSelect(optionItems);
    }

    @Override
    public boolean openOnce() {
        return true;
    }
}
