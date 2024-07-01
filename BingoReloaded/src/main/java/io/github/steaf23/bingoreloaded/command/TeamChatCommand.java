package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TeamChatCommand implements Listener, TabExecutor
{
    private final List<BingoPlayer> enabledPlayers;
    private final Function<Player, BingoSession> sessionResolver;

    public TeamChatCommand(Function<Player, BingoSession> sessionResolver)
    {
        this.enabledPlayers = new ArrayList<>();
        this.sessionResolver = sessionResolver;
    }

    private BingoSession getSession(Player player)
    {
        return sessionResolver.apply(player);
    }

    @EventHandler
    public void onPlayerSendMessage(final AsyncChatEvent event)
    {
        BingoSession session = getSession(event.getPlayer());
        if (session == null)
            return;

        TeamManager teamManager = session.teamManager;

        BingoParticipant player = teamManager.getPlayerAsParticipant(event.getPlayer());
        if (!(player instanceof BingoPlayer) || !enabledPlayers.contains(player)) return;

        BingoTeam team = player.getTeam();
        if (team == null) return;

        sendMessage(team, event.getPlayer(), event.message());

        event.setCancelled(true);
    }

    public void sendMessage(BingoTeam team, Player player, Component message)
    {
        team.sendMessage(Component.text()
                .append(team.getPrefix())
                .append(Component.text("<" + player.displayName() + "> "))
                .append(message)
                .build());
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (commandSender instanceof Player p)
        {
            BingoSession session = getSession(p);
            if (session == null)
                return false;

            TeamManager teamManager = session.teamManager;
            BingoParticipant participant = teamManager.getPlayerAsParticipant(p);

            if (!(participant instanceof BingoPlayer player))
                return false;

            if (!teamManager.getParticipants().contains(player))
            {
                BingoMessage.NO_CHAT.sendToAudience(player, NamedTextColor.RED);
                return false;
            }

            if (enabledPlayers.contains(player))
            {
                enabledPlayers.remove(player);
                BingoMessage.CHAT_OFF.sendToAudience(player, NamedTextColor.GREEN, Component.text("/btc").color(NamedTextColor.GRAY));
            }
            else
            {
                enabledPlayers.add(player);
                BingoMessage.CHAT_ON.sendToAudience(player, NamedTextColor.GREEN, Component.text("/btc").color(NamedTextColor.GRAY));
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}