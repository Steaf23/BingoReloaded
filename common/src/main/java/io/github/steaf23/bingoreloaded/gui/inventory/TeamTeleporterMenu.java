package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuType;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ScrollableItemBar;
import io.github.steaf23.bingoreloaded.lib.inventory.group.SelectionModel;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class TeamTeleporterMenu extends BasicMenu {

	private static final ItemTemplate TO_SPAWN = new ItemTemplate(VanillaItems.COMPASS.type(), BingoReloaded.applyTitleFormat(BingoMessage.TELEPORT_TO_SPAWN.asPhrase()));

	private final BingoPlayer player;
	private final Consumer<PlayerHandle> optionSelected;
	private final BingoGame game;

	public TeamTeleporterMenu(MenuBoard manager, BingoPlayer player, BingoGame game, Consumer<PlayerHandle> optionSelected) {
		super(manager, BingoMessage.TEAM_TELEPORTER_TITLE.asPhrase(), MenuType.DROPPER);
		this.player = player;
		this.game = game;
		this.optionSelected = optionSelected;
	}

	@Override
	public void beforeOpening(PlayerHandle playerHandle) {
		super.beforeOpening(playerHandle);

		addAction(TO_SPAWN.copyToSlot(1), args -> {
			game.getSpawnCoordinator().teleportPlayerToSpawn(player);
			close(args.player());
			optionSelected.accept(args.player());
		});

		if (player.getTeam() == null) {
			return;
		}

		List<PlayerHandle> members = new ArrayList<>(player.getTeam().getMembers()).stream()
				.filter(p -> !p.equals(player))
				.map(BingoParticipant::sessionPlayer)
				.flatMap(Optional::stream)
				.toList();

		// special case for duos to make the GUI more centered
		if (members.size() == 1) {
			PlayerHandle otherPlayer = members.getFirst();
			UUID playerId = otherPlayer.uniqueId();
			addAction(teleportToPlayer(otherPlayer).copyToSlot(7), args -> {
				playerClickedCallback(args.player(), playerId);
			});
			return;
		}

		ScrollableItemBar<UUID> players = new ScrollableItemBar<>(this, 6, 0, 3, SelectionModel.SelectMode.NONE);
		players.setItemClickedCallback((args, index, item, uuid) -> {
			playerClickedCallback(args.player(), uuid);
			return item;
		});

		List<ItemTemplate> templates = new ArrayList<>();
		List<UUID> ids = new ArrayList<>();
		for (PlayerHandle player : members) {
			templates.add(teleportToPlayer(player));
			ids.add(player.uniqueId());
		}

		players.setItems(templates, ids);
	}

	public void open() {
		player.sessionPlayer().ifPresent(this::open);
	}

	private ItemTemplate teleportToPlayer(PlayerHandle toPlayer) {
		return ItemTemplate.createPlayerHead(toPlayer)
				.setName(BingoReloaded.applyTitleFormat(toPlayer.displayName()))
				.setLore(INPUT_LEFT_CLICK.append(Component.translatable("spectatorMenu.teleport")));
	}

	public void playerClickedCallback(PlayerHandle currentPlayer, UUID playerId) {
		game.getSession().teamManager.getParticipants().stream()
				.filter(participant -> participant.getId().equals(playerId))
				.findAny().flatMap(BingoParticipant::sessionPlayer).ifPresentOrElse(player -> {
							currentPlayer.teleportAsync(player.position());
							close(currentPlayer);
							optionSelected.accept(currentPlayer);
						},
						() -> {
							close(currentPlayer);
							BingoPlayerSender.sendMessage(Component.translatable("argument.entity.notfound.player").color(NamedTextColor.RED), currentPlayer);
						});
	}
}
