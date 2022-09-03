package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamManager
{
    private final List<BingoTeam> activeTeams;
    private final BingoGame game;
    private final Scoreboard teams;

    public TeamManager(BingoGame game)
    {
        this.activeTeams = new ArrayList<>();
        this.game = game;
        this.teams = Bukkit.getScoreboardManager().getNewScoreboard();

        createTeams();
    }

    public BingoTeam getTeamOfPlayer(Player player)
    {
        for (BingoTeam t : activeTeams)
        {
            for (String entry : t.team.getEntries())
            {
                if (entry.equals(player.getName()))
                {
                    return t;
                }
            }
        }
        return null;
    }

    public void openTeamSelector(Player player, AbstractGUIInventory parentUI)
    {
        if (game.inProgress)
        {
            new Message("game.team.no_join").send(player);
            return;
        }

        List<InventoryItem> optionItems = new ArrayList<>();
        for (FlexibleColor color : FlexibleColor.values())
        {
            optionItems.add(new InventoryItem(color.concrete, color.chatColor + color.displayName));
        }

        ListPickerUI teamPicker = new ListPickerUI(optionItems, "Pick A Team", parentUI, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                FlexibleColor color = FlexibleColor.fromConcrete(clickedOption.getType());
                if (color == null)
                    return;

                addPlayerToTeam(player, color.displayName);
                close(player);
            }
        };
        teamPicker.open(player);
    }

    public void addPlayerToTeam(Player player, String teamName)
    {
        Team team = teams.getTeam(teamName);
        if (team == null)
        {
            new Message("game.team.no_team").arg(teamName).send(player);
            return;
        }
        removePlayerFromAllTeams(player);

        BingoTeam bingoTeam = activateTeam(team);

        if (bingoTeam != null)
        {
            team.addEntry(player.getName());
            new Message("game.team.join").color(ChatColor.GREEN)
                    .arg(bingoTeam.getName()).color(bingoTeam.getColor()).bold()
                    .send(player);
        }
    }

    public void removePlayerFromAllTeams(Player player)
    {
        if (!getParticipants().contains(player))
            return;
        for (Team team : teams.getTeams())
        {
            team.removeEntry(player.getName());
        }
    }

    public void removeEmptyTeams()
    {
        activeTeams.removeIf((t) -> t.team.getEntries().size() <= 0);
    }

    public void initializeCards(BingoCard masterCard)
    {
        if (masterCard instanceof LockoutBingoCard lockoutCard)
        {
            lockoutCard.teamCount = activeTeams.size();
        }
        activeTeams.forEach((t) -> t.card = masterCard.copy());
    }

    public void setCardForTeam(BingoTeam team, BingoCard card)
    {
        team.card = card;
    }

    public List<BingoTeam> getActiveTeams()
    {
        return activeTeams;
    }


    public Set<Player> getParticipants()
    {
        Set<Player> players = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers())
        {
            boolean found = false;
            for (BingoTeam t : activeTeams)
            {
                for (String entry : t.team.getEntries())
                {
                    if (entry.equals(p.getName()))
                    {
                        players.add(p);
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    break;
                }
            }
        }

        return players;
    }

    public void updateActivePlayers()
    {
        for (BingoTeam team : activeTeams)
        {
            for (String entry : team.team.getEntries())
            {
                Player p = Bukkit.getPlayer(entry);
                if (p == null || !p.isOnline())
                    removePlayerFromAllTeams(p);;
            }
        }
    }

    public BingoTeam getTeamByName(String name)
    {
        for (BingoTeam team : activeTeams)
        {
            if (team.getName().equals(name))
            {
                return team;
            }
        }

        return null;
    }

    public Set<Player> getPlayersOfTeam(BingoTeam team)
    {
        Set<Player> players = new HashSet<>();
        for (Player p : getParticipants())
        {
            if (team.equals(getTeamOfPlayer(p)))
            {
                players.add(p);
            }
        }
        return players;
    }

    public BingoTeam getLeadingTeam()
    {
        Optional<BingoTeam> leadingTeam = activeTeams.stream().max(
                (t, t2) -> t.card.getCompleteCount(t) - t2.card.getCompleteCount(t)
        );
        return leadingTeam.orElse(null);
    }

    public BingoTeam getLosingTeam()
    {
        Optional<BingoTeam> losingTeam = activeTeams.stream().min(
                (t, t2) -> t.card.getCompleteCount(t) - t2.card.getCompleteCount(t)
        );
        return losingTeam.orElse(null);
    }

    public BingoTeam activateTeam(Team team)
    {
        if (activeTeams.stream().noneMatch((t) -> t.team == team))
        {
            FlexibleColor color = FlexibleColor.fromDisplayName(team.getName());
            BingoTeam bTeam;
            if (color != null)
            {
                bTeam = new BingoTeam(team, null, color.chatColor);
            }
            else
            {
                bTeam = new BingoTeam(team, null, ChatColor.WHITE);
            }
            activeTeams.add(bTeam);
            return bTeam;
        }
        return null;
    }

    private void createTeams()
    {
        for (FlexibleColor fColor : FlexibleColor.values())
        {
            String name = fColor.displayName;
            teams.registerNewTeam(name);
        }
        Message.log(ChatColor.GREEN + "Successfully created " + teams.getTeams().size() + " teams");
    }

    public void playerQuit(Player player)
    {
        if (!getParticipants().contains(player)) return;

        removePlayerFromAllTeams(player);
        new Message("game.player.leave").send(player);
        BingoGame.takePlayerEffects(player);
    }
}
