package io.github.steaf23.bingoreloaded.core.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoGameManager;
import io.github.steaf23.bingoreloaded.core.BingoSession;
import io.github.steaf23.bingoreloaded.core.cards.BingoCard;
import io.github.steaf23.bingoreloaded.core.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.core.event.BingoPlayerJoinEvent;
import io.github.steaf23.bingoreloaded.core.event.BingoPlayerLeaveEvent;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.*;

public class TeamManager
{
    private final BingoSession session;
    private final Set<BingoTeam> activeTeams;
    private final Scoreboard teams;

    private int maxTeamSize;

    public TeamManager(Scoreboard teamBoard, BingoSession session)
    {
        this.session = session;
        this.activeTeams = new HashSet<>();
        this.teams = teamBoard;
        this.maxTeamSize = session.settingsBuilder.view().maxTeamSize();
        createTeams();
    }

    /**
     *
     * @param player
     * @return The team of the player, null if the team is not active
     */
    public BingoTeam getTeamOfPlayer(BingoPlayer player)
    {
        return player.getTeam();
    }

    @Nullable
    public BingoPlayer getBingoPlayer(@NonNull Player player)
    {
        for (BingoPlayer participant : getParticipants())
        {
            if (participant.playerId.equals(player.getUniqueId()))
            {
                return participant;
            }
        }
        return null;
    }

    public void openTeamSelector(Player player, MenuInventory parentUI)
    {
        List<InventoryItem> optionItems = new ArrayList<>();
        for (FlexColor color : FlexColor.values())
        {
            optionItems.add(new InventoryItem(color.concrete, "" + color.chatColor + ChatColor.BOLD + color.getTranslatedName()));
        }

        PaginatedPickerMenu teamPicker = new PaginatedPickerMenu(optionItems, BingoReloaded.get().getTranslator().itemName("menu.options.team"), parentUI, FilterType.DISPLAY_NAME)
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

        if (session.isRunning() && !activeTeams.stream().anyMatch(t -> t.getColor().name.equals(teamName)))
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

        if (bingoTeam.players.size() == maxTeamSize)
        {
            Message.error("Team " + color.getTranslatedName() + " has reached it's capacity of " + maxTeamSize + " players!");
            return false;
        }

        team.addEntry(player.getName());
        new TranslatedMessage("game.team.join").color(ChatColor.GREEN)
                .arg(bingoTeam.getColoredName().asLegacyString())
                .send(player);

        BingoPlayer bingoPlayer = new BingoPlayer(player, bingoTeam, session);
        bingoTeam.addPlayer(bingoPlayer);
        var event = new BingoPlayerJoinEvent(bingoPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public boolean removePlayerFromTeam(BingoPlayer player)
    {
        if (!getParticipants().contains(player))
            return false;

        var event = new BingoPlayerLeaveEvent(player);
        Bukkit.getPluginManager().callEvent(event);

        getTeamOfPlayer(player).removePlayer(player);
        return true;
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
                    if (player.playerId.equals(p.getUniqueId()))
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

    //TODO: Create SettingsChangedEvent?
    public void setMaxTeamSize(int maxTeamSize)
    {
        this.maxTeamSize = maxTeamSize;
        getParticipants().forEach(p -> {
            removePlayerFromTeam(p);
            p.gamePlayer().ifPresent(gamePlayer -> new Message()
                    .untranslated("Team sized changed, please rejoin your team of choice!")
                    .color(ChatColor.RED)
                    .send(gamePlayer));
        });
    }

    private void createTeams()
    {
        for (FlexColor fColor : FlexColor.values())
        {
            String name = fColor.name;
            Team t = teams.registerNewTeam(name);
            String prefix = "" + ChatColor.DARK_RED + "[" + fColor.chatColor + ChatColor.BOLD + fColor.getTranslatedName() + ChatColor.DARK_RED + "] ";
            t.setPrefix(prefix);
            // Add dummy entry to show the prefix on the board
            t.addEntry("" + fColor.chatColor);
        }
        Message.log("Successfully created 16 teams");
    }

    public void handlePlayerJoinsServer(final PlayerJoinEvent event)
    {
        BingoPlayer player = getBingoPlayer(event.getPlayer());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        Player onlinePlayer = player.gamePlayer().get();

        if (session.isRunning())
        {
            if (getParticipants().contains(player))
            {
                new TranslatedMessage("game.player.join_back").send(onlinePlayer);
                var joinEvent = new BingoPlayerJoinEvent(player);
                Bukkit.getPluginManager().callEvent(joinEvent);
            }
        }
    }

    public void handlePlayerChangedWorld(final PlayerChangedWorldEvent event, BingoGameManager gameManager)
    {
        BingoPlayer player = getBingoPlayer(event.getPlayer());
        if (player == null)
            return;

        // If player is leaving this game's world(s)
        if (session.worldName.equals(BingoGameManager.getWorldName(event.getFrom())))
        {
            player.getTeam().team.removeEntry(event.getPlayer().getName());
            player.takeEffects(true);
            var leaveEvent = new BingoPlayerLeaveEvent(player);
            Bukkit.getPluginManager().callEvent(leaveEvent);
            return;
        }

        World target = event.getPlayer().getWorld();
        // If player is arriving in this world
        if (gameManager.doesSessionExist(target))
        {
            if (BingoGameManager.getWorldName(target).equals(session.worldName))
            {
                player.getTeam().team.addEntry(event.getPlayer().getName());
                player.giveEffects(session.settingsBuilder.view().effects());
                var joinEvent = new BingoPlayerJoinEvent(player);
                Bukkit.getPluginManager().callEvent(joinEvent);
            }
        }
    }
}
