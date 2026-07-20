package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.BlockColor;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

public class TeamPouch extends GameItem {

	public static final Key ID = BingoReloaded.resourceKey("team_pouch");

	public TeamPouch() {
		super(ID);
	}

	@Override
	public ItemTemplate createForParticipant(@Nullable BingoParticipant participant) {
		ItemTemplate def = new ItemTemplate(BlockColor.WHITE.bundle,
				BingoMessage.ITEM_POUCH_NAME.asPhrase()
						.color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC, TextDecoration.BOLD),
				BingoMessage.ITEM_POUCH_DESC.asMultiline())
				.setDummy(true);

		if (participant == null) {
			return def;
		}

		BingoTeam team = participant.getTeam();
		if (team == null) {
			return def;
		}

		return def.setItemType(team.getDyeColor().bundle);
	}

	@Override
	public EventResult<?> use(StackHandle stack, BingoParticipant participant, BingoConfigurationData config) {
		if (participant instanceof BingoPlayer player && player.getTeam() != null) {
			player.sessionPlayer().ifPresent(handle -> handle.openInventory(player.getTeam().storage()));
		}
		return EventResult.CONSUME;
	}
}
