package io.github.steaf23.bingoreloaded.mixin;

import io.github.steaf23.bingoreloaded.lib.event.PlayerAdvancementCompleted;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {

	@Shadow
	private ServerPlayer player;

	@Inject(
			method = "award",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V")
	)
	public void beforeRewards(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir) {
		PlayerAdvancementCompleted.EVENT.invoker().advancementCompleted(player, holder, criterion);
	}
}
