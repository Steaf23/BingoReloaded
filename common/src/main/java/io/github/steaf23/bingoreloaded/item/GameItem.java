package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import jdk.jfr.EventType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class GameItem implements Keyed {

	private final Key id;
	private final @Nullable ItemCooldown cooldown;
	private boolean automaticCooldown;

	public GameItem(Key id) {
		this(id, null, false);
	}

	public GameItem(Key id, @Nullable ItemCooldown cooldown) {
		this(id, cooldown, true);
	}

	public GameItem(Key id, @Nullable ItemCooldown cooldown, boolean automaticCooldown) {
		this.id = id;
		this.cooldown = cooldown;
		this.automaticCooldown = automaticCooldown;
	}

	@Override
	public @NotNull Key key() {
		return id;
	}

	public Optional<ItemCooldown> cooldown() {
		return Optional.ofNullable(cooldown);
	}

	public EventResult<?> tryUse(StackHandle stack, PlayerHandle player, BingoParticipant participant, BingoGame game) {
		if (player.hasCooldown(stack)) {
			return EventResult.IGNORE;
		}

		if (!automaticCooldown || applyCooldown(stack, player, game).consume()) {
			return use(stack, player, participant, game);
		} else {
			return EventResult.IGNORE;
		}
	}

	/**
	 * @return EventResult.CONSUME when the cooldown successfully started.
	 */
	public EventResult<?> applyCooldown(StackHandle stack, PlayerHandle player, BingoGame game) {
		if (cooldown == null) {
			return EventResult.CONSUME;
		}

		if (player.hasCooldown(stack)) {
			return EventResult.IGNORE;
		}

		double cooldownSeconds = cooldown.cooldownSupplier().apply(game);

		stack.setCooldown(cooldown.group(), cooldownSeconds);
		player.setCooldown(stack, (int)(cooldownSeconds * 20));
		return EventResult.CONSUME;
	}

	public boolean canLeaveInventory() {
		return false;
	}

	public abstract ItemTemplate createForParticipant(@Nullable BingoParticipant participant);

	public abstract EventResult<?> use(StackHandle stack, PlayerHandle player, BingoParticipant participant, BingoGame game);
}
