package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantLeftTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteCategory;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.gui.hud.BingoSettingsHUDGroup;
import io.github.steaf23.bingoreloaded.gui.hud.DisabledBingoSettingsHUDGroup;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.VoteMenu;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

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
    private final MenuBoard menuBoard;
    private final CountdownTimer playerCountTimer;

    private final BingoSettingsHUDGroup settingsHUD;

    private boolean playerCountTimerPaused = false;
    private boolean gameStarted = false;

    public PregameLobby(MenuBoard menuBoard, HUDRegistry hudRegistry, BingoSession session, BingoConfigurationData config) {
        this.menuBoard = menuBoard;
        this.session = session;
        this.votes = new HashMap<>();
        this.config = config;
        this.playerCountTimer = new CountdownTimer(config.getOptionValue(BingoOptions.PLAYER_WAIT_TIME), session);
        if (config.getOptionValue(BingoOptions.DISABLE_SCOREBOARD_SIDEBAR)) {
            this.settingsHUD = new DisabledBingoSettingsHUDGroup(hudRegistry);
        }
        else {
            this.settingsHUD = new BingoSettingsHUDGroup(hudRegistry);
        }

        playerCountTimer.addNotifier(time -> {
            settingsHUD.setStatus(BingoMessage.STARTING_STATUS.asPhrase(Component.text(String.valueOf(time))));
            if (time == 10) {
                BingoMessage.STARTING_STATUS.sendToAudience(session, Component.text(time).color(NamedTextColor.GOLD));
            }
            if (time == 0) {
                gameStarted = true;
                session.startGame();
            } else if (time <= 5) {
                BingoMessage.STARTING_STATUS.sendToAudience(session, Component.text(time).color(NamedTextColor.RED));
            }
        });
    }

    public void voteGamemode(String gamemode, HumanEntity player) {
        registerVote(VoteTicket.CATEGORY_GAMEMODE, gamemode, player);
    }

    public void voteCard(@NotNull String card, HumanEntity player) {
        registerVote(VoteTicket.CATEGORY_CARD, card, player);
    }

    public void voteKit(String kit, HumanEntity player) {
        registerVote(VoteTicket.CATEGORY_KIT, kit, player);
    }

    public void voteCardsize(String cardSize, HumanEntity player) {
        registerVote(VoteTicket.CATEGORY_CARDSIZE, cardSize, player);
    }

    public void registerVote(VoteCategory<?> category, @NotNull String value, HumanEntity player) {
        if (!config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM)) {
            ConsoleMessenger.warn("Players cannot vote because useVoteSystem is set to false in config.yml!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());
        if (value.equals(ticket.getVote(category))) {
            // player already voted for this
            return;
        }

        if (!ticket.addVote(category, value)) {
            ConsoleMessenger.error("Player cannot vote for " + category + " " + value);
            return;
        }
        votes.put(player.getUniqueId(), ticket);

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

    private void giveVoteItem(Player player) {
        player.getInventory().addItem(PlayerKit.VOTE_ITEM.buildItem(true));
    }

    private void giveTeamItem(Player player) {
        player.getInventory().addItem(PlayerKit.TEAM_ITEM.buildItem(false));
    }

    private void initializePlayer(Player player) {
        settingsHUD.addPlayer(player);
        player.getInventory().clear();

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

        BingoReloaded.scheduleTask((t) -> {
            if (gameStarted) {
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (session.hasPlayer(p)) {
                    initializePlayer(p.getPlayer());
                }
            }

            // start a new timer in a task since the session will still assume the game is not in the lobby phase
            startPlayerCountTimerIfMinCountReached();
        }, 10);


    }

    @Override
    public void end() {
        playerCountTimer.stop();
        settingsHUD.removeAllPlayers();
    }

    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        initializePlayer(event.getPlayer());
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        settingsHUD.removePlayer(event.getPlayer());
        session.teamManager.removeMemberFromTeam(session.teamManager.getPlayerAsParticipant(event.getPlayer()));
    }

    @Override
    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event) {
        settingsHUD.updateSettings(event.getNewSettings(), config);
    }

    @Override
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR)
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (PlayerKit.VOTE_ITEM.isCompareKeyEqual(event.getItem())) {
            event.setCancelled(true);
            VoteMenu menu = new VoteMenu(menuBoard, config.getOptionValue(BingoOptions.VOTE_LIST), this);
            menu.open(event.getPlayer());
        } else if (PlayerKit.TEAM_ITEM.isCompareKeyEqual(event.getItem())) {
            event.setCancelled(true);
            TeamSelectionMenu teamSelection = new TeamSelectionMenu(menuBoard, session);
            teamSelection.open(event.getPlayer());
        }
    }

    @Override
    public void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {
        if (event.getParticipant() != null) {
            event.getParticipant().sessionPlayer().ifPresent(settingsHUD::addPlayer);
        }
        settingsHUD.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text(session.teamManager.getParticipantCount())));

        if (playerCountTimer.isRunning() && playerCountTimer.getTime() > 10) {
            event.getParticipant().sessionPlayer().ifPresent(p -> {
                BingoMessage.STARTING_STATUS.sendToAudience(p,
                        Component.text(playerCountTimer.getTime()).color(NamedTextColor.GOLD));
            });
        }

        startPlayerCountTimerIfMinCountReached();
    }

    @Override
    public void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {
        int playerCount = session.teamManager.getParticipantCount();

        if (playerCount == 0) {
            settingsHUD.setStatus(BingoMessage.WAIT_STATUS.asPhrase());
        } else {
            settingsHUD.setStatus(BingoMessage.PLAYER_STATUS.asPhrase(Component.text("" + playerCount)));
        }

        // Schedule check in the future since a player can switch teams where they will briefly leave the team
        // and lower the participant count to possibly stop the timer.
        BingoReloaded.scheduleTask(t -> {
            if (session.teamManager.getParticipantCount() < config.getOptionValue(BingoOptions.MINIMUM_PLAYER_COUNT) && playerCountTimer.isRunning()) {
                playerCountTimer.stop();
            }
        });
    }

    public void handlePlayerRespawn(final PlayerRespawnEvent event) {
        initializePlayer(event.getPlayer());
    }

    public Collection<VoteTicket> getAllVotes() {
        List<VoteTicket> result = new ArrayList<>();
        for (UUID playerId : votes.keySet()) {
            result.add(votes.get(playerId));
        }
        return result;
    }
}
