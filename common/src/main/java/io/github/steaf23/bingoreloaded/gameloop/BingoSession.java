package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoLobby;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.GamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PostGamePhase;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteCategory;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.EventResults;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.world.BlockHelper;
import io.github.steaf23.bingoreloaded.menu.BingoGameInfoMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    public final BingoGameInfoMenu gameInfoMenu;
    public final TeamManager teamManager;
    private final GameManager gameManager;
    private final BingoConfigurationData config;
    private final TeamDisplay teamDisplay;

    // A bingo session controls 1 group of worlds
    private final WorldGroup worlds;
    private GamePhase phase;

    public BingoSession(GameManager gameManager, @NotNull WorldGroup worlds, BingoConfigurationData config) {
		this.gameManager = gameManager;
        this.worlds = worlds;
        this.config = config;

        boolean showPlayerInScoreboard = config.getOptionValue(BingoOptions.SHOW_PLAYER_IN_SCOREBOARD);
		this.gameInfoMenu = new BingoGameInfoMenu(this, showPlayerInScoreboard);
        this.settingsBuilder = new BingoSettingsBuilder(this);
        if (config.getOptionValue(BingoOptions.SINGLE_PLAYER_TEAMS)) {
            this.teamManager = new SoloTeamManager(this);
        } else {
            this.teamManager = new BasicTeamManager(this);
        }

        this.teamDisplay = gameManager.getRuntime().createTeamDisplay(this);
        this.phase = null;

        //TODO: decide a better place for this command
//        BingoReloaded.getInstance().registerCommand("bingobot", new BotCommand(this));

        gameManager.getPlatform().runTask(10L, (t) -> {
            for (PlayerHandle p : PlatformResolver.get().getOnlinePlayers()) {
                if (hasPlayer(p)) {
                    addPlayer(p);
                }
            }
        });
        prepareNextGame();
    }

    public boolean isRunning() {
        return phase instanceof BingoGame;
    }

	public boolean canPlayersViewCard() {
		return phase.canViewCard();
	}

    public GamePhase phase() {
        return phase;
    }


	public boolean startGame() {
		return startGame(null);
	}

    public boolean startGame(@Nullable WorldPosition atPosition) {
        if (!(phase instanceof PregameLobby lobby)) {
            ConsoleMessenger.error("Cannot start a game on this world if it is not in the lobby phase!");
            return false;
        }

        BingoSettingsBuilder gameSettings = determineSettingsByVote(lobby);

        BingoCardData cardsData = new BingoCardData();
        BingoSettings settings = settingsBuilder.view();
        if (!cardsData.getCardNames().contains(settings.card())) {
            BingoMessage.NO_CARD.sendToAudience(this, NamedTextColor.RED, Component.text(settings.card()));
            return false;
        }

        if (teamManager.getParticipantCount() == 0) {
            ConsoleMessenger.log("Could not start bingo since no players have joined!", worlds.worldName());
            teamManager.reset();
            return false;
        }

        teamManager.setup();
        gameInfoMenu.updateTeamScores();
		getGameManager().getRuntime().gameDisplay().update(gameInfoMenu);

        // First make sure the previous phase (PregameLobby) is ended.
        phase.end();
        phase = new BingoGame(gameManager.getPlatform(), this, gameSettings == null ? settings : gameSettings.view(), config, this::onGameEnded, atPosition);
        phase.setup();

		return true;
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
        gameManager.prepareNextBingoGame(this);

        // When we came from the PostGamePhase we need to make sure to end it properly
        if (phase != null) {
            phase.end();
        }

        phase = new PregameLobby(this, config);
        phase.setup();

        getOverworld().players().forEach(p -> {
			getGameManager().getRuntime().getClientManager().updateCard(p, null);

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
    public void removeParticipant(@NotNull BingoParticipant player) {
        teamManager.removeMemberFromTeam(player);
    }

    public void onGameEnded() {
		int gameRestartTime = config.getOptionValue(BingoOptions.GAME_RESTART_TIME);

		if (config.getOptionValue(BingoOptions.TELEPORT_TO_LOBBY_AFTER_GAME)) {
			BingoLobby lobby = gameManager.getLobbyData().getCreatedLobby();
			if (lobby != null && lobby.spawnPosition() != null) {
				double delaySeconds = config.getOptionValue(BingoOptions.TELEPORT_TO_LOBBY_DELAY);
				int spread = config.getOptionValue(BingoOptions.TELEPORT_TO_LOBBY_SPREAD);
				delaySeconds = Math.min(delaySeconds, gameRestartTime);

				if (delaySeconds > 0.0) {
					gameManager.getPlatform().runTask((long) (delaySeconds * BingoReloaded.ONE_SECOND), t -> {
						getPlayersInWorld().forEach(p -> {
							WorldPosition pos = BlockHelper.getRandomPosWithinRange(lobby.spawnPosition(), spread, spread);
							p.teleportAsync(pos);
						});
					});
				} else {
					getPlayersInWorld().forEach(p -> {
						WorldPosition pos = BlockHelper.getRandomPosWithinRange(lobby.spawnPosition(), spread, spread);
						p.teleportAsync(pos);
					});
				}
			}
		}

		BingoOptions.ConfigGamemode gamemode = config.getOptionValue(BingoOptions.PLAYER_GAMEMODE_AFTER_GAME);

		PlayerGamemode mode = switch (gamemode) {
			case SURVIVAL -> PlayerGamemode.SURVIVAL;
			case SPECTATOR -> PlayerGamemode.SPECTATOR;
			case ADVENTURE -> PlayerGamemode.ADVENTURE;
			case CREATIVE -> PlayerGamemode.CREATIVE;
			default -> null;
		};
		if (mode != null) {
			getPlayersInWorld().forEach(p -> p.setGamemode(mode));
		}

		phase = new PostGamePhase(this, gameRestartTime);
        phase.setup();
    }

    public void onSettingUpdated(BingoSettings newSettings) {
        phase.handleSettingsUpdated(newSettings);
        teamManager.handleSettingsUpdated(newSettings);
    }

    public void addPlayer(PlayerHandle player) {
        onPlayerJoinedSessionWorld(player);

		// Teleport players always to the lobby if they haven't played before if the game is not running.
		if (teamManager.getPlayerAsParticipant(player) == null || !isRunning()) {
			BingoLobby lobby = gameManager.getLobbyData().getCreatedLobby();

			int spread = config.getOptionValue(BingoOptions.TELEPORT_TO_LOBBY_SPREAD);
			if (lobby != null && lobby.spawnPosition() != null) {
				player.teleportAsync(BlockHelper.getRandomPosWithinRange(lobby.spawnPosition(), spread, spread));
			}
		}
        BingoReloaded.sendResourcePack(player);
    }

    public void removePlayer(PlayerHandle player) {
        onPlayerLeftSessionWorld(player);
    }

    public EventResult<?> handlePlayerDroppedStack(PlayerHandle player, StackHandle stack) {
		if (PlayerKit.CARD_ITEM.isCompareKeyEqual(stack) ||
				PlayerKit.WAND_ITEM.isCompareKeyEqual(stack) ||
				PlayerKit.VOTE_ITEM.isCompareKeyEqual(stack) ||
				PlayerKit.TEAM_ITEM.isCompareKeyEqual(stack)) {
            return EventResult.CANCEL;
        }
        return EventResult.PASS;
	}

    public void onPlayerJoinedSessionWorld(PlayerHandle player) {
        gameManager.getPlatform().runTask(t -> {
            teamManager.handlePlayerJoinedSessionWorld(player);
            phase.handlePlayerJoinedSessionWorld(player);

            if (isRunning()) {
                getGameManager().getRuntime().gameDisplay().addPlayer(player);
            }
            teamDisplay.update();
        });
    }

    public void onPlayerLeftSessionWorld(PlayerHandle player) {
        // Clear player's teams before anything else.
        // This is because they might join another bingo as a result of leaving this one, so we have to remove the player's team display at this moment
        teamDisplay.clearTeamsForPlayer(player);
		getGameManager().getRuntime().getClientManager().updateCard(player, null);


        getGameManager().getPlatform().runTask(t -> {
            teamManager.handlePlayerLeftSessionWorld(player);
            phase.handlePlayerLeftSessionWorld(player);

            player.clearAllEffects();

            if (isRunning()) {
                BingoMessage.LEAVE.sendToAudience(player);
            }

			getGameManager().getRuntime().gameDisplay().removePlayer(player);
            teamDisplay.update();

            if (!config.getOptionValue(BingoOptions.END_GAME_WITHOUT_TEAMS)) {
                return;
            }

            if (teamManager.getActiveTeams().getOnlineTeamCount() <= 1 || teamManager.getActiveTeams().getAllOnlineParticipants().isEmpty()) {
                if (isRunning()) {
                    ConsoleMessenger.log(Component.text("Ending game because there is no competition anymore.").color(NamedTextColor.LIGHT_PURPLE), Component.text(worlds.worldName()));
                }
                endGame();
            }
        });
    }

    public void onParticipantJoinedTeam(final BingoEvents.TeamParticipantEvent event) {
        phase.handleParticipantJoinedTeam(event);
        teamDisplay.update();
    }

    public void onParticipantLeftTeam(final BingoEvents.TeamParticipantEvent event) {
        phase.handleParticipantLeftTeam(event);
        teamDisplay.update();
    }

    public EventResult<EventResults.PlayerMoveResult> handlePlayerPortalEvent(final PlayerHandle player, final WorldPosition fromPos, @NotNull WorldPosition toPos) {
        WorldHandle origin = fromPos.world();
        WorldHandle target = toPos.world();

        WorldPosition targetLocation = new WorldPosition(toPos);
        if (origin.uniqueId().equals(worlds.overworldId())) {
            // coming from the OW we can go to either the nether or the end
            if (target.dimension() == DimensionType.NETHER) {
                // Nether
                targetLocation.setWorld(worlds.getNetherWorld());
            } else if (target.dimension() == DimensionType.THE_END) {
                // The End
                targetLocation.setWorld(worlds.getEndWorld());
            } else {
                ConsoleMessenger.bug("Could not catch player going through portal", this);
            }
        } else if (origin.uniqueId().equals(worlds.netherId())) {
            // coming from the nether we can only go to the OW
            targetLocation.setWorld(worlds.getOverworld());
        } else if (origin.uniqueId().equals(worlds.endId())) {
            // coming from the end we can go to either the overworld or to the end spawn from an outer portal.
            if (target.dimension() == DimensionType.OVERWORLD) {
                // Overworld
                targetLocation.setWorld(worlds.getOverworld());
            } else if (target.dimension() == DimensionType.THE_END) {
                // The End
                targetLocation.setWorld(worlds.getEndWorld());
            } else {
                ConsoleMessenger.bug("Could not catch player going through portal", this);
            }
        }

        return EventResults.playerMoveResult(false, true, targetLocation);
    }

    public EventResult<?> handlePlayerBlockBreak(PlayerHandle player, WorldPosition position, ItemType block) {
        if (!isRunning() && config.getOptionValue(BingoOptions.PREVENT_PLAYER_GRIEFING) && !player.hasPermission("bingo.admin")) {
            BingoMessage.NO_GRIEFING.sendToAudience(player);
            return EventResult.CANCEL;
        }
        return EventResult.PASS;
    }

    public EventResult<?> handlePlayerBlockPlace(PlayerHandle player, WorldPosition position, ItemType block) {
        if (!isRunning() && config.getOptionValue(BingoOptions.PREVENT_PLAYER_GRIEFING) && !player.hasPermission("bingo.admin")) {
            BingoMessage.NO_GRIEFING.sendToAudience(player);
            return EventResult.CANCEL;
        }
        return EventResult.PASS;
    }

    public WorldHandle getOverworld() {
        return worlds.getOverworld();
    }

    public boolean ownsWorld(@NotNull WorldHandle world) {
        return worlds.hasWorld(world.uniqueId());
    }

    public BingoConfigurationData getPluginConfig() {
        return config;
    }

    private BingoSettingsBuilder determineSettingsByVote(PregameLobby lobby) {
        if (!config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM)) {
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

    public boolean hasPlayer(@NotNull PlayerHandle p) {
        return ownsWorld(p.world());
    }

    public @Nullable GamePhase getPhase() {
        return phase;
    }

    public void destroy() {
        teamDisplay.reset();
    }

    public Set<PlayerHandle> getPlayersInWorld() {
        return worlds.getPlayers();
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return teamManager.getParticipants();
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
