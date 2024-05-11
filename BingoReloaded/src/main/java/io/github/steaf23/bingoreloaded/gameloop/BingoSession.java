package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.GamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PostGamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a session of a bingo game on a single world(group).
 * A game world must only have 1 session since bingo events for a session are propagated through the world
 */
public class BingoSession
{
    public BingoSettingsBuilder settingsBuilder;
    public final BingoScoreboard scoreboard;
    public final TeamManager teamManager;
    private final ConfigData config;
    private final MenuBoard menuBoard;
    private final PlayerSerializationData playerData;
    private final GameManager gameManager;

    // A bingo session controls 1 group of worlds
    private final WorldGroup worlds;
    private GamePhase phase;

    public BingoSession(GameManager gameManager, MenuBoard menuBoard, @NotNull WorldGroup worlds, ConfigData config, PlayerSerializationData playerData) {
        this.gameManager = gameManager;
        this.menuBoard = menuBoard;
        this.worlds = worlds;
        this.config = config;
        this.playerData = playerData;
        this.scoreboard = new BingoScoreboard(this, config.showPlayerInScoreboard && false);
        if (config.singlePlayerTeams) {
            this.teamManager = new SoloTeamManager(scoreboard.getTeamBoard(), this);
        }
        else {
            this.teamManager = new BasicTeamManager(scoreboard.getTeamBoard(), this);
        }


        BingoReloaded.scheduleTask((t) -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (hasPlayer(p)) {
                    var playerJoinEvent = new PlayerJoinedSessionWorldEvent(p, this, p.getLocation(), p.getLocation(), true);
                    Bukkit.getPluginManager().callEvent(playerJoinEvent);
                }
            }
        }, 10);
        prepareNextGame();
    }

    public boolean isRunning() {
        return phase instanceof BingoGame;
    }

    public GamePhase phase() {
        return phase;
    }

    public void startGame() {
        if (!(phase instanceof PregameLobby lobby)) {
            Message.error("Cannot start a game on this world if it is not in the lobby phase!");
            return;
        }

        BingoSettingsBuilder gameSettings = determineSettingsByVote(lobby);

        BingoCardData cardsData = new BingoCardData();
        BingoSettings settings = settingsBuilder.view();
        if (!cardsData.getCardNames().contains(settings.card())) {
            new TranslatedMessage(BingoTranslation.NO_CARD).color(ChatColor.RED).arg(settings.card()).sendAll(this);
            return;
        }

        teamManager.setup();
        if (teamManager.getParticipants().size() == 0) {
            Message.log("Could not start bingo since no players have joined!", worlds.getName());
            return;
        }

        scoreboard.updateTeamScores();

        // First make sure the previous phase (PregameLobby) is ended.
        phase.end();

        phase = new BingoGame(this, gameSettings == null ? settings : gameSettings.view(), config);
        phase.setup();
    }

    public void endGame() {
        if (!isRunning()) return;

        phase.end();
    }

    public void prepareNextGame() {
        if (config.savePlayerInformation && config.loadPlayerInformationStrategy == ConfigData.LoadPlayerInformationStrategy.AFTER_GAME) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (hasPlayer(p)) {
                    playerData.loadPlayer(p);
                }
            }
        }

        // When we came from the PostGamePhase we need to make sure to end it properly
        if (phase != null) {
            phase.end();
        }

        phase = new PregameLobby(menuBoard, this, config);

        if (settingsBuilder == null) {
            this.settingsBuilder = new BingoSettingsBuilder(this);
        }
        phase.setup();
    }

    public void removeParticipant(@NonNull BingoParticipant player) {
        teamManager.removeMemberFromTeam(player);
    }

    public void handleGameEnded(final BingoEndedEvent event) {
        phase = new PostGamePhase(this, config.gameRestartTime);
        phase.setup();
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event) {
        phase.handleSettingsUpdated(event);
        teamManager.handleSettingsUpdated(event);
    }

    public void handlePlayerTeleport(final PlayerTeleportEvent event) {
        World sourceWorld = event.getFrom().getWorld();
        World targetWorld = event.getTo().getWorld();

        // If player is leaving this game's world
        if (ownsWorld(sourceWorld)) {
            if (!ownsWorld(targetWorld)) {
                BingoReloaded.scheduleTask(t -> {
                    var leftWorldEvent = new PlayerLeftSessionWorldEvent(event.getPlayer(), this, event.getFrom(), event.getTo());
                    Bukkit.getPluginManager().callEvent(leftWorldEvent);
                }, 5);
            }
        }
        // If player is arriving in this world
        else if (ownsWorld(targetWorld)) {
            if (!ownsWorld(sourceWorld)) {
                BingoReloaded.scheduleTask(t -> {
                    boolean sourceIsBingoWorld = gameManager.getSessionFromWorld(event.getFrom().getWorld()) != null;
                    var joinedWorldEvent = new PlayerJoinedSessionWorldEvent(event.getPlayer(), this, event.getFrom(), event.getTo(), sourceIsBingoWorld);
                    Bukkit.getPluginManager().callEvent(joinedWorldEvent);
                }, 10);
            }
        }
    }

    public void handlePlayerJoinsServer(final PlayerJoinEvent event) {
        var joinedWorldEvent = new PlayerJoinedSessionWorldEvent(event.getPlayer(), this, null, event.getPlayer().getLocation(), true);
        Bukkit.getPluginManager().callEvent(joinedWorldEvent);
    }

    public void handlePlayerQuitsServer(final PlayerQuitEvent event) {
        var leftWorldEvent = new PlayerLeftSessionWorldEvent(event.getPlayer(), this, event.getPlayer().getLocation(), null);
        Bukkit.getPluginManager().callEvent(leftWorldEvent);
    }

    public void handlePlayerDropItem(final PlayerDropItemEvent dropEvent) {
        if (PlayerKit.CARD_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.WAND_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.VOTE_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.TEAM_ITEM.isCompareKeyEqual(dropEvent.getItemDrop().getItemStack())) {
            dropEvent.setCancelled(true);
        }
    }

    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        SerializablePlayer serializablePlayer = SerializablePlayer.fromPlayer(BingoReloaded.getInstance(), event.getPlayer());
        if (event.getSource() != null) {
            serializablePlayer.location = event.getSource();
        }

        if (!config.savePlayerInformation)
            return;

        // Only save player data if it does not pertain to a bingo world
        if (!event.sourceIsBingoWorld()) {
            playerData.savePlayer(serializablePlayer, false);
        }
    }

    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        if (config.savePlayerInformation && event.getDestination() != null)
            if (playerData.loadPlayer(event.getPlayer()) == null) {
                new SerializablePlayer().reset(event.getPlayer(), event.getDestination()).toPlayer(event.getPlayer());
            }

        Player player = event.getPlayer();
        for (PotionEffectType effect : PotionEffectType.values()) {
            player.removePotionEffect(effect);
        }

        if (isRunning()) {
            new TranslatedMessage(BingoTranslation.LEAVE).send(event.getPlayer());
        }
    }

    public void handleParticipantCountChangedEvent(final ParticipantCountChangedEvent event) {
        if (!isRunning()) {
            return;
        }

        if (!config.endGameWithoutTeams) {
            return;
        }

        if (teamManager.getActiveTeams().getOnlineTeamCount() <= 1) {
            endGame();
        }

        if (event.newAmount == 0) {
            endGame();
        }
    }

    public MenuBoard getMenuManager() {
        return menuBoard;
    }

    public PlayerSerializationData getPlayerData() {
        return playerData;
    }

    public World getOverworld() {
        return worlds.getOverworld();
    }

    public boolean ownsWorld(World world) {
        return worlds.hasWorld(world.getUID());
    }

    private BingoSettingsBuilder determineSettingsByVote(PregameLobby lobby) {
        if (!config.useVoteSystem) {
            return null;
        }

        PregameLobby.VoteTicket voteResult = lobby.getVoteResult();
        if (voteResult.isEmpty()) {
            return null;
        }

        BingoSettingsBuilder result = settingsBuilder.getVoteResult(voteResult);
        new Message(" ").sendAll(this);
        if (!voteResult.gamemode.isEmpty()) {
            var tuple = voteResult.gamemode.split("_");
            new TranslatedMessage(BingoTranslation.VOTE_WON).arg(BingoTranslation.OPTIONS_GAMEMODE.translate()).arg(BingoGamemode.fromDataString(tuple[0]).displayName + " " + tuple[1] + "x" + tuple[1]).sendAll(this);
        }
        if (!voteResult.kit.isEmpty()) {
            new TranslatedMessage(BingoTranslation.VOTE_WON).arg(BingoTranslation.OPTIONS_KIT.translate()).arg(PlayerKit.fromConfig(voteResult.kit).getDisplayName()).sendAll(this);
        }
        if (!voteResult.card.isEmpty()) {
            new TranslatedMessage(BingoTranslation.VOTE_WON).arg(BingoTranslation.OPTIONS_CARD.translate()).arg(voteResult.card).italic().sendAll(this);
        }
        new Message(" ").sendAll(this);

        return result;
    }

    public boolean hasPlayer(Player p) {
        return ownsWorld(p.getWorld());
    }
}
