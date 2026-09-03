package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoClientManager;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.protocol.data.BingoCard;
import io.github.steaf23.bingoreloaded.protocol.data.BingoGamemode;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.TaskSlot;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloaded.protocol.payload.PayloadDefinition;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.key.Key;
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
import java.util.Optional;
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
		messenger.registerIncomingPluginChannel(plugin, BingoReloadedPayloads.CLIENT_HELLO.key().asString(), (channel, player, buf) -> {
			if (!channel.equals(BingoReloadedPayloads.CLIENT_HELLO.key().asString())) return;

			connectedPlayers.add(player.getUniqueId());
			ConsoleMessenger.log("Player " + player.getName() + " connected using the companion mod");

			PlayerHandle handle = new PlayerHandlePaper(bingo.getGameManager().getServer(), player);
			BingoSession session = bingo.getGameManager().getSessionOfPlayer(handle);
			if (session == null) {
				return;
			}

			BingoParticipant participant = session.teamManager.getPlayerAsParticipant(handle);
			if (participant == null) {
				return;
			}

			bingo.getGameManager().getServer().taskScheduler().runTask(20, t -> {
				updateCard(handle, participant.getCard().orElse(null));
			});
		});

		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.UPDATE_CARD.key().asString());
		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.key().toString());
		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.OPEN_CREATOR.key().toString());
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

		if (card == null) {
			sendMessage(((PlayerHandlePaper) player).handle(), BingoReloadedPayloads.UPDATE_CARD, Optional.empty());
		} else {
			sendMessage(((PlayerHandlePaper) player).handle(), BingoReloadedPayloads.UPDATE_CARD, Optional.of(new BingoCard(
					BingoGamemode.fromIdentifier(Key.key("bingoreloaded", "gamemode/" + card.getMode().configName()), false),
					card.size.size,
					card.getTasks().stream()
							.map(GameTask::asProtocolTask)
							.toList())));
		}
	}

	@Override
	public void updateHotswapContext(PlayerHandle player, @NotNull List<TaskSlot> holders) {
		sendMessage(((PlayerHandlePaper)player).handle(), BingoReloadedPayloads.UPDATE_HOTSWAP_CARD, holders);
	}

	@Override
	public void playerLeavesServer(PlayerHandle player) {
		connectedPlayers.remove(player.uniqueId());
	}

	@Override
	public void openCreator(PlayerHandle player, @NotNull CreatorTaskSupplier tasks) {
		sendMessage(((PlayerHandlePaper)player).handle(), BingoReloadedPayloads.OPEN_CREATOR, tasks);
	}

	private <Data> void sendMessage(Player player, PayloadDefinition<Data> definition, Data data) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(stream);

		try {
			definition.codec().encode(outputStream, data);
			byte[] bytes = stream.toByteArray();
			player.sendPluginMessage(plugin, definition.key().asString(), bytes);
		} catch (IOException ignored) {
		}
	}
}
