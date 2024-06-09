package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.command.BotCommand;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.GamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PostGamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.gui.hud.BingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.gui.hud.DisabledBingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a session of a bingo game on a single world(group).
 * A game world must only have 1 session since bingo events for a session are propagated through the world
 */
public class BingoSession
{
    public BingoSettingsBuilder settingsBuilder;
    public final BingoGameHUDGroup scoreboard;
    public final TeamManager teamManager;
    private final ConfigData config;
    private final MenuBoard menuBoard;
    private final HUDRegistry hudRegistry;
    private final GameManager gameManager;
    private final BingoSoundPlayer soundPlayer;
    private final BotCommand botCommand;

    // A bingo session controls 1 group of worlds
    private final WorldGroup worlds;
    private GamePhase phase;

    public BingoSession(GameManager gameManager, MenuBoard menuBoard, HUDRegistry hudRegistry, @NotNull WorldGroup worlds, ConfigData config) {
        this.gameManager = gameManager;
        this.menuBoard = menuBoard;
        this.hudRegistry = hudRegistry;
        this.worlds = worlds;
        this.config = config;
        if (config.disableScoreboardSidebar) {
            this.scoreboard =  new DisabledBingoGameHUDGroup(hudRegistry, this, config.showPlayerInScoreboard);
        } else {
            this.scoreboard = new BingoGameHUDGroup(hudRegistry, this, config.showPlayerInScoreboard);
        }
        this.soundPlayer = new BingoSoundPlayer(this);
        this.settingsBuilder = new BingoSettingsBuilder(this);
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        if (config.singlePlayerTeams) {
            this.teamManager = new SoloTeamManager(board, this);
        }
        else {
            this.teamManager = new BasicTeamManager(board, this);
        }

        this.botCommand = new BotCommand(teamManager);

        BingoReloaded.scheduleTask((t) -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (hasPlayer(p)) {
                    addPlayer(p);
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
        if (teamManager.getParticipantCount() == 0) {
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

    public void pauseAutomaticStart() {
        if (!(phase instanceof PregameLobby lobbyPhase)) {
            return;
        }

        lobbyPhase.playerCountTimerTogglePause();
    }

    public void prepareNextGame() {
        var event = new PrepareNextBingoGameEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        getOverworld().getPlayers().forEach(p -> {
            if (teamManager.getPlayerAsParticipant(p) == null) {
                teamManager.addMemberToTeam(new BingoPlayer(p, this), "auto");
            }
        });

        // When we came from the PostGamePhase we need to make sure to end it properly
        if (phase != null) {
            phase.end();
        }

        phase = new PregameLobby(menuBoard, hudRegistry, this, config);
        phase.setup();
    }

    /**
     * Remove participant from an active game or lobby as if they chose leave game in the team selector.
     * Does not force the player out of the world (use removePlayer for that instead)
     * @param player participant to remove from the active game or lobby.
     */
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

    public void handlePlaySoundEvent(final BingoPlaySoundEvent event) {
        soundPlayer.playSoundToEveryone(event.getSound(), event.getLoudness(), event.getPitch());
    }

    public void addPlayer(Player player) {
        var joinedWorldEvent = new PlayerJoinedSessionWorldEvent(player, this);
        Bukkit.getPluginManager().callEvent(joinedWorldEvent);
    }

    public void removePlayer(Player player) {
        var leftWorldEvent = new PlayerLeftSessionWorldEvent(player, this);
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
        if (isRunning()) {
            scoreboard.addPlayer(event.getPlayer());
        }
    }

    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {

        Player player = event.getPlayer();
        for (PotionEffectType effect : PotionEffectType.values()) {
            player.removePotionEffect(effect);
        }

        if (isRunning()) {
            new TranslatedMessage(BingoTranslation.LEAVE).send(event.getPlayer());
        }
        else {
            // remove player from session if it isn't in progress
            BingoParticipant participant = teamManager.getPlayerAsParticipant(event.getPlayer());
            teamManager.removeMemberFromTeam(participant);
        }

        scoreboard.removePlayer(player);
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

    public void handlePlayerPortalEvent(final PlayerPortalEvent event) {
        World origin = event.getFrom().getWorld();
        World target = event.getTo().getWorld();

        Location targetlocation = event.getTo();
        if (origin.getUID().equals(worlds.overworldId())) {
            // coming from the OW we can go to either the nether or the end
            if (target.getEnvironment() == World.Environment.NETHER) {
                // Nether
                targetlocation.setWorld(worlds.getNetherWorld());
            }
            else if (target.getEnvironment() == World.Environment.THE_END) {
                // The End
                targetlocation.setWorld(worlds.getEndWorld());
            }
            else {
                Message.error("could not catch player going through portal (Please report!)");
            }
        }
        else if (origin.getUID().equals(worlds.netherId())) {
            // coming from the nether we can only go to the OW
            targetlocation.setWorld(worlds.getOverworld());
        }
        else if (origin.getUID().equals(worlds.endId())) {
            // coming from the end we can go to either the overworld or to the end spawn from an outer portal.
            if (target.getEnvironment() == World.Environment.NORMAL) {
                // Overworld
                targetlocation.setWorld(worlds.getOverworld());
            }
            else if (target.getEnvironment() == World.Environment.THE_END) {
                // The End
                targetlocation.setWorld(worlds.getEndWorld());
            }
            else {
                Message.error("could not catch player going through portal (Please report!)");
            }
        }

        event.setTo(targetlocation);
    }

    public MenuBoard getMenuManager() {
        return menuBoard;
    }

    public World getOverworld() {
        return worlds.getOverworld();
    }

    public boolean ownsWorld(@NotNull World world) {
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

    public @Nullable GamePhase getPhase() {
        return phase;
    }

    public void destroy() {
    }
}
