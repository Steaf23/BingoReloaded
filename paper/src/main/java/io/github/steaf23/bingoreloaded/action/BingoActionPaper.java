package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gui.inventory.item.MinecraftBingoItems;
import io.github.steaf23.bingoreloaded.item.GoUpWand;
import io.github.steaf23.bingoreloaded.lib.action.ActionArgument;
import io.github.steaf23.bingoreloaded.lib.action.ActionResult;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;

import java.util.List;

public class BingoActionPaper extends BingoAction {

	public BingoActionPaper(BingoReloaded bingo, BingoConfigurationData config, GameManager gameManager) {
		super(bingo, config, gameManager);

		ActionTree itemKitAction = new ActionTree("item", (user, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}
			return giveUserBingoItem(player, args[0]);
		})
				.addArgument(ActionArgument.required("item_name", List.of("wand", "card")));

		ActionTree kitAction = getSubAction("kit");
		if (kitAction != null) {
			kitAction.addSubAction(itemKitAction);
		}

		this.addSessionSubAction("getcard", List.of(), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			if (session.canPlayersViewCard()) {
				BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
				if (participant instanceof BingoPlayer bingoPlayer) {
					BingoGame game = (BingoGame) session.phase();
					game.setupPlayer(player.world(), bingoPlayer);
				}
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.IGNORED;
			}
		});
	}

	public ActionResult giveUserBingoItem(PlayerHandle player, String itemName) {
		BingoSession session = getSessionFromUser(player);
		if (session == null) {
			return ActionResult.IGNORED;
		}

		return switch (itemName) {
			case "wand" -> {
				player.inventory().addItem(session.items().createStack(GoUpWand.ID));
				yield ActionResult.SUCCESS;
			}
			case "card" -> {
				player.inventory().addItem(MinecraftBingoItems.CARD_ITEM.buildItem());
				yield ActionResult.SUCCESS;
			}
			default -> ActionResult.INCORRECT_USE;
		};
	}
}
