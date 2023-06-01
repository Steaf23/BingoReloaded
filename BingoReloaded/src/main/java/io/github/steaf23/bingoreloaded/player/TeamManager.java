package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.game.BingoSession;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoParticipantJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantLeaveEvent;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
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

    @Nullable
    public BingoParticipant getBingoParticipant(@NonNull Player player)
    {
        for (BingoParticipant participant : getParticipants())
        {
            if (participant.getId().equals(player.getUniqueId()))
            {
                return participant;
            }
        }
        return null;
    }

    public void openTeamSelector(Player player, MenuInventory parentUI)
    {
        List<MenuItem> optionItems = new ArrayList<>();
        for (FlexColor color : FlexColor.values())
        {
            optionItems.add(new MenuItem(color.concrete, "" + color.chatColor + ChatColor.BOLD + color.getTranslatedName()));
        }

        PaginatedPickerMenu teamPicker = new PaginatedPickerMenu(optionItems, BingoTranslation.OPTIONS_TEAM.translate(), parentUI, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player)
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
        BingoTeam bingoTeam = getTeamFromName(teamName);

        if (bingoTeam == null)
        {
            return false;
        }
        if (bingoTeam.getMembers().size() == maxTeamSize)
        {
            Message.error("Team " + bingoTeam.getColoredName().asLegacyString() + " has reached it's capacity of " + maxTeamSize + " players!");
            return false;
        }

        if (getBingoParticipant(player) != null)
            removeMemberFromTeam(getBingoParticipant(player));

        bingoTeam.team.addEntry(player.getName());
        new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
                .arg(bingoTeam.getColoredName().asLegacyString())
                .send(player);

        BingoPlayer bingoPlayer = new BingoPlayer(player, bingoTeam, session);
        bingoTeam.addMember(bingoPlayer);
        var event = new BingoParticipantJoinEvent(bingoPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public boolean addVirtualPlayerToTeam(String playerName, String teamName)
    {
        BingoTeam bingoTeam = getTeamFromName(teamName);

        if (bingoTeam == null)
        {
            return false;
        }
        if (bingoTeam.getMembers().size() == maxTeamSize)
        {
            Message.error("Team " + bingoTeam.getColoredName().asLegacyString() + " has reached it's capacity of " + maxTeamSize + " players!");
            return false;
        }

        //TODO: remove player from existing team

        bingoTeam.team.addEntry(playerName);
        VirtualBingoPlayer bingoPlayer = new VirtualBingoPlayer(playerName, bingoTeam, session);
        bingoTeam.addMember(bingoPlayer);
        var event = new BingoParticipantJoinEvent(bingoPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public boolean removeMemberFromTeam(BingoParticipant player)
    {
        if (!getParticipants().contains(player))
            return false;

        var event = new BingoParticipantLeaveEvent(player);
        Bukkit.getPluginManager().callEvent(event);

        player.getTeam().removeMember(player);
        return true;
    }

    public void removeEmptyTeams()
    {
        for (Iterator<BingoTeam> it = activeTeams.iterator(); it.hasNext();)
        {
            BingoTeam team = it.next();
            if (team.getMembers().size() == 0)
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
    public Set<BingoParticipant> getParticipants()
    {
        Set<BingoParticipant> players = new HashSet<>();
        for (BingoTeam activeTeam : activeTeams)
        {
            players.addAll(activeTeam.getMembers());
        }
        for (Player p : Bukkit.getOnlinePlayers())
        {
            boolean found = false;
            for (BingoTeam t : activeTeams)
            {
                for (var player : t.getMembers())
                {
                    if (player.getId().equals(p.getUniqueId()))
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
            for (BingoParticipant participant : team.getMembers())
            {
                if (participant.gamePlayer().isEmpty() && !participant.alwaysActive())
                {
                    removeMemberFromTeam(participant);
                }
            }
        }

        removeEmptyTeams();
    }

    public Set<BingoParticipant> getParticipantsOfTeam(BingoTeam team)
    {
        return team.getMembers();
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
            removeMemberFromTeam(p);
            p.gamePlayer().ifPresent(gamePlayer -> new Message()
                    .untranslated("Team sized changed, please rejoin your team of choice!")
                    .color(ChatColor.RED)
                    .send(gamePlayer));
        });
    }

    @Nullable
    public BingoTeam getTeamFromName(String teamName)
    {
        Team team = teams.getTeam(teamName);
        FlexColor color = FlexColor.fromName(teamName);
        if (color == null || team == null)
        {
            Message.error("Team " + teamName + " does not exist!");
            return null;
        }

        if (session.isRunning() && !activeTeams.stream().anyMatch(t -> t.getColor().name.equals(teamName)))
        {
            Message.error("Team " + color.getTranslatedName() + " is not playing in this game of bingo!");
            return null;
        }

        BingoTeam bingoTeam = activateTeam(team);
        return bingoTeam;
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
        BingoParticipant participant = getBingoParticipant(event.getPlayer());
        if (participant == null || participant.gamePlayer().isEmpty())
            return;

        Player onlinePlayer = participant.gamePlayer().get();

        if (session.isRunning())
        {
            if (getParticipants().contains(participant))
            {
                new TranslatedMessage(BingoTranslation.REJOIN_SESSION).send(onlinePlayer);
                var joinEvent = new BingoParticipantJoinEvent(participant);
                Bukkit.getPluginManager().callEvent(joinEvent);
            }
        }
    }

    public void handlePlayerChangedWorld(final PlayerChangedWorldEvent event)
    {
        BingoParticipant participant = getBingoParticipant(event.getPlayer());
        if (participant == null)
            return;

        // If player is leaving this game's world(s)
        if (session.worldName.equals(BingoReloaded.getWorldNameOfDimension(event.getFrom())))
        {
            participant.getTeam().team.removeEntry(event.getPlayer().getName());
            var leaveEvent = new BingoParticipantLeaveEvent(participant);
            Bukkit.getPluginManager().callEvent(leaveEvent);
            return;
        }

        World target = event.getPlayer().getWorld();
        // If player is arriving in this world
        if (BingoReloaded.getWorldNameOfDimension(target).equals(session.worldName))
        {
            participant.getTeam().team.addEntry(event.getPlayer().getName());
            var joinEvent = new BingoParticipantJoinEvent(participant);
            Bukkit.getPluginManager().callEvent(joinEvent);
        }
    }

    public void handlePlayerShowCard(final PlayerInteractEvent event, Material deathMatchItem)
    {
        BingoParticipant participant = getBingoParticipant(event.getPlayer());
        if (participant == null)
            return;

        if (PlayerKit.CARD_ITEM.isKeyEqual(event.getItem()))
        {
            event.setCancelled(true);
            BingoTeam playerTeam = participant.getTeam();
            if (playerTeam == null)
            {
                return;
            }
            BingoCard card = playerTeam.card;

            // if the player is actually participating, show it
            if (card != null)
            {
                if (deathMatchItem != null)
                {
                    participant.showDeathMatchItem(deathMatchItem);
                    return;
                }
                card.showInventory(event.getPlayer());
            }
            else
            {
                new TranslatedMessage(BingoTranslation.NO_PLAYER_CARD).send(event.getPlayer());
            }
        }
    }
}
