package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public class StackBuilderPaper implements StackBuilder {


	@SuppressWarnings("UnstableApiUsage")
	@Override
	public StackHandle buildItem(ItemTemplate template, boolean hideAttributes, boolean customTextures) {
//		if (textured && texturedVariant != null) {
//			return buildItem(hideAttributes, false);
//		}

		List<Component> descriptionList = template.buildDescriptionList();
		ItemStack stack = new ItemStack(((ItemTypePaper)template.getItemType()).handle(), template.getAmount());

		stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, template.isGlowing());
		stack.setData(DataComponentTypes.CUSTOM_NAME, template.getName().colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
		stack.setData(DataComponentTypes.LORE, ItemLore.lore(descriptionList));

		var maxDamage = template.getMaxDamage();
		if (maxDamage != null) {
			stack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
			stack.setData(DataComponentTypes.DAMAGE, template.getDamage());
		}

		if (template.getMaxStackSize() != null) {
			stack.setData(DataComponentTypes.MAX_STACK_SIZE, template.getMaxStackSize());
		}

		var tooltipBuilder = TooltipDisplay.tooltipDisplay()
				.hideTooltip(template.hasNoTooltip())
				.addHiddenComponents(
						DataComponentTypes.ENCHANTMENTS,
						DataComponentTypes.UNBREAKABLE,
						DataComponentTypes.DYED_COLOR,
						DataComponentTypes.STORED_ENCHANTMENTS,
						DataComponentTypes.PROVIDES_TRIM_MATERIAL);
		if (hideAttributes) {
			tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		}
		stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());

		var enchantmentBuilder = ItemEnchantments.itemEnchantments();
		var enchantments = template.getEnchantments();
		for (Key key : enchantments.keySet()) {
			Enchantment enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
			if (enchant == null) {
				ConsoleMessenger.bug("Invalid enchantment '" + key + "' cannot be put on an item", this);
				continue;
			}

			enchantmentBuilder.add(enchant, enchantments.get(key));
		}
		stack.setData(DataComponentTypes.ENCHANTMENTS, enchantmentBuilder);

		//FIXME: REFACTOR reimplement custom model data
//		stackMeta.setCustomModelData(customModelData);

		if (template.getCompareKey() != null) {
			ItemMeta meta = stack.getItemMeta();
			if (meta != null) {
				PersistentDataContainer pdc = meta.getPersistentDataContainer();
				PDCHelper.addStringToPdc(pdc, "compare_key", template.getCompareKey());
				stack.setItemMeta(meta);
			}
		}

		return new StackHandlePaper(stack);
	}
}
