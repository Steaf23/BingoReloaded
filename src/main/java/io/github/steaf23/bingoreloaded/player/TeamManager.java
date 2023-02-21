package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoPlayerJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoPlayerLeaveEvent;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.PaginatedPickerUI;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.*;

public class TeamManager implements Listener
{
    private final Set<BingoTeam> activeTeams;
    private final Scoreboard teams;
    private int maximumTeamSize;
    private String worldName;

    public TeamManager(Scoreboard board, String worldName)
    {
        this.activeTeams = new HashSet<>();
        this.teams = board;
        this.worldName = worldName;
        this.maximumTeamSize = GameWorldManager.get().getGameSettings(worldName).maxTeamSize;

        createTeams();
        Bukkit.getPluginManager().registerEvents(this, BingoReloaded.get());
    }

    /**
     *
     * @param player
     * @return The team of the player, null if the team is not active
     */
    public BingoTeam getTeamOfPlayer(BingoPlayer player)
    {
        return player.team();
    }

    @Nullable
    public BingoPlayer getBingoPlayer(@NonNull Player player)
    {
        for (BingoPlayer participant : getParticipants())
        {
            if (participant.playerId().equals(player.getUniqueId()))
            {
                return participant;
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

    public boolean addPlayerToTeam(Player player, String teamName)
    {
        Team team = teams.getTeam(teamName);
        FlexColor color = FlexColor.fromName(teamName);
        if (color == null)
        {
            Message.error("Team " + teamName + " does not exist!");
            return false;
        }
        if (team == null)
        {
            Message.error("Team " + color.getTranslatedName() + " does not exist, could not add " + player.getDisplayName() + " to this team!");
            return false;
        }

        if (GameWorldManager.get().isGameWorldActive(worldName) && !activeTeams.stream().anyMatch(t -> t.getColor().name.equals(teamName)))
        {
            Message.error("Team " + color.getTranslatedName() + " is not playing in this game of bingo!");
            return false;
        }

        if (getBingoPlayer(player) != null)
            removePlayerFromTeam(getBingoPlayer(player));

        BingoTeam bingoTeam = activateTeam(team);

        if (bingoTeam == null)
        {
            return false;
        }

        if (bingoTeam.players.size() == maximumTeamSize)
        {
            Message.error("Team " + color.getTranslatedName() + " has reached it's capacity of " + maximumTeamSize + " players!");
            return false;
        }

        team.addEntry(player.getName());
        new Message("game.team.join").color(ChatColor.GREEN)
                .arg(bingoTeam.getColoredName().asLegacyString())
                .send(player);

        BingoPlayer bingoPlayer = new BingoPlayer(player.getUniqueId(), bingoTeam, worldName, player.getName(), player.getDisplayName());
        bingoTeam.addPlayer(bingoPlayer);
        var event = new BingoPlayerJoinEvent(bingoPlayer, worldName);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public void removePlayerFromTeam(BingoPlayer player)
    {
        if (!getParticipants().contains(player))
            return;

        var event = new BingoPlayerLeaveEvent(player, worldName);
        Bukkit.getPluginManager().callEvent(event);

        getTeamOfPlayer(player).removePlayer(player);
    }

    public void removeEmptyTeams()
    {
        for (Iterator<BingoTeam> it = activeTeams.iterator(); it.hasNext();)
        {
            BingoTeam team = it.next();
            if (team.players.size() == 0)
            {
                it.remove();
            }
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

    public Set<BingoTeam> getActiveTeams()
    {
        return activeTeams;
    }


    /**
     * Gets all BingoPlayers regardless if they are currently in the world.
     * @return Set of BingoPlayers that have joined a team.
     */
    public Set<BingoPlayer> getParticipants()
    {
        Set<BingoPlayer> players = new HashSet<>();
        for (BingoTeam activeTeam : activeTeams)
        {
            players.addAll(activeTeam.getPlayers());
        }
        for (Player p : Bukkit.getOnlinePlayers())
        {
            boolean found = false;
            for (BingoTeam t : activeTeams)
            {
                for (var player : t.getPlayers())
                {
                    if (player.playerId().equals(p.getUniqueId()))
                    {
                        players.add(player);
                        found = true;
                    }
                }
                if (found)
                    break;
            }
        }
        return players;
    }

    public void updateActivePlayers()
    {
        for (BingoTeam team : activeTeams)
        {
            for (BingoPlayer participant : team.players)
            {
                if (participant.gamePlayer().isEmpty())
                {
                    removePlayerFromTeam(participant);
                }
            }
        }

        removeEmptyTeams();
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

    public Set<BingoPlayer> getPlayersOfTeam(BingoTeam team)
    {
        return team.players;
    }

    public BingoTeam getLeadingTeam()
    {
        Optional<BingoTeam> leadingTeam = activeTeams.stream().max(
                (t, t2) -> t.card.getCompleteCount(t) - t2.card.getCompleteCount(t2)
        );
        return leadingTeam.orElse(null);
    }

    public BingoTeam getLosingTeam()
    {
        Optional<BingoTeam> losingTeam = activeTeams.stream().min(
                (t, t2) -> t.card.getCompleteCount(t) - t2.card.getCompleteCount(t2)
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
                bTeam = new BingoTeam(team, null, color);
            }
            else
            {
                bTeam = new BingoTeam(team, null, FlexColor.WHITE);
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
            // Add dummy entry to show the prefix on the board
            t.addEntry("" + fColor.chatColor);
        }
        Message.log("Successfully created 16 teams");
    }

    public String getWorldName()
    {
        return worldName;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        BingoPlayer player = getBingoPlayer(event.getPlayer());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        Player onlinePlayer = player.gamePlayer().get();

        if (GameWorldManager.get().isGameWorldActive(worldName))
        {
            if (getParticipants().contains(player))
            {
                new Message("game.player.join_back").send(onlinePlayer);
                var joinEvent = new BingoPlayerJoinEvent(player, worldName);
                Bukkit.getPluginManager().callEvent(joinEvent);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerSwitchWorld(final PlayerChangedWorldEvent event)
    {
        BingoPlayer player = getBingoPlayer(event.getPlayer());
        if (player == null)
            return;

        // If player is leaving this game's world(s)
        if (GameWorldManager.get().doesGameWorldExist(event.getFrom()))
        {
            if (GameWorldManager.getWorldName(event.getFrom()).equals(worldName))
            {
                player.getTeam().team.removeEntry(event.getPlayer().getName());
                player.takeEffects(true);
                var leaveEvent = new BingoPlayerLeaveEvent(player, worldName);
                Bukkit.getPluginManager().callEvent(leaveEvent);
            }
            return;
        }

        World target = event.getPlayer().getWorld();
        // If player is arriving in this world
        if (GameWorldManager.get().doesGameWorldExist(target))
        {
            if (GameWorldManager.getWorldName(target).equals(worldName))
            {
                player.getTeam().team.addEntry(event.getPlayer().getName());
                player.giveEffects(GameWorldManager.get().getGameSettings(worldName).effects);
                var joinEvent = new BingoPlayerJoinEvent(player, worldName);
                Bukkit.getPluginManager().callEvent(joinEvent);
            }
        }
    }
}
