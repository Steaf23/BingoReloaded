package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.PaginatedPickerUI;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.FlexColor;
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
    private final Scoreboard teams;
    private int maximumTeamSize;
    private String worldName;

    public TeamManager(Scoreboard board, String worldName)
    {
        this.activeTeams = new ArrayList<>();
        this.teams = board;
        this.worldName = worldName;
        this.maximumTeamSize = GameWorldManager.get().getGameSettings(worldName).maxTeamSize;

        createTeams();
    }

    // Returns null when a player is not part of a team.
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
        if (GameWorldManager.get().isGameWorldActive(worldName))
        {
            new Message("game.team.no_join").color(ChatColor.RED).send(player);
            return;
        }

        List<InventoryItem> optionItems = new ArrayList<>();
        for (FlexColor color : FlexColor.values())
        {
            optionItems.add(new InventoryItem(color.concrete, "" + color.chatColor + ChatColor.BOLD + color.getTranslatedName()));
        }

        PaginatedPickerUI teamPicker = new PaginatedPickerUI(optionItems, TranslationData.itemName("menu.options.team"), parentUI, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                FlexColor color = FlexColor.fromConcrete(clickedOption.getType());
                if (color == null)
                    return;

                addPlayerToTeam(player, color.name);
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
            Message.log("Team " + FlexColor.fromName(teamName).getTranslatedName() + " does not exist, could not add " + player.getDisplayName() + " to this team!");
            return;
        }
        if (team.getEntries().size() >= maximumTeamSize)
        {
            Message.log("Team " + FlexColor.fromName(teamName).getTranslatedName() + " has reached it's capacity of " + maximumTeamSize + " players!");
            return;
        }
        removePlayerFromAllTeams(player);

        BingoTeam bingoTeam = activateTeam(team);

        if (bingoTeam != null)
        {
            team.addEntry(player.getName());
            new Message("game.team.join").color(ChatColor.GREEN)
                    .arg(FlexColor.fromName(bingoTeam.getName()).getTranslatedName()).color(bingoTeam.getColor()).bold()
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
        List<BingoTeam> teamsToRemove = new ArrayList<>();
        for (BingoTeam t : activeTeams)
        {
            boolean remove = true;
            for (String entry : t.team.getEntries())
            {
                if (Bukkit.getPlayer(entry) != null)
                {
                    remove = false;
                }
            }
            if (remove)
            {
                teamsToRemove.add(t);
            }
        }
        for (var team : teamsToRemove)
        {
            activeTeams.remove(team);
        }
    }

    public void initializeCards(BingoCard masterCard)
    {
        if (masterCard instanceof LockoutBingoCard lockoutCard)
        {
            lockoutCard.teamCount = activeTeams.size();
        }
        activeTeams.forEach((t) -> {
            t.outOfTheGame = false;
            t.card = masterCard.copy();
        });
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
        BingoTeam bTeam;
        bTeam = activeTeams.stream().filter(
                (t) -> t.team.getName().equals(team.getName()))
                .findFirst().orElse(null);

        if(bTeam == null)
        {
            FlexColor color = FlexColor.fromName(team.getName());
            if (color != null)
            {
                bTeam = new BingoTeam(team, null, color.chatColor);
            }
            else
            {
                bTeam = new BingoTeam(team, null, ChatColor.WHITE);
            }

            activeTeams.add(bTeam);
        }
        return bTeam;
    }

    public int getCompleteCount(BingoTeam team)
    {
        return team.card.getCompleteCount(team);
    }

    private void createTeams()
    {
        for (FlexColor fColor : FlexColor.values())
        {
            String name = fColor.name;
            Team t = teams.registerNewTeam(name);
            t.setPrefix("" + ChatColor.DARK_RED + "[" + fColor.chatColor + ChatColor.BOLD + fColor.getTranslatedName() + ChatColor.DARK_RED + "] ");
            t.addEntry("" + fColor.chatColor);
        }
        Message.log(ChatColor.GREEN + "Successfully created " + teams.getTeams().size() + " teams");
    }

    public String getWorldName()
    {
        return worldName;
    }
}
