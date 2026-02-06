package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoSound;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.kyori.adventure.key.Key;

public class GoUpWand extends GameItem {

	public static Key ID = BingoReloaded.resourceKey("go_up_wand");

	public GoUpWand() {
		super(ID);
	}

	@Override
	public EventResult<?> use(BingoGame game, StackHandle stack, BingoParticipant participant, BingoConfigurationData config) {
		if (participant instanceof BingoPlayer player) {
			player.sessionPlayer().ifPresent(sessionPlayer -> {
				useGoUpWand(game, sessionPlayer, stack,
						config.getOptionValue(BingoOptions.GO_UP_WAND_COOLDOWN),
						config.getOptionValue(BingoOptions.GO_UP_WAND_DOWN_DISTANCE),
						config.getOptionValue(BingoOptions.GO_UP_WAND_UP_DISTANCE),
						config.getOptionValue(BingoOptions.GO_UP_WAND_PLATFORM_LIFETIME));
			});
		}

		return EventResult.CONSUME;
	}

	private void useGoUpWand(BingoGame game, PlayerHandle player, StackHandle wand, double wandCooldownSeconds, int downDistance, int upDistance, int platformLifetimeSeconds) {
		if (player.hasCooldown(wand)) {
			return;
		}

		wand.setCooldown(PlayerKit.WAND_COOLDOWN_GROUP, wandCooldownSeconds);
		player.setCooldown(wand, (int)(wandCooldownSeconds * 20));

		game.runtime().getServerSoftware().runTask(player.world().uniqueId(), task -> {
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

			WorldPosition teleportLocation = player.position();
			WorldPosition platformLocation = teleportLocation.clone().floor();
			teleportLocation.setY(teleportLocation.y() + distance + fallDistance);
			platformLocation.setY(platformLocation.y() + distance);

			game.spawnPlatform(platformLocation, 1, true);
			BingoReloaded.runtime().getServerSoftware().runTask(player.world().uniqueId(), (long) Math.max(0, platformLifetimeSeconds) * BingoReloaded.ONE_SECOND, laterTask -> {
				game.removePlatform(platformLocation, 1);
			});

			player.teleportBlocking(teleportLocation);
			player.playSound(BingoSound.GO_UP_WAND_USED.builder().build());

			player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:resistance"),
					BingoReloaded.ONE_SECOND * (platformLifetimeSeconds + 4))
					.setAmplifier(100)
					.setParticles(false));

			BingoReloaded.incrementPlayerStat(player, BingoStatType.WAND_USES);
		});
	}
}
