package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.event.BingoPlaySoundEvent;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantLeftTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PrepareNextBingoGameEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.GamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PostGamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteCategory;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.gui.hud.BingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.gui.hud.DisabledBingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.gui.hud.TeamDisplay;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

/**
 * This class represents a session of a bingo game on a single world(group).
 * A game world must only have 1 session since bingo events for a session are propagated through the world
 */
public class BingoSession implements ForwardingAudience
{
    public BingoSettingsBuilder settingsBuilder;
    public final BingoGameHUDGroup scoreboard;
    public final TeamManager teamManager;
    private final BingoConfigurationData config;
    private final MenuBoard menuBoard;
    private final HUDRegistry hudRegistry;
    private final TeamDisplay teamDisplay;

    // A bingo session controls 1 group of worlds
    private final WorldGroup worlds;
    private GamePhase phase;

    public BingoSession(MenuBoard menuBoard, HUDRegistry hudRegistry, @NotNull WorldGroup worlds, BingoConfigurationData config) {
        this.menuBoard = menuBoard;
        this.hudRegistry = hudRegistry;
        this.worlds = worlds;
        this.config = config;
        if (config.disableScoreboardSidebar) {
            this.scoreboard = new DisabledBingoGameHUDGroup(hudRegistry, this, config.showPlayerInScoreboard);
        } else {
            this.scoreboard = new BingoGameHUDGroup(hudRegistry, this, config.showPlayerInScoreboard);
        }
        this.settingsBuilder = new BingoSettingsBuilder(this);
        if (config.singlePlayerTeams) {
            this.teamManager = new SoloTeamManager(this);
        } else {
            this.teamManager = new BasicTeamManager(this);
        }

        this.teamDisplay = new TeamDisplay(this);
        this.phase = null;

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
            ConsoleMessenger.error("Cannot start a game on this world if it is not in the lobby phase!");
            return;
        }

        BingoSettingsBuilder gameSettings = determineSettingsByVote(lobby);

        BingoCardData cardsData = new BingoCardData();
        BingoSettings settings = settingsBuilder.view();
        if (!cardsData.getCardNames().contains(settings.card())) {
            BingoMessage.NO_CARD.sendToAudience(this, NamedTextColor.RED, Component.text(settings.card()));
            return;
        }

        if (teamManager.getParticipantCount() == 0) {
            ConsoleMessenger.log("Could not start bingo since no players have joined!", worlds.worldName());
            teamManager.reset();
            return;
        }

        teamManager.setup();
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
        teamManager.reset();
        var event = new PrepareNextBingoGameEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        // When we came from the PostGamePhase we need to make sure to end it properly
        if (phase != null) {
            phase.end();
        }

        phase = new PregameLobby(menuBoard, hudRegistry, this, config);
        phase.setup();

        getOverworld().getPlayers().forEach(p -> {
            if (teamManager.getPlayerAsParticipant(p) == null) {
                teamManager.addMemberToTeam(new BingoPlayer(p, this), "auto");
            }
        });
    }

    /**
     * Remove participant from an active game or lobby as if they chose leave game in the team selector.
     * Does not force the player out of the world (use removePlayer for that instead)
     *
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
        playSound(Sound.sound().type(event.getSound()).volume(event.getLoudness()).pitch(event.getPitch()).build());
    }

    public void addPlayer(Player player) {
        var joinedWorldEvent = new PlayerJoinedSessionWorldEvent(player, this);
        Bukkit.getPluginManager().callEvent(joinedWorldEvent);

        BingoReloaded.sendResourcePack(player);
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
        BingoReloaded.scheduleTask(t -> {
            teamManager.handlePlayerJoinedSessionWorld(event);
            phase.handlePlayerJoinedSessionWorld(event);

            if (isRunning()) {
                scoreboard.addPlayer(event.getPlayer());
            }
            teamDisplay.update();
        });
    }

    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        // Clear player's teams before anything else.
        // This is because they might join another bingo as a result of leaving this one, so we have to remove the player's team display at this moment
        teamDisplay.clearTeamsForPlayer(event.getPlayer());

        BingoReloaded.scheduleTask(t -> {
            teamManager.handlePlayerLeftSessionWorld(event);
            phase.handlePlayerLeftSessionWorld(event);

            Player player = event.getPlayer();
            player.clearActivePotionEffects();

            if (isRunning()) {
                BingoMessage.LEAVE.sendToAudience(event.getPlayer());
            }

            scoreboard.removePlayer(player);
            teamDisplay.update();

            if (!config.endGameWithoutTeams) {
                return;
            }

            if (teamManager.getActiveTeams().getOnlineTeamCount() <= 1 || teamManager.getActiveTeams().getAllOnlineParticipants().isEmpty()) {
                if (isRunning()) {
                    ConsoleMessenger.log(Component.text("Ending game because there is no competition anymore.").color(NamedTextColor.LIGHT_PURPLE), Component.text(worlds.worldName()));
                }
                endGame();
                return;
            }
        });
    }

    public void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {
        phase.handleParticipantJoinedTeam(event);
        teamDisplay.update();
    }

    public void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {
        phase.handleParticipantLeftTeam(event);
        teamDisplay.update();
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
            } else if (target.getEnvironment() == World.Environment.THE_END) {
                // The End
                targetlocation.setWorld(worlds.getEndWorld());
            } else {
                ConsoleMessenger.bug("Could not catch player going through portal", this);
            }
        } else if (origin.getUID().equals(worlds.netherId())) {
            // coming from the nether we can only go to the OW
            targetlocation.setWorld(worlds.getOverworld());
        } else if (origin.getUID().equals(worlds.endId())) {
            // coming from the end we can go to either the overworld or to the end spawn from an outer portal.
            if (target.getEnvironment() == World.Environment.NORMAL) {
                // Overworld
                targetlocation.setWorld(worlds.getOverworld());
            } else if (target.getEnvironment() == World.Environment.THE_END) {
                // The End
                targetlocation.setWorld(worlds.getEndWorld());
            } else {
                ConsoleMessenger.bug("Could not catch player going through portal", this);
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

        VoteTicket voteResult = VoteTicket.getVoteResult(lobby.getAllVotes());
        if (voteResult.isEmpty()) {
            return null;
        }

        BingoSettingsBuilder result = settingsBuilder.applyVoteResult(voteResult);

        Consumer<VoteCategory<?>> sendVoteMessage = category -> {
            BingoMessage.VOTE_WON.sendToAudience(this,
                    category.asComponent(),
                    category.getValueComponent(voteResult.getVote(category)).decorate(TextDecoration.BOLD));
        };
        this.sendMessage(Component.text(" "));

        if (voteResult.containsCategory(VoteTicket.CATEGORY_GAMEMODE)) {
            sendVoteMessage.accept(VoteTicket.CATEGORY_GAMEMODE);
        }
        if (voteResult.containsCategory(VoteTicket.CATEGORY_KIT)) {
            sendVoteMessage.accept(VoteTicket.CATEGORY_KIT);
        }
        if (voteResult.containsCategory(VoteTicket.CATEGORY_CARD)) {
            sendVoteMessage.accept(VoteTicket.CATEGORY_CARD);
        }
        if (voteResult.containsCategory(VoteTicket.CATEGORY_CARDSIZE)) {
            sendVoteMessage.accept(VoteTicket.CATEGORY_CARDSIZE);
        }

        this.sendMessage(Component.text(" "));

        return result;
    }

    public boolean hasPlayer(@NotNull Player p) {
        return ownsWorld(p.getWorld());
    }

    public @Nullable GamePhase getPhase() {
        return phase;
    }

    public void destroy() {
        teamDisplay.reset();
    }

    public Set<Player> getPlayersInWorld() {
        return worlds.getPlayers();
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return teamManager.getParticipants();
    }
}
