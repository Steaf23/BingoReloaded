package io.github.steaf23.bingoreloaded.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {

	@Accessor("containerCounter")
	int bingoreloaded$containerCounter();

	@Invoker("nextContainerCounter")
	void bingoreloaded$nextContainerCounter();

	@Invoker("initMenu")
	void bingoreloaded$initMenu(final AbstractContainerMenu container);
}
