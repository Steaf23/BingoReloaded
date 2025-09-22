package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.network.packets.HotswapTasksWriter;
import io.github.steaf23.bingoreloaded.api.network.packets.TaskCardWriter;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import org.apache.commons.lang3.function.FailableConsumer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PaperClientManager implements BingoClientManager {

	private final JavaPlugin plugin;
	private final Set<UUID> connectedPlayers = new HashSet<>();

	private final BingoReloaded bingo;

	public PaperClientManager(JavaPlugin plugin, BingoReloaded bingo) {
		this.plugin = plugin;
		this.bingo = bingo;

		Messenger messenger = plugin.getServer().getMessenger();
		messenger.registerIncomingPluginChannel(plugin, "bingoreloaded:hello", (channel, player, buf) -> {
			if (!channel.equals("bingoreloaded:hello")) return;

			connectedPlayers.add(player.getUniqueId());
			ConsoleMessenger.log("Player " + player.getName() + " connected using the companion mod");

			PlayerHandle handle = new PlayerHandlePaper(player);
			BingoSession session = bingo.getGameManager().getSessionOfPlayer(handle);
			if (session == null) {
				return;
			}

			BingoParticipant participant = session.teamManager.getPlayerAsParticipant(handle);
			if (participant == null) {
				return;
			}

			bingo.getGameManager().getPlatform().runTask(20, t -> {
				updateCard(handle, participant.getCard().orElse(null));
			});
		});

		messenger.registerOutgoingPluginChannel(plugin, "bingoreloaded:update_card");
		messenger.registerOutgoingPluginChannel(plugin, "bingoreloaded:hotswap_tasks");
	}

	@Override
	public boolean playerHasClient(PlayerHandle player) {
		return connectedPlayers.contains(player.uniqueId());
	}

	@Override
	public void updateCard(PlayerHandle player, @Nullable TaskCard card) {
		if (!playerHasClient(player)) {
			return;
		}

		sendMessage(((PlayerHandlePaper) player).handle(), BingoReloadedPackets.SERVER_UPDATE_CARD.id(), stream -> {
			TaskCardWriter.WRITER.write(card, stream);
		});
	}

	@Override
	public void updateHotswapContext(PlayerHandle player, @NotNull List<HotswapTaskHolder> holders) {
		sendMessage(((PlayerHandlePaper)player).handle(), BingoReloadedPackets.SERVER_HOTSWAP_TASKS.id(), stream -> {
			HotswapTasksWriter.WRITER.write(holders, stream);
		});
	}

	@Override
	public void playerLeavesServer(PlayerHandle player) {
		connectedPlayers.remove(player.uniqueId());
	}

	private void sendMessage(Player player, String channel, FailableConsumer<DataOutputStream, IOException> writer) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(stream);

		try {
			writer.accept(data);
			byte[] bytes = stream.toByteArray();
			player.sendPluginMessage(plugin, channel, bytes);
		} catch (IOException ignored) {
		}
	}
}
