package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TeamTeleporterMenu extends BasicMenu {

	private static final ItemTemplate TO_SPAWN = new ItemTemplate(VanillaItems.COMPASS.type(), BingoReloaded.applyTitleFormat(BingoMessage.TELEPORT_TO_SPAWN.asPhrase()));

	private final BingoPlayer player;
	private final Consumer<PlayerHandle> optionSelected;
	private final BingoGame game;

	public TeamTeleporterMenu(MenuBoard manager, BingoPlayer player, BingoGame game, Consumer<PlayerHandle> optionSelected) {
		super(manager, BingoMessage.TEAM_TELEPORTER_TITLE.asPhrase(), 1);
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

		for (PlayerHandle player : members) {
			addAction(ItemTemplate.createPlayerHead(player), args -> {
				args.player().teleportAsync(player.position());
				close(args.player());
				optionSelected.accept(args.player());
			});
		}
	}

	public void open() {
		player.sessionPlayer().ifPresent(this::open);
	}
}
