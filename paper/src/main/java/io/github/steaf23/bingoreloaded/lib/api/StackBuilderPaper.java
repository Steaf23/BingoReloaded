package io.github.steaf23.bingoreloaded.lib.api;

import com.google.common.collect.ImmutableMultimap;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.function.Function;

public class StackBuilderPaper implements StackBuilder {

	@Override
	public StackHandle buildItem(ItemTemplate template, boolean hideAttributes, boolean customTextures) {
		if (textured && texturedVariant != null) {
			return buildItem(hideAttributes, false);
		}

		List<Component> descriptionList = buildDescriptionList();
		StackHandle stack = new StackHandle(type, amount);

		ItemMeta stackMeta = stack.getItemMeta();
		if (stackMeta == null) {
			return stack;
		}

		if (glowing) {
			stackMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
		}

		if (name != null) {
			stackMeta.displayName(name.colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
		}
		if (!descriptionList.isEmpty()) {
			stackMeta.lore(descriptionList);
		}

		if (maxDamage != null) {
			if (stackMeta instanceof Damageable) {
				((Damageable)stackMeta).setMaxDamage(maxDamage);
				((Damageable)stackMeta).setDamage(currentDamage);
			}
		}

		if (maxStackSize != null) {
			stackMeta.setMaxStackSize(maxStackSize);
		}

		if (compareKey != null) {
			PersistentDataContainer pdc = stackMeta.getPersistentDataContainer();
			PDCHelper.addStringToPdc(pdc, "compare_key", compareKey);
		}

		stackMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE,
				ItemFlag.HIDE_DYE, ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM);
		if (hideAttributes) {
			//TODO: change if there is a need for items to be used by the player with hidden attributes
			stackMeta.setAttributeModifiers(ImmutableMultimap.of());
			stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}

		stackMeta.setCustomModelData(customModelData);

		for (Function<ItemMeta, ItemMeta> modifier : metaModifiers) {
			stackMeta = modifier.apply(stackMeta);
		}
		for (Enchantment enchantment : enchantments.keySet()) {
			stackMeta.addEnchant(enchantment, enchantments.get(enchantment), true);
		}
		stack.setItemMeta(stackMeta);

		return stack;
	}
}
