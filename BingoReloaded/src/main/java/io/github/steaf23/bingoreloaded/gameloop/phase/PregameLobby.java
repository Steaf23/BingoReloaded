package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.hud.BingoSettingsHUDGroup;
import io.github.steaf23.bingoreloaded.gui.hud.DisabledBingoSettingsHUDGroup;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.VoteMenu;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PregameLobby implements GamePhase
{
    // Each player can cast a single vote for all categories, To keep track of this a VoteTicket will be made for every player that votes on something
    public static class VoteTicket
    {
        public String gamemode = "";
        public String kit = "";
        public String card = "";

        public boolean isEmpty() {
            return gamemode.isEmpty() && kit.isEmpty() && card.isEmpty();
        }
    }

    private final BingoSession session;
    private final Map<UUID, VoteTicket> votes;
    private final ConfigData config;
    private final MenuBoard menuBoard;
    private final CountdownTimer playerCountTimer;

    private final BingoSettingsHUDGroup settingsHUD;

    private boolean playerCountTimerPaused = false;
    private boolean gameStarted = false;

    public PregameLobby(MenuBoard menuBoard, HUDRegistry hudRegistry, BingoSession session, ConfigData config) {
        this.menuBoard = menuBoard;
        this.session = session;
        this.votes = new HashMap<>();
        this.config = config;
        this.playerCountTimer = new CountdownTimer(config.playerWaitTime, session);
        if (config.disableScoreboardSidebar) {
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
        if (!config.useVoteSystem) {
            ConsoleMessenger.warn("Players cannot vote because useVoteSystem is set to false in config.yml!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());
        if (gamemode.equals(ticket.gamemode)) {
            return;
        }

        ticket.gamemode = gamemode;
        votes.put(player.getUniqueId(), ticket);

        int count = 0;
        for (VoteTicket t : votes.values()) {
            if (t.gamemode.equals(gamemode)) {
                count++;
            }
        }

        String[] tuple = gamemode.split("_");
        if (tuple.length != 2) {
            return;
        }
        //FIXME: use gamemode display name from config
        sendVoteCountMessage(count, BingoMessage.OPTIONS_GAMEMODE.asPhrase(), tuple[0] + " " + tuple[1] + "x" + tuple[1]);
    }

    public void voteCard(String card, HumanEntity player) {
        if (!config.useVoteSystem) {
            ConsoleMessenger.warn("Players cannot vote because useVoteSystem is set to false in config.yml!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());
        if (card.equals(ticket.card)) {
            return;
        }

        ticket.card = card;
        votes.put(player.getUniqueId(), ticket);

        int count = 0;
        for (VoteTicket t : votes.values()) {
            if (t.card.equals(card)) {
                count++;
            }
        }
        sendVoteCountMessage(count, BingoMessage.OPTIONS_CARD.asPhrase(), card);
    }

    public void voteKit(String kit, HumanEntity player) {
        if (!config.useVoteSystem) {
            ConsoleMessenger.warn("Players cannot vote because useVoteSystem is set to false in config.yml!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());
        if (kit.equals(ticket.kit)) {
            return;
        }

        ticket.kit = kit;
        votes.put(player.getUniqueId(), ticket);

        int count = 0;
        for (VoteTicket t : votes.values()) {
            if (t.kit.equals(kit)) {
                count++;
            }
        }
        //FIXME: use actual name
        sendVoteCountMessage(count, BingoMessage.OPTIONS_KIT.asPhrase(), PlayerKit.fromConfig(kit).configName);
    }

    public void sendVoteCountMessage(int count, Component category, String voteItem) {
        BingoMessage.VOTE_COUNT.sendToAudience(session,
                Component.text(count).color(NamedTextColor.GOLD),
                category,
                Component.text(voteItem));
    }

    public VoteTicket getVoteResult() {
        VoteTicket outcome = new VoteTicket();

        Map<String, Integer> gamemodes = new HashMap<>();
        Map<String, Integer> kits = new HashMap<>();
        Map<String, Integer> cards = new HashMap<>();

        for (UUID player : votes.keySet()) {
            VoteTicket ticket = votes.get(player);
            gamemodes.put(ticket.gamemode, gamemodes.getOrDefault(ticket.gamemode, 0) + 1);
            kits.put(ticket.kit, kits.getOrDefault(ticket.kit, 0) + 1);
            cards.put(ticket.card, cards.getOrDefault(ticket.card, 0) + 1);
        }

        outcome.gamemode = getKeyWithHighestValue(gamemodes);
        outcome.kit = getKeyWithHighestValue(kits);
        outcome.card = getKeyWithHighestValue(cards);

        return outcome;
    }

    private String getKeyWithHighestValue(Map<String, Integer> values) {
        String recordKey = "";
        for (var k : values.keySet()) {
            if (recordKey.isEmpty() || values.get(k) > values.get(recordKey)) {
                recordKey = k;
            }
        }
        return recordKey;
    }

    private void giveVoteItem(Player player) {
        player.getInventory().addItem(PlayerKit.VOTE_ITEM.buildItem(false));
    }

    private void giveTeamItem(Player player) {
        player.getInventory().addItem(PlayerKit.TEAM_ITEM.buildItem(false));
    }

    private void initializePlayer(Player player) {
        settingsHUD.addPlayer(player);
        player.getInventory().clear();

        if (config.useVoteSystem && !config.voteUsingCommandsOnly && !config.voteList.isEmpty()) {
            giveVoteItem(player);
        }
        if (!config.selectTeamsUsingCommandsOnly) {
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
        if (config.minimumPlayerCount == 0 || gameStarted) {
            return;
        }

        if (session.teamManager.getParticipantCount() < config.minimumPlayerCount) {
            return;
        }

        if (playerCountTimer.isRunning() || playerCountTimerPaused) {
            return;
        }

        playerCountTimer.start();
        if (playerCountTimer.getTime() > 10) {
            BingoMessage.STARTING_STATUS.sendToAudience(session,
                    Component.text(config.playerWaitTime).color(NamedTextColor.GOLD));
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
            VoteMenu menu = new VoteMenu(menuBoard, config.voteList, this);
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
            event.getParticipant().sessionPlayer().ifPresent(p -> settingsHUD.addPlayer(p));
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
            if (session.teamManager.getParticipantCount() < config.minimumPlayerCount && playerCountTimer.isRunning()) {
                playerCountTimer.stop();
            }
        });
    }
}
