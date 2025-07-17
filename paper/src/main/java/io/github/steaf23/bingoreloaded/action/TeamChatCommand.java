package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TeamChatCommand extends ActionTree implements Listener {

	private final List<BingoPlayer> enabledPlayers;
	private final Function<PlayerHandle, @Nullable BingoSession> sessionResolver;

	public TeamChatCommand(Function<PlayerHandle, @Nullable BingoSession> sessionResolver) {
		super("btc", List.of("bingo.player"));
		this.enabledPlayers = new ArrayList<>();
		this.sessionResolver = sessionResolver;

		setAction((args) -> {
			if (!(getLastUser() instanceof PlayerHandle handle)) {
				return false;
			}

			BingoSession session = getSession(handle);
			if (session == null)
				return false;

			TeamManager teamManager = session.teamManager;
			BingoParticipant participant = teamManager.getPlayerAsParticipant(handle);

			if (!(participant instanceof BingoPlayer player))
				return false;

			if (!teamManager.getParticipants().contains(player)) {
				BingoMessage.NO_CHAT.sendToAudience(player, NamedTextColor.RED);
				return false;
			}

			if (enabledPlayers.contains(player)) {
				enabledPlayers.remove(player);
				BingoMessage.CHAT_OFF.sendToAudience(player, NamedTextColor.GREEN, Component.text("/btc").color(NamedTextColor.GRAY));
			} else {
				enabledPlayers.add(player);
				BingoMessage.CHAT_ON.sendToAudience(player, NamedTextColor.GREEN, Component.text("/btc").color(NamedTextColor.GRAY));
			}

			return true;
		});
	}

	private @Nullable BingoSession getSession(PlayerHandle player) {
		return sessionResolver.apply(player);
	}

	@EventHandler
	public void onPlayerSendMessage(final AsyncChatEvent event) {
		PlayerHandle handle = new PlayerHandlePaper(event.getPlayer());
		BingoSession session = getSession(handle);
		if (session == null)
			return;

		TeamManager teamManager = session.teamManager;

		BingoParticipant player = teamManager.getPlayerAsParticipant(handle);
		if (!(player instanceof BingoPlayer) || !enabledPlayers.contains(player)) return;

		BingoTeam team = player.getTeam();
		if (team == null) return;

		sendMessage(team, handle, event.message());

		event.setCancelled(true);
	}

	public void sendMessage(BingoTeam team, PlayerHandle player, Component message) {
		team.sendMessage(Component.text()
				.append(team.getPrefix())
				.append(Component.text("<").append(player.displayName()).append(Component.text("> ")))
				.append(message)
				.build());
	}
}