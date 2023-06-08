package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoParticipantJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantLeaveEvent;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
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

    // Contains all players that will join a team automatically when the game starts
    private final Set<UUID> automaticTeamPlayers;

    private int maxTeamSize;

    public TeamManager(Scoreboard teamBoard, BingoSession session)
    {
        this.session = session;
        this.activeTeams = new HashSet<>();
        this.teams = teamBoard;
        this.maxTeamSize = session.settingsBuilder.view().maxTeamSize();
        this.automaticTeamPlayers  =new HashSet<>();
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
        optionItems.add(new MenuItem(Material.NETHER_STAR, "" + ChatColor.BOLD + ChatColor.ITALIC + BingoTranslation.TEAM_AUTO.translate()).setKey("auto"));
        for (FlexColor color : FlexColor.values())
        {
            List<String> description = new ArrayList<>();
            for (BingoTeam team : activeTeams)
            {
                if (!team.getName().equals(color.name))
                {
                    continue;
                }
                for (BingoParticipant participant : team.getMembers())
                {
                    description.add("" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + ChatColor.WHITE + participant.getDisplayName());
                }
            }

            optionItems.add(new MenuItem(color.concrete, "" + color.chatColor + ChatColor.BOLD + color.getTranslatedName(),
                    description.toArray(new String[]{})).setKey(color.name));
        }

        PaginatedPickerMenu teamPicker = new PaginatedPickerMenu(optionItems, BingoTranslation.OPTIONS_TEAM.translate(), parentUI, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player)
            {
                if (clickedOption.getKey().equals("auto"))
                {
                    addPlayerToAutoTeam(player);
                    return;
                }

                FlexColor color = FlexColor.fromName(clickedOption.getKey());
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
        BingoTeam bingoTeam = activateTeamFromName(teamName);

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

    public boolean addPlayerToAutoTeam(Player player)
    {
        BingoParticipant participant = getBingoParticipant(player);
        if (participant != null)
            removeMemberFromTeam(participant);

        automaticTeamPlayers.add(player.getUniqueId());

        BingoPlayer bingoPlayer = new BingoPlayer(player, null, session);
        var event = new BingoParticipantJoinEvent(bingoPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public void addAutoPlayersToTeams()
    {
        record TeamCount(BingoTeam team, int count){};

        // 1. create list sorted by how many players are missing from each team using a bit of insertion sorting...
        List<TeamCount> counts = new ArrayList<>();
        for (BingoTeam team : activeTeams)
        {
            TeamCount newCount = new TeamCount(team, team.getMembers().size());
            if (counts.size() == 0)
            {
                counts.add(newCount);
                continue;
            }
            int idx = 0;
            for (TeamCount tCount : counts)
            {
                if (newCount.count <= tCount.count)
                {
                    counts.add(idx, newCount);
                    break;
                }
                idx++;
            }
            if (idx >= counts.size())
            {
                counts.add(newCount);
            }
        }
        Message.log("" + counts);

        // 2. fill this list 1 by 1 using players from the list of queued players.
        // To actually implement this, we need to take the team with the least amount of players,
        //      add a player to it, and then insert it back into the list.
        //      when the team with the least amount of players has the same amount as the biggest team, all teams have been filled.

        // Since we need to remove players from this list as we are iterating, use a direct reference to the iterator.
        for (Iterator<UUID> uuidIterator = automaticTeamPlayers.iterator(); uuidIterator.hasNext();)
        {
            UUID playerId = uuidIterator.next();
            TeamCount lowest = counts.get(0);

            // If our lowest count is the same as the highest count, all incomplete teams have been filled (or we only have 1 team)
            if (lowest.count == maxTeamSize)
            {
                Message.log("DAFUQ? " + lowest + " " + counts.get(counts.size() - 1));
                break;
            }

            counts.remove(0);

            Player player = Bukkit.getPlayer(playerId);
            addPlayerToTeam(player, lowest.team.getName());
            lowest = new TeamCount(lowest.team, lowest.count + 1);
            uuidIterator.remove();

            int idx = 0;
            for (TeamCount tCount : counts)
            {
                if (lowest.count <= tCount.count)
                {
                    counts.add(idx, lowest);

                    break;
                }
                idx++;
            }
            if (idx >= counts.size())
            {
                counts.add(lowest);
            }

        }
        Message.log("" + counts);

        // 3. if all teams are filled but there are still players left in the auto queue, add them to new teams.

        // Create the right amount of teams to allow every auto player to play.
        // (We do not need a break condition since we create the perfect amount of teams needed)
        for (int teamIdx = 0; teamIdx < automaticTeamPlayers.size() / maxTeamSize; teamIdx++)
        {
            BingoTeam team = activateAnyTeam();
            for (int memberIdx = 0; memberIdx < maxTeamSize; memberIdx++)
            {
                if (automaticTeamPlayers.size() == 0)
                {
                    // We are done here
                    break;
                }
                // It should be impossible to have the get return null, since we are checking the collections size just above here
                UUID uuid = automaticTeamPlayers.stream().findAny().get();
                addPlayerToTeam(Bukkit.getPlayer(uuid), team.getName());
                automaticTeamPlayers.remove(uuid);
            }
        }

        Message.log("Divided all players into teams!");
        Message.log("This should be 0: " + automaticTeamPlayers.size());
    }

    public boolean addVirtualPlayerToTeam(String playerName, String teamName)
    {
        BingoTeam bingoTeam = activateTeamFromName(teamName);

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
        if (!session.isRunning())
        {
            getParticipants().forEach(p -> {
                removeMemberFromTeam(p);
                p.gamePlayer().ifPresent(gamePlayer -> new Message()
                        .untranslated(BingoTranslation.TEAM_SIZE_CHANGED.translate())
                        .color(ChatColor.RED)
                        .send(gamePlayer));
            });
        }
    }

    @Nullable
    public BingoTeam activateTeamFromName(String teamName)
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

        return activateTeam(team);
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

    /**
     *
     * @return null if all teams are already active, else the team that was just activated
     */
    private BingoTeam activateAnyTeam()
    {
        for (FlexColor col : FlexColor.values())
        {
            boolean active = false;
            for (BingoTeam team : activeTeams)
            {
                if (team.getName().equals(col.name))
                {
                    active = true;
                    break;
                }
            }

            if (!active)
            {
                return activateTeamFromName(col.name);
            }
        }

        return null;
    }

//== EventHandlers ==========================================
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

    /**
     *
     * @param event
     * @param deathMatchTask the card item will show this task instead of the card if it is not null
     */
    public void handlePlayerShowCard(final PlayerInteractEvent event, @Nullable BingoTask deathMatchTask)
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
                if (deathMatchTask != null)
                {
                    participant.showDeathMatchTask(deathMatchTask);
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
