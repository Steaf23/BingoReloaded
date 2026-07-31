package io.github.steaf23.bingoreloaded.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class MenuSlot extends Slot {

	private final Consumer<Player> onClick;

	public MenuSlot(Container container, int index, int x, int y,
	                Consumer<Player> onClick) {
		super(container, index, x, y);
		this.onClick = onClick;
	}

	@Override
	public boolean allowModification(Player player) {
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(Player player) {
		return false;
	}

	public void click(Player player) {
		onClick.accept(player);
	}
}
