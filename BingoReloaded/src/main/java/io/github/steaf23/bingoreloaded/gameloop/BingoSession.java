package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.recoverydata.RecoveryData;
import io.github.steaf23.bingoreloaded.data.recoverydata.RecoveryDataManager;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class represents a session of bingo games on a single world(group).
 * A game world can/ should only have 1 session since bingo events for a session are propagated through the world
 */
public class BingoSession
{
    public final String worldName;
    public final BingoSettingsBuilder settingsBuilder;
    public final BingoScoreboard scoreboard;
    public final TeamManager teamManager;
    private final ConfigData config;
    private GamePhase phase;

    public BingoSession(String worldName, ConfigData config)
    {
        this.worldName = worldName;
        this.config = config;
        this.settingsBuilder = new BingoSettingsBuilder(this);
        settingsBuilder.fromOther(new BingoSettingsData().getSettings(config.defaultSettingsPreset));
        this.scoreboard = new BingoScoreboard(this, config.showPlayerInScoreboard);
        this.teamManager = new TeamManager(scoreboard.getTeamBoard(), this);
        this.phase = new PregameLobby(this, config);

        BingoReloaded.scheduleTask((t) -> {
            this.teamManager.addVirtualPlayerToTeam("testPlayer", "orange");
        }, 10);

        BingoReloaded.scheduleTask((t) -> {
            World world = Bukkit.getWorld(worldName);
            for (Player p : world.getPlayers())
            {
                var playerJoinEvent = new PlayerJoinedSessionWorldEvent(p, this);
                Bukkit.getPluginManager().callEvent(playerJoinEvent);
            }
        }, 10);
    }

    public boolean isRunning()
    {
        return phase instanceof BingoGame;
    }

    public GamePhase phase()
    {
        return phase;
    }

    public void startGame()
    {
        if (isRunning())
        {
            return;
        }

        PregameLobby lobby = ((PregameLobby) phase);

        BingoSettingsBuilder gameSettings = null;

        if (config.useVoteSystem)
        {
            PregameLobby.VoteTicket voteResult = lobby.getVoteResult();
            gameSettings = settingsBuilder.getVoteResult(voteResult);
        }

        teamManager.addAutoPlayersToTeams();

        BingoCardData cardsData = new BingoCardData();
        BingoSettings settings = settingsBuilder.view();
        if (!cardsData.getCardNames().contains(settings.card()))
        {
            new TranslatedMessage(BingoTranslation.NO_CARD).color(ChatColor.RED).arg(settings.card()).sendAll(this);
            return;
        }

        if (teamManager.getParticipants().size() == 0)
        {
            Message.log("Could not start bingo since no players have joined!", worldName);
            return;
        }
        teamManager.updateActivePlayers();

        scoreboard.updateTeamScores();
        // The game is started in the constructor
        phase = new BingoGame(this, gameSettings == null ? settings : gameSettings.view(), config);
    }

    public void resumeGame()
    {
        if (isRunning())
        {
            return;
        }

        RecoveryDataManager manager = new RecoveryDataManager();
        RecoveryData recoveryData = manager.loadRecoveryData(this);
        if (recoveryData == null || recoveryData.hasNull()) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            manager.loadPlayerRecoveryData(player, teamManager);
        }

        scoreboard.updateTeamScores();
        // The game is started in the constructor
        phase = new BingoGame(this, recoveryData.getSettings(), config, recoveryData.getTimer(), recoveryData.getBingoCard(), recoveryData.getStatisticTracker());
    }

    public void endGame()
    {
        if (!isRunning()) return;

        ((BingoGame)phase).end(null);
    }

    public void removeParticipant(@NonNull BingoParticipant player)
    {
        teamManager.removeMemberFromTeam(player);
    }

    public void handleGameEnded(final BingoEndedEvent event)
    {
        phase = new PregameLobby(this, config);
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        phase.handleSettingsUpdated(event);
        teamManager.handleSettingsUpdated(event);
        settingsBuilder.fromOther(event.getNewSettings());
    }

    /**
     * Used to reset player's scoreboard when leaving this world
     * @param event
     */
    public void handlePlayerChangedWorld(final PlayerChangedWorldEvent event)
    {
        String sourceWorldName = BingoReloaded.getWorldNameOfDimension(event.getFrom());
        String targetWorldName = BingoReloaded.getWorldNameOfDimension(event.getPlayer().getWorld());

        // If player is leaving this game's world
        if (sourceWorldName.equals(this.worldName))
        {
            if (!targetWorldName.equals(this.worldName))
            {
                var leftWorldEvent = new PlayerLeftSessionWorldEvent(event.getPlayer(), this);
                Bukkit.getPluginManager().callEvent(leftWorldEvent);
            }
        }
        // If player is arriving in this world
        else if (targetWorldName.equals(this.worldName))
        {
            if (!sourceWorldName.equals(this.worldName))
            {
                var joinedWorldEvent = new PlayerJoinedSessionWorldEvent(event.getPlayer(), this);
                Bukkit.getPluginManager().callEvent(joinedWorldEvent);
            }
        }
    }

    public void handlePlayerJoinsServer(final PlayerJoinEvent event)
    {
        var joinedWorldEvent = new PlayerJoinedSessionWorldEvent(event.getPlayer(), this);
        Bukkit.getPluginManager().callEvent(joinedWorldEvent);
    }

    public void handlePlayerQuitsServer(final PlayerQuitEvent event)
    {
        var leftWorldEvent = new PlayerLeftSessionWorldEvent(event.getPlayer(), this);
        Bukkit.getPluginManager().callEvent(leftWorldEvent);
    }

    public void handlePlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        if (PlayerKit.CARD_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.WAND_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.VOTE_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.TEAM_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()))
        {
            dropEvent.setCancelled(true);
        }
    }

    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event)
    {
    }

    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event)
    {
        Player player = event.getPlayer();
        for (PotionEffectType effect : PotionEffectType.values())
        {
            player.removePotionEffect(effect);
        }

        if (isRunning())
            new TranslatedMessage(BingoTranslation.LEAVE).send(event.getPlayer());
    }
}
