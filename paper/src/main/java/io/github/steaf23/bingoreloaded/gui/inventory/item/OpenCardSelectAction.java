package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamCardSelectMenu;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.Material;

public class OpenCardSelectAction extends MenuAction {

	private final BingoReloaded bingo;

	public OpenCardSelectAction(BingoReloaded bingo) {
		this.bingo = bingo;
	}

	public static OpenCardSelectAction createItem(BingoReloaded bingo, int slot) {
		OpenCardSelectAction action = new OpenCardSelectAction(bingo);
		action.setItem(new ItemTemplate(ItemTypePaper.of(Material.BUNDLE), BingoReloaded.applyTitleFormat(BingoMessage.SHOW_TEAM_CARD_TITLE.asPhrase())).setSlot(slot));
		return action;
	}

	@Override
	public void use(ActionArguments arguments) {
		// The reason to not use getSessionOfPlayer is that not all players that execute this command have to be active bingo players.
		BingoSession session = bingo.getGameManager().getSessionFromWorld(arguments.player().world());
		if (session == null) {
			return;
		}

		if (!bingo.config().getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
			return;
		}

		arguments.menu().setOpenOnce(true);

		MenuBoard board = arguments.menu().getMenuBoard();
		new TeamCardSelectMenu(board, session).open(arguments.player());
	}
}
