package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.PaperItemEditor;
import io.github.steaf23.bingoreloaded.lib.api.item.StackBuilderPaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PaperItemStacker implements PlatformItemStacker {

	@Override
	public StackHandle createStack(ItemType type, int amount) {
		Material mat = ((ItemTypePaper)type).handle();
		return new StackHandlePaper(new ItemStack(mat, amount));
	}

	@Override
	public StackHandle createStackFromBytes(byte[] bytes) {
		return new StackHandlePaper(ItemStack.deserializeBytes(bytes));
	}

	@Override
	public StackHandle createStackFromTemplate(ItemTemplate template, boolean hideAttributes) {
		return new StackBuilderPaper().buildItem(template, hideAttributes);
	}

	@Override
	public byte[] createBytesFromStack(StackHandle stack) {
		return ((StackHandlePaper)stack).handle().serializeAsBytes();
	}

	@Override
	public StackHandle colorItemStack(StackHandle stack, TextColor color) {
		if (!ItemTemplate.LEATHER_ARMOR.contains(stack.type())) {
			return stack;
		}
		((StackHandlePaper)stack).handle().setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(color.value())));
		return stack;
	}

	@Override
	public ItemTemplate createPlayerHeadTemplate(PlayerHandle player) {
		return new ItemTemplate(VanillaItems.PLAYER_HEAD.type())
				.customize(PaperItemEditor.class, stack -> {
					stack.editMeta(SkullMeta.class, m -> m.setOwningPlayer(((PlayerHandlePaper)player).handle()));
				});
	}
}
