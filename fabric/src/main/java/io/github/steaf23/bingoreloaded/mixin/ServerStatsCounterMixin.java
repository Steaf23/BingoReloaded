package io.github.steaf23.bingoreloaded.mixin;

import io.github.steaf23.bingoreloaded.lib.event.PlayerStatIncrement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsCounter.class)
public class ServerStatsCounterMixin {

	@Inject(
			method = "increment",
			at = @At("HEAD"),
			cancellable = true)
	private void onIncrement(Player player, Stat<?> stat, int count, CallbackInfo ci) {
		ServerPlayer serverPlayer = (ServerPlayer) player;
		int current = serverPlayer.getStats().getValue(stat);
		int newValue = current + count;
		InteractionResult result = PlayerStatIncrement.EVENT.invoker().incrementStat(serverPlayer, stat, count, current, newValue);

		if (result == InteractionResult.FAIL) {
			ci.cancel();
		}
	}
}
