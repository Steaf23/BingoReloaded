package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoSound;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformTaskScheduler;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

public class GoUpWand extends GameItem {

	public static final Key WAND_COOLDOWN_GROUP = BingoReloaded.resourceKey("wand_cooldown");

	public static final Key ID = BingoReloaded.resourceKey("go_up_wand");

	public GoUpWand() {
		super(ID, ItemCooldown.configurableCooldown(WAND_COOLDOWN_GROUP, BingoOptions.GO_UP_WAND_COOLDOWN));
	}

	@Override
	public ItemTemplate createForParticipant(@Nullable BingoParticipant participant) {
		return new ItemTemplate(
				VanillaItems.WARPED_FUNGUS_ON_A_STICK.type(),
				BingoMessage.ITEM_WAND_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
				BingoMessage.ITEM_WAND_DESC.asMultiline())
				.addEnchantment(Key.key("minecraft:unbreaking"), 3)
				.setGlowing(true);
	}

	@Override
	public EventResult<?> use(StackHandle stack, PlayerHandle player, BingoParticipant participant, BingoGame game) {
		var config = game.getConfig();

		useGoUpWand(game.taskScheduler(), player,
				config.getOptionValue(BingoOptions.GO_UP_WAND_DOWN_DISTANCE),
				config.getOptionValue(BingoOptions.GO_UP_WAND_UP_DISTANCE),
				config.getOptionValue(BingoOptions.GO_UP_WAND_PLATFORM_LIFETIME));

		return EventResult.CONSUME;
	}

	private void useGoUpWand(PlatformTaskScheduler taskScheduler, PlayerHandle player, int downDistance, int upDistance, int platformLifetimeSeconds) {
		taskScheduler.runTask(task -> {
			double distance;
			double fallDistance;
			// Use the wand
			if (player.isSneaking()) {
				distance = -downDistance;
				fallDistance = 0.0;
			} else {
				distance = upDistance;
				fallDistance = 2.0;
			}

			GlobalPosition teleportLocation = player.position();
			GlobalPosition platformLocation = teleportLocation.clone().floor();
			teleportLocation.setY(teleportLocation.y() + distance + fallDistance);
			platformLocation.setY(platformLocation.y() + distance);

			BingoGame.spawnPlatform(player.world(), platformLocation, 1, true);
			taskScheduler.runTask((long) Math.max(0, platformLifetimeSeconds) * BingoReloaded.ONE_SECOND, laterTask -> {
				BingoGame.removePlatform(player.world(), platformLocation, 1);
			});

			player.teleportBlocking(teleportLocation);
			player.playSound(BingoSound.GO_UP_WAND_USED.sound());

			player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:resistance"),
					BingoReloaded.ONE_SECOND * (platformLifetimeSeconds + 4))
					.setAmplifier(100)
					.setParticles(false));

			BingoReloaded.incrementPlayerStat(player, BingoStatType.WAND_USES);
		});
	}
}
