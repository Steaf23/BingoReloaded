package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamManager
{
    private final List<BingoTeam> activeTeams;
    private final BingoGame game;
    private final Scoreboard scoreboard;

    public TeamManager(BingoGame game, Scoreboard scoreboard)
    {
        this.activeTeams = new ArrayList<>();
        this.game = game;
        this.scoreboard = scoreboard;

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
            BingoReloaded.print(ChatColor.RED + "You cannot join an ongoing game!", player);
            return;
        }

        List<InventoryItem> optionItems = new ArrayList<>();
        for (Team t : scoreboard.getTeams())
        {
            FlexibleColor color = FlexibleColor.fromChatColor(t.getColor());
            if (color != null)
            {
                optionItems.add(new InventoryItem(color.concrete, color.chatColor + t.getDisplayName()));
            }
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
        Team team = scoreboard.getTeam(teamName);
        if (team == null)
        {
            BingoReloaded.broadcast(ChatColor.RED + "Could not add you to team '" + teamName + "', since it just doesn't exist!");
            return;
        }
        removePlayerFromAllTeams(player);

        activateTeam(team);

        team.addEntry(player.getName());
        BingoReloaded.print("You successfully joined team " + team.getColor() + team.getDisplayName(), player);
    }

    public void removePlayerFromAllTeams(Player player)
    {
        if (!getParticipants().contains(player))
            return;
        for (Team team : scoreboard.getTeams())
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
                if (p != null)
                    if (p.isOnline())
                        break;

                removePlayerFromAllTeams(p);
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

    public void activateTeam(Team team)
    {
        if (activeTeams.stream().noneMatch((t) -> t.team == team))
        {
            activeTeams.add(new BingoTeam(team, null));
        }
    }

    private void createTeams()
    {
        for (ChatColor c : ChatColor.values())
        {
            if (c.isColor())
            {
                FlexibleColor flexColor = FlexibleColor.fromChatColor(c);
                if (flexColor == null)
                    return;

                String name = flexColor.displayName;
                scoreboard.registerNewTeam(name);
                scoreboard.getTeam(name).setColor(flexColor.chatColor);
            }
        }

        BingoReloaded.broadcast(ChatColor.GREEN + "Successfully created " + scoreboard.getTeams().size() + " teams");
        BingoReloaded.print(ChatColor.GREEN + "Successfully created " + scoreboard.getTeams().size() + " teams");
    }

    public void playerQuit(Player player)
    {
        if (!getParticipants().contains(player)) return;

        removePlayerFromAllTeams(player);
        BingoReloaded.print("You have been successfully removed from the game, use " + ChatColor.DARK_RED + "/bingo join " + ChatColor.RESET + "to come back to me :D", player);
        BingoGame.takePlayerEffects(player);
    }
}
