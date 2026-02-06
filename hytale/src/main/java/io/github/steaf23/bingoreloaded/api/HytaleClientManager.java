package io.github.steaf23.bingoreloaded.api;

import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.steaf23.bingoreloaded.api.network.BingoClientManager;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.gui.BingoCardUIHud;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleHytale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HytaleClientManager implements BingoClientManager {


	@Override
	public boolean playerHasClient(PlayerHandle player) {
		return true;
	}

	@Override
	public void updateCard(PlayerHandle player, @Nullable TaskCard card) {
		if (fromPlayer(player).getCustomHud() instanceof BingoCardUIHud uiHud) {
			uiHud.setCard(card);
		}
		else {
			PlayerRef ref = ref(player);
			BingoCardUIHud uiHud = new BingoCardUIHud(ref);
			uiHud.setCard(card);
			fromPlayer(player).setCustomHud(ref, uiHud);
		}
	}

	@Override
	public void updateHotswapContext(PlayerHandle player, @NotNull List<HotswapTaskHolder> holders) {

	}

	@Override
	public void playerLeavesServer(PlayerHandle player) {
		fromPlayer(player).resetUserInterface(ref(player));
	}

	private static HudManager fromPlayer(PlayerHandle player) {
		return ((PlayerHandleHytale)player).playerFromInternal().getHudManager();
	}

	private static PlayerRef ref(PlayerHandle player) {
		return ((PlayerHandleHytale)player).ref();
	}
}
