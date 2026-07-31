package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryListener;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FabricMenu extends AbstractContainerMenu implements InventoryListener {

	private final Menu menu;
	private final MenuBoard board;
	private final PlayerHandle player;
	private final Container container;

	public FabricMenu(PlayerHandle player, @Nullable MenuType<?> menuType, int containerId, Menu menu, Inventory playerInventory) {
		super(menuType, containerId);

		this.player = player;
		this.menu = menu;
		this.board = menu.getMenuBoard();
		InventoryTemplate template = menu.getBackedInventory();
		container = new SimpleContainer(template.size());

		for (int i = 0; i < template.size(); i++) {
			addSlot(new MenuSlot(container, i, 0, 0, null));
			setItem(i, containerId, ((StackHandleFabric)template.getItem(i)).handle());
		}
		addStandardInventorySlots(playerInventory, 0, 0);

		template.addListener(this);
	}

	@Override
	public void clicked(int slotIndex, int buttonNum, @NonNull ContainerInput containerInput, @NonNull Player player) {
		System.out.println("Slot " + slotIndex + " clicked on with button " + buttonNum + " to perform: " + containerInput);

		// NOTE: throwing an item by dragging it out of the inventory will not be possible with this,
		// as the slotIndex will be -999. This means the ClickType WINDOW_BORDER_* will never be called.
		if (slotIndex < 0 || slotIndex >= menu.getBackedInventory().size()) {
			return;
		}

		ClickType type = switch (containerInput) {
			case PICKUP -> buttonNum == 0 ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK;
			case QUICK_MOVE -> buttonNum == 0 ? ClickType.SHIFT_LEFT : ClickType.SHIFT_RIGHT;
			case SWAP -> buttonNum == 0 ? ClickType.NUMBER_KEY : ClickType.SWAP_OFFHAND;
			case CLONE -> ClickType.MIDDLE;
			case THROW -> buttonNum == 0 ? ClickType.DROP : ClickType.CONTROL_DROP;
			case QUICK_CRAFT -> ClickType.UNKNOWN;
			case PICKUP_ALL -> ClickType.DOUBLE_CLICK;
		};

		ItemStack clickedStack = container.getItem(slotIndex);
		board.inventoryClicked(
				this.player,
				menu,
				type,
				slotIndex,
				new StackHandleFabric(clickedStack));
	}

	@Override
	public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}

	@Override
	public void itemChanged(InventoryTemplate template, int slot, @org.jetbrains.annotations.Nullable StackHandle newStack, @org.jetbrains.annotations.Nullable StackHandle oldStack) {
		setItem(slot, containerId, ((StackHandleFabric)newStack).handle());
	}

	@Override
	public void cleared(InventoryTemplate template) {
		for (int i = 0; i < template.size(); i++) {
			setItem(i, containerId, ItemStack.EMPTY);
		}
	}

	@Override
	public void contentsReplaced(InventoryTemplate template, StackHandle[] newContents, StackHandle[] oldContents) {
		for (int i = 0; i < template.size(); i++) {
			setItem(i, containerId, ((StackHandleFabric)newContents[i]).handle());
		}
	}
}
