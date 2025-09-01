package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.api.network.packets.TaskCardWriter;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.apache.commons.lang3.function.FailableConsumer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PaperClientManager implements BingoClientManager {

	private final JavaPlugin plugin;
	private final Set<UUID> connectedPlayers = new HashSet<>();

	public PaperClientManager(JavaPlugin plugin) {
		this.plugin = plugin;

		Messenger messenger = plugin.getServer().getMessenger();
		messenger.registerIncomingPluginChannel(plugin, "bingoreloaded:hello", (channel, player, buf) -> {
			if (!channel.equals("bingoreloaded:hello")) return;

			connectedPlayers.add(player.getUniqueId());
			ConsoleMessenger.log("Player " + player.getName() + " said hi!");
		});

		messenger.registerOutgoingPluginChannel(plugin, "bingoreloaded:update_card");
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
