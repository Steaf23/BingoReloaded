package io.github.steaf23.bingoreloaded.mixin;

import io.github.steaf23.bingoreloaded.lib.event.PlayerDroppedItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	// Nearly identical to paper's patch for the PlayerDropItemEvent
	@Inject(
			method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At("HEAD"),
			cancellable = true)
	private void onDrop(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
		if (!thrownFromHand) {
			return;
		}

		if (!((LivingEntity)(Object)this instanceof ServerPlayer player)) {
			return;
		}

		InteractionResult result = PlayerDroppedItem.EVENT.invoker().droppedItem(player, itemStack);
		boolean cancel = result == InteractionResult.FAIL || result == InteractionResult.CONSUME;
		if (cancel) {
			cir.cancel();
			player.inventoryMenu.broadcastFullState();
			if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
				player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
			}
			else {
				player.addItem(itemStack);
			}
		}
	}

}
