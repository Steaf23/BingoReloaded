package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gui.VoteMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.settings.SettingsPreviewBoard;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

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
    }

    private final BingoSession session;
    private final SettingsPreviewBoard settingsBoard;
    private final Map<UUID, VoteTicket> votes;
    private final PlayerData playerData;
    private final ConfigData config;
    private final MenuManager menuManager;

    public PregameLobby(MenuManager menuManager, BingoSession session, ConfigData config)
    {
        this.menuManager = menuManager;
        this.session = session;
        this.settingsBoard = new SettingsPreviewBoard();
        settingsBoard.showSettings(session.settingsBuilder.view());
        this.votes = new HashMap<>();
        this.playerData = new PlayerData();
        this.config = config;
    }

    public void voteGamemode(String gamemode, HumanEntity player)
    {
        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!gamemode.equals(ticket.gamemode))
        {
            ticket.gamemode = gamemode;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public void voteCard(String card, HumanEntity player)
    {
        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!card.equals(ticket.card))
        {
            ticket.card = card;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public void voteKit(String kit, HumanEntity player)
    {
        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!kit.equals(ticket.kit))
        {
            ticket.kit = kit;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public VoteTicket getVoteResult()
    {
        VoteTicket outcome = new VoteTicket();

        Map<String, Integer> gamemodes = new HashMap<>();
        Map<String, Integer> kits = new HashMap<>();
        Map<String, Integer> cards = new HashMap<>();

        for (UUID player : votes.keySet())
        {
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

    private String getKeyWithHighestValue(Map<String, Integer> values)
    {
        String recordKey = "";
        for (var k : values.keySet())
        {
            if (recordKey.isEmpty() || values.get(k) > values.get(recordKey))
            {
                recordKey = k;
            }
        }
        return recordKey;
    }

    private void giveVoteItem(Player player)
    {
        player.getInventory().setItem(1, PlayerKit.VOTE_ITEM);
    }

    private void giveTeamItem(Player player)
    {
        player.getInventory().setItem(0, PlayerKit.TEAM_ITEM);
    }

    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event)
    {
        settingsBoard.applyToPlayer(event.getPlayer());
        playerData.savePlayer(event.getPlayer(), false);
        event.getPlayer().getInventory().clear();
        giveVoteItem(event.getPlayer());
        giveTeamItem(event.getPlayer());
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event)
    {
        settingsBoard.clearPlayerBoard(event.getPlayer());
        playerData.loadPlayer(event.getPlayer());
    }

    @Override
    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        settingsBoard.handleSettingsUpdated(event);
    }

    @Override
    public void handlePlayerInteract(PlayerInteractEvent event)
    {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR)
            return;

        MenuItem item = new MenuItem(event.getItem());

        if (item.getCompareKey().equals("vote"))
        {
            event.setCancelled(true);
            VoteMenu menu = new VoteMenu(menuManager, config.voteList, this);
            menu.open(event.getPlayer());
        }
        else if (item.getCompareKey().equals("team"))
        {
            event.setCancelled(true);
            session.teamManager.openTeamSelector(menuManager, event.getPlayer());
        }
    }
}
