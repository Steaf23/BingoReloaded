package me.steven.bingoreloaded.player;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.data.MessageSender;
import me.steven.bingoreloaded.gui.AbstractGUIInventory;
import me.steven.bingoreloaded.gui.ItemPickerUI;
import me.steven.bingoreloaded.gui.cards.BingoCard;
import me.steven.bingoreloaded.gui.cards.LockoutBingoCard;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.util.FlexibleColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.*;

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
        if (game.isGameInProgress())
        {
            MessageSender.send("game.team.no_join", player, null, ChatColor.RED);
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

        ItemPickerUI teamPicker = new ItemPickerUI(optionItems, "Pick A Team", parentUI)
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
            MessageSender.send("game.team.no_team", player, List.of(teamName), ChatColor.RED);
            return;
        }
        removePlayerFromAllTeams(player);

        activateTeam(team);

        team.addEntry(player.getName());
        MessageSender.send("game.team.join", player, List.of(teamName), ChatColor.GREEN);
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

        MessageSender.log(ChatColor.GREEN + "Successfully created " + scoreboard.getTeams().size() + " teams");
    }
}
