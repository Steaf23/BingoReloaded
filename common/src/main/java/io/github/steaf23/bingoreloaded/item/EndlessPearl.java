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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

public class EndlessPearl extends GameItem {

	public static final Key PEARL_COOLDOWN_GROUP = BingoReloaded.resourceKey("pearl_cooldown");

	public static final Key ID = BingoReloaded.resourceKey("endless_pearl");

	public EndlessPearl() {
		super(ID);
	}

	@Override
	public ItemTemplate createForParticipant(@Nullable BingoParticipant participant) {
		return new ItemTemplate(VanillaItems.ENDER_PEARL.type(),
				BingoMessage.ENDLESS_PEARL_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
				BingoMessage.ENDLESS_PEARL_DESC.asMultiline())
				.setDummy(true)
				.setGlowing(true);
	}

	@Override
	public EventResult<?> use(StackHandle stack, BingoParticipant participant, BingoGame game) {
		if (participant.sessionPlayer().isEmpty()) {
			return EventResult.IGNORE;
		}

		PlayerHandle player = participant.sessionPlayer().orElseThrow();

		if (player.hasCooldown(stack)) {
			return EventResult.IGNORE;
		}

		double cooldownSeconds = game.getConfig().getOptionValue(BingoOptions.ENDLESS_PEARL_COOLDOWN);

		stack.setCooldown(PEARL_COOLDOWN_GROUP, cooldownSeconds);
		player.setCooldown(stack, (int)(cooldownSeconds * 20));

		player.world().throwPearlForPlayer(player);

		return EventResult.CONSUME;
	}
}
