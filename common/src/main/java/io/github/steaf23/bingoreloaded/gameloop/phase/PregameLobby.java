package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.gui.hud.DisabledBingoSettingsHUDGroup;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteCategory;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.gui.hud.BingoSettingsHUDGroup;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PregameLobby implements GamePhase
{
    private final BingoSession session;
    private final Map<UUID, VoteTicket> votes;
    private final BingoConfigurationData config;
    private final CountdownTimer playerCountTimer;

    private final BingoSettingsHUDGroup settingsHUD;

    private boolean playerCountTimerPaused = false;
    private boolean gameStarted = false;

    public PregameLobby(BingoSession session, BingoConfigurationData config) {
        this.session = session;
		this.votes = new HashMap<>();
        this.config = config;
        this.playerCountTimer = new CountdownTimer(config.getOptionValue(BingoOptions.PLAYER_WAIT_TIME), this::onCountdownTimerFinished);

        if (config.getOptionValue(BingoOptions.DISABLE_SCOREBOARD_SIDEBAR)) {
            this.settingsHUD = new DisabledBingoSettingsHUDGroup(session);
        }
        else {
            this.settingsHUD = new BingoSettingsHUDGroup(session);
        }

        playerCountTimer.addNotifier(this::updateCounterVisual);
    }

    private void updateCounterVisual(long time) {
        settingsHUD.setStatus(BingoMessage.STARTING_STATUS.asPhrase(Component.text(String.valueOf(time))));
        if (time == 10) {
            BingoMessage.STARTING_STATUS.sendToAudience(session, Component.text(time).color(NamedTextColor.GOLD));
        } else if (time <= 5) {
            BingoMessage.STARTING_STATUS.sendToAudience(session, Component.text(time).color(NamedTextColor.RED));
        }
    }

    private void onCountdownTimerFinished() {
        gameStarted = true;
        session.startGame();
    }

    public void voteGamemode(String gamemode, PlayerHandle player) {
        registerVote(VoteTicket.CATEGORY_GAMEMODE, gamemode, player);
    }

    public void voteCard(@NotNull String card, PlayerHandle player) {
        registerVote(VoteTicket.CATEGORY_CARD, card, player);
    }

    public void voteKit(String kit, PlayerHandle player) {
        registerVote(VoteTicket.CATEGORY_KIT, kit, player);
    }

    public void voteCardsize(String cardSize, PlayerHandle player) {
        registerVote(VoteTicket.CATEGORY_CARDSIZE, cardSize, player);
    }

    public void registerVote(VoteCategory<?> category, @NotNull String value, PlayerHandle player) {
        if (!config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM)) {
            ConsoleMessenger.warn("Players cannot vote because useVoteSystem is set to false in config.yml!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.uniqueId(), new VoteTicket());
        if (value.equals(ticket.getVote(category))) {
            // player already voted for this
            return;
        }

        if (!ticket.addVote(category, value)) {
            ConsoleMessenger.error("Player cannot vote for " + category + " " + value);
            return;
        }
        votes.put(player.uniqueId(), ticket);

        int count = 0;
        for (VoteTicket t : votes.values()) {
            if (value.equals(t.getVote(category))) {
                count++;
            }
        }
        BingoMessage.VOTE_COUNT.sendToAudience(session,
                Component.text(count).color(NamedTextColor.GOLD),
                category.asComponent(),
                category.getValueComponent(value));
    }

    private void giveVoteItem(PlayerHandle player) {
        player.inventory().addItem(PlayerKit.VOTE_ITEM.buildItem());
    }

    private void giveTeamItem(PlayerHandle player) {
        player.inventory().addItem(PlayerKit.TEAM_ITEM.buildItem());
    }

    private void initializePlayer(PlayerHandle player) {
        settingsHUD.forceUpdate();
        player.clearInventory();

        if (config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM) &&
                !config.getOptionValue(BingoOptions.VOTE_USING_COMMANDS_ONLY) &&
                !config.getOptionValue(BingoOptions.VOTE_LIST).isEmpty()) {
            giveVoteItem(player);
        }
        if (!config.getOptionValue(BingoOptions.SELECT_TEAMS_USING_COMMANDS_ONLY)) {
            giveTeamItem(player);
        }
    }

    public void pausePlayerCountTimer() {
        playerCountTimerPaused = true;
        playerCountTimer.stop();
        settingsHUD.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
    }

    public void resumePlayerCountTimer() {
        playerCountTimerPaused = false;

        int playerCount = session.teamManager.getParticipantCount();
        if (playerCount == 0) {
            settingsHUD.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            settingsHUD.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(playerCount)));
        }

        startPlayerCountTimerIfMinCountReached();
    }

    public void playerCountTimerTogglePause() {
        if (playerCountTimerPaused) {
            resumePlayerCountTimer();
        }
        else {
            pausePlayerCountTimer();
        }
    }

    private void startPlayerCountTimerIfMinCountReached() {
        int minimumPlayerCount = config.getOptionValue(BingoOptions.MINIMUM_PLAYER_COUNT);
        if (minimumPlayerCount == 0 || gameStarted) {
            return;
        }

        if (session.teamManager.getParticipantCount() < minimumPlayerCount) {
            return;
        }

        if (playerCountTimer.isRunning() || playerCountTimerPaused) {
            return;
        }

        playerCountTimer.start();
        if (playerCountTimer.getTime() > 10) {
            BingoMessage.STARTING_STATUS.sendToAudience(session,
                    Component.text(config.getOptionValue(BingoOptions.PLAYER_WAIT_TIME)).color(NamedTextColor.GOLD));
        }
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    @Override
    public void setup() {
        int playerCount = session.teamManager.getParticipantCount();

        settingsHUD.updateSettings(session.settingsBuilder.view(), config);
        if (playerCount == 0) {
            settingsHUD.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            settingsHUD.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(playerCount)));
        }

        session.getGameManager().getPlatform().runTask(10L, (t) -> {
            if (gameStarted) {
                return;
            }

            session.getPlayersInWorld().stream()
                    .filter(session::hasPlayer)
                    .forEach(this::initializePlayer);

            // start a new timer in a task since the session will still assume the game is not in the lobby phase
            startPlayerCountTimerIfMinCountReached();
        });


    }

    @Override
    public void end() {
        playerCountTimer.stop();
        //FIXME: REFACTOR handle this?
//        settingsHUD.removeAllPlayers();
    }

    @Override
    public void handlePlayerJoinedSessionWorld(BingoEvents.PlayerSessionEvent event) {
        initializePlayer(event.player());
    }

    @Override
    public void handlePlayerLeftSessionWorld(BingoEvents.PlayerSessionEvent event) {
        //FIXME: REFACTOR remove players from scoreboard when they leave (dont leave them hanging with outdated scoreboards...
//        settingsHUD.removePlayer(event.player());
        session.teamManager.removeMemberFromTeam(session.teamManager.getPlayerAsParticipant(event.player()));
    }

    @Override
    public void handleSettingsUpdated(BingoSettings newSettings) {
        settingsHUD.updateSettings(newSettings, config);
    }

    @Override
    public EventResult<?> handlePlayerInteracted(PlayerHandle player, @Nullable StackHandle stack, InteractAction action) {
		if (stack == null || stack.type().isAir())
            return EventResult.PASS;

        if (!action.rightClick()) {
            return EventResult.PASS;
        }

        if (PlayerKit.VOTE_ITEM.isCompareKeyEqual(stack)) {
            BingoReloaded.runtime().openVoteMenu(player, this);
            return EventResult.CANCEL;
        } else if (PlayerKit.TEAM_ITEM.isCompareKeyEqual(stack)) {
            BingoReloaded.runtime().openTeamSelector(player, session);
            return EventResult.CANCEL;
        }

        return EventResult.PASS;
    }

    @Override
    public void handleParticipantJoinedTeam(final BingoEvents.TeamParticipantEvent event) {
        settingsHUD.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(session.teamManager.getParticipantCount())));

        if (playerCountTimer.isRunning() && playerCountTimer.getTime() > 10) {
            event.participant().sessionPlayer().ifPresent(p -> {
                BingoMessage.STARTING_STATUS.sendToAudience(p,
                        Component.text(playerCountTimer.getTime()).color(NamedTextColor.GOLD));
            });
        }

        startPlayerCountTimerIfMinCountReached();
    }

    @Override
    public void handleParticipantLeftTeam(final BingoEvents.TeamParticipantEvent event) {
        int playerCount = session.teamManager.getParticipantCount();

        if (playerCount == 0) {
            settingsHUD.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            settingsHUD.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text("" + playerCount)));
        }

        // Schedule check in the future since a player can switch teams where they will briefly leave the team
        // and lower the participant count to possibly stop the timer.
        session.getGameManager().getPlatform().runTask(t -> {
            if (session.teamManager.getParticipantCount() < config.getOptionValue(BingoOptions.MINIMUM_PLAYER_COUNT) && playerCountTimer.isRunning()) {
                playerCountTimer.stop();
            }
        });
    }

    public void handlePlayerRespawn(final PlayerHandle player) {
        initializePlayer(player);
    }

    public Collection<VoteTicket> getAllVotes() {
        List<VoteTicket> result = new ArrayList<>();
        for (UUID playerId : votes.keySet()) {
            result.add(votes.get(playerId));
        }
        return result;
    }
}
