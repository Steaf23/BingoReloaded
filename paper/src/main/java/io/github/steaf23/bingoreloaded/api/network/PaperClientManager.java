package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoClientManager;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.protocol.data.BingoCard;
import io.github.steaf23.bingoreloaded.protocol.data.BingoGamemode;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorContext;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.TaskSlot;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloaded.protocol.payload.PayloadDefinition;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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

		messenger.registerIncomingPluginChannel(plugin, BingoReloadedPayloads.CLIENT_GET_CREATOR_LIST.key().asString(), (channel, player, buf) -> {
			if (!channel.equals(BingoReloadedPayloads.CLIENT_GET_CREATOR_LIST.key().asString())) return;

			String list = decodePayload(BingoReloadedPayloads.CLIENT_GET_CREATOR_LIST, buf);
			if (list.isBlank()) {
				return;
			}

			PlatformServer server = bingo.getGameManager().getServer();
			PlayerHandle playerHandle = new PlayerHandlePaper(bingo.getGameManager().getServer(), player);
			if (!BingoReloaded.isAdmin(playerHandle)) {
				BingoPlayerSender.sendMessage(Component.text("You do not have permission to edit tasks!").color(NamedTextColor.RED), playerHandle);
				return;
			}

			sendCreatorList(playerHandle, new BingoCardData().lists().getList(server, list));
		});

		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.UPDATE_CARD.key().asString());
		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.key().toString());
		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.OPEN_CREATOR.key().toString());
		messenger.registerOutgoingPluginChannel(plugin, BingoReloadedPayloads.CREATOR_LIST.key().toString());
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
	public void openCreator(PlayerHandle player, @NotNull CreatorTaskSupplier tasks, @NonNull BingoCardData cardData) {
		CreatorContext context = new CreatorContext(tasks, cardData.getAllCards());
		sendMessage(((PlayerHandlePaper)player).handle(), BingoReloadedPayloads.OPEN_CREATOR, context);
	}

	@Override
	public void sendCreatorList(PlayerHandle player, CustomList list) {
		sendMessage(((PlayerHandlePaper)player).handle(), BingoReloadedPayloads.CREATOR_LIST, list);
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

	public <T> T decodePayload(PayloadDefinition<T> definition, byte[] buf) {
		try {
			return definition.codec().decode(new DataInputStream(new ByteArrayInputStream(buf)));
		} catch (IOException e) {
			ConsoleMessenger.bug("Cannot decode payload of type " + definition.key().asString() + " , received buffer size: " + buf.length, this);
			throw new RuntimeException(e);
		}
	}
}
