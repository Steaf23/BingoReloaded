package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

public class TeamTeleporter extends GameItem {

	public static final Key TEAM_TELEPORTER_COOLDOWN_GROUP = BingoReloaded.resourceKey("team_teleporter_cooldown");

	public static final Key ID = BingoReloaded.resourceKey("team_teleporter");

	public TeamTeleporter() {
		super(ID, ItemCooldown.configurableCooldown(TEAM_TELEPORTER_COOLDOWN_GROUP, BingoOptions.TEAM_BOX_COOLDOWN), false);
	}

	@Override
	public ItemTemplate createForParticipant(@Nullable BingoParticipant participant) {
		return new ItemTemplate(VanillaItems.ENDER_CHEST.type(),
				BingoMessage.ITEM_TEAM_TELEPORTER_NAME.asPhrase()
						.color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC, TextDecoration.BOLD),
				BingoMessage.ITEM_TEAM_TELEPORTER_DESC.asPhrase())
				.setDummy(true)
				.setGlowing(true);
	}

	@Override
	public EventResult<?> use(StackHandle stack, PlayerHandle player, BingoParticipant participant, BingoGame game) {
		if (!(participant instanceof BingoPlayer bingoPlayer)) {
			return EventResult.IGNORE;
		}
		game.getSession().getGameManager().getRuntime().openTeamTeleporter(bingoPlayer, game, p -> applyCooldown(stack, p, game));
		return EventResult.CONSUME;
	}
}
