package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoInteraction;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteCategory;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.PlayerInput;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.menu.BingoSettingsInfoMenu;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
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
	private final BingoReloadedRuntime runtime;

	private final BingoSettingsInfoMenu infoMenu;

    private boolean playerCountTimerPaused = false;
    private boolean gameStarted = false;

    public PregameLobby(BingoSession session, BingoConfigurationData config) {
        this.session = session;
		this.runtime = session.getGameManager().getRuntime();
		this.votes = new HashMap<>();
        this.config = config;
        this.playerCountTimer = new CountdownTimer(session.getOverworld(), config.getOptionValue(BingoOptions.PLAYER_WAIT_TIME), this::onCountdownTimerFinished);

		this.infoMenu = new BingoSettingsInfoMenu();

        playerCountTimer.addNotifier(this::updateCounterVisual);
    }

    private void updateCounterVisual(long time) {
        infoMenu.setStatus(BingoMessage.STARTING_STATUS.asPhrase(Component.text(String.valueOf(time))));
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

    private void initializePlayer(PlayerHandle player) {
        runtime.settingsDisplay().addPlayer(player);
        player.clearInventory();

        runtime.playerJoinedLobby(session, player);
    }

    public void pausePlayerCountTimer() {
        playerCountTimerPaused = true;
        playerCountTimer.stop();
        infoMenu.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
		runtime.settingsDisplay().update(infoMenu);
    }

    public void resumePlayerCountTimer() {
        playerCountTimerPaused = false;

        int playerCount = session.teamManager.getParticipantCount();
        if (playerCount == 0) {
            infoMenu.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            infoMenu.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(playerCount)));
        }
		runtime.settingsDisplay().update(infoMenu);

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

        infoMenu.updateSettings(session.settingsBuilder.view(), config);
        if (playerCount == 0) {
            infoMenu.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            infoMenu.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(playerCount)));
        }
		runtime.settingsDisplay().update(infoMenu);

        session.getGameManager().getPlatform().runTask(session.getOverworld().uniqueId(), 10L, (t) -> {
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
        runtime.settingsDisplay().clearPlayers();
    }

    @Override
    public void handlePlayerJoinedSessionWorld(PlayerHandle player) {
        initializePlayer(player);
    }

    @Override
    public void handlePlayerLeftSessionWorld(PlayerHandle player) {
		runtime.settingsDisplay().removePlayer(player);
        session.teamManager.removeMemberFromTeam(session.teamManager.getPlayerAsParticipant(player));
    }

    @Override
    public void handleSettingsUpdated(BingoSettings newSettings) {
        infoMenu.updateSettings(newSettings, config);
		runtime.settingsDisplay().update(infoMenu);
    }

    @Override
    public EventResult<?> handlePlayerInteracted(PlayerHandle player, @Nullable StackHandle stack, PlayerInput action) {
		if (stack == null || stack.type().isAir())
            return EventResult.IGNORE;

        if (!action.rightClick()) {
            return EventResult.IGNORE;
        }

        if (runtime.canItemBeUsedForInteraction(session, player, BingoInteraction.START_VOTE, stack, action)) {
            runtime.openVoteMenu(player, this);
            return EventResult.CONSUME;
        } else if (runtime.canItemBeUsedForInteraction(session, player, BingoInteraction.SELECT_TEAM, stack, action)) {
            runtime.openTeamSelector(player, session);
            return EventResult.CONSUME;
        }

        return EventResult.IGNORE;
    }

    @Override
    public void handleParticipantJoinedTeam(final BingoEvents.TeamParticipantEvent event) {
        infoMenu.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(session.teamManager.getParticipantCount())));
		runtime.settingsDisplay().update(infoMenu);

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
            infoMenu.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            infoMenu.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text("" + playerCount)));
        }
		runtime.settingsDisplay().update(infoMenu);

        // Schedule check in the future since a player can switch teams where they will briefly leave the team
        // and lower the participant count to possibly stop the timer.
        session.getGameManager().getPlatform().runTask(session.getOverworld().uniqueId(), t -> {
            if (session.teamManager.getParticipantCount() < config.getOptionValue(BingoOptions.MINIMUM_PLAYER_COUNT) && playerCountTimer.isRunning()) {
                playerCountTimer.stop();
            }
        });
    }

	@Override
	public boolean canViewCard() {
		return false;
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
