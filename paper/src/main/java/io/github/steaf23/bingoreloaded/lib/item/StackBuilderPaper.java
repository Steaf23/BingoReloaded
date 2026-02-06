package io.github.steaf23.bingoreloaded.lib.item;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.DataComponentTypeKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public class StackBuilderPaper {


	@SuppressWarnings("UnstableApiUsage")
	public StackHandle buildItem(ItemTemplate template, boolean hideAttributes, boolean customTextures) {
//		if (textured && texturedVariant != null) {
//			return buildItem(hideAttributes, false);
//		}

		List<Component> descriptionList = template.buildDescriptionList();

		ItemStack stack;

		if (template.isDummy()) {
			stack = ItemStack.of(Material.POISONOUS_POTATO, template.getAmount());
			stack.unsetData(DataComponentTypes.CONSUMABLE);
			stack.setData(DataComponentTypes.ITEM_MODEL, template.getItemType().key());
		}
		else {
			stack = ItemStack.of(template.getItemType(), template.getAmount());
		}


		if (template.getName() != null) {
			stack.setData(DataComponentTypes.CUSTOM_NAME, template.getName().colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
		}
		stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, template.isGlowing());
		stack.setData(DataComponentTypes.LORE, ItemLore.lore(descriptionList));

		var maxDamage = template.getMaxDamage();
		if (maxDamage != null) {
			stack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
			stack.setData(DataComponentTypes.DAMAGE, template.getDamage());
		}

		if (template.getMaxStackSize() != null) {
			stack.setData(DataComponentTypes.MAX_STACK_SIZE, template.getMaxStackSize());
			stack.unsetData(DataComponentTypes.MAX_DAMAGE);
		}

		var tooltipBuilder = TooltipDisplay.tooltipDisplay()
				.hideTooltip(template.hasNoTooltip())
				.addHiddenComponents(
						DataComponentTypes.ENCHANTMENTS,
						DataComponentTypes.UNBREAKABLE,
						DataComponentTypes.DYED_COLOR,
						DataComponentTypes.STORED_ENCHANTMENTS,
						DataComponentTypes.PROVIDES_TRIM_MATERIAL,
						DataComponentTypes.BUNDLE_CONTENTS,
						DataComponentTypes.BLOCK_DATA,
						DataComponentTypes.POTION_CONTENTS,
						RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).get(DataComponentTypeKeys.BEES));
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
		if (template.getLeatherColor() != null) {
			stack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(template.getLeatherColor().value())));
		}

		stack.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(1).cooldownGroup(template.getCooldownGroup()));

		stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(template.getCustomModelData()).build());

		if (template.getCompareKey() != null) {
			ItemMeta meta = stack.getItemMeta();
			if (meta != null) {
				PersistentDataContainer pdc = meta.getPersistentDataContainer();
				PDCHelper.addStringToPdc(pdc, "compare_key", template.getCompareKey());
				stack.setItemMeta(meta);
			}
		}

		StackHandle handle = new StackHandlePaper(stack);
		if (template.getExtraData() != null) {
			handle.setStorage(template.getExtraData());
		}

		return handle;
	}

	public StackHandle buildItem(ItemTemplate template, boolean hideAttributes) {
		return buildItem(template, hideAttributes, false);
	}

	public StackHandle buildItem(ItemTemplate template) {
		return buildItem(template, false, false);
	}
}
