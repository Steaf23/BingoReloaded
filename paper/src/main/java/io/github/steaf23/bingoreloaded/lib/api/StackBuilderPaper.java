package io.github.steaf23.bingoreloaded.lib.api;

import com.google.common.collect.ImmutableMultimap;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.function.Function;

public class StackBuilderPaper implements StackBuilder {

	@Override
	public StackHandle buildItem(ItemTemplate template, boolean hideAttributes, boolean customTextures) {
//		if (textured && texturedVariant != null) {
//			return buildItem(hideAttributes, false);
//		}

		List<Component> descriptionList = template.buildDescriptionList();
		ItemStack stack = new ItemStack(((ItemTypePaper)template.getItemType()).handle(), template.getAmount());

		ItemMeta stackMeta = stack.getItemMeta();
		if (stackMeta == null) {
			return new StackHandlePaper(stack);
		}

		if (template.isGlowing()) {
			stackMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
		}

		if (template.getName() != null) {
			stackMeta.displayName(template.getName().colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
		}
		if (!descriptionList.isEmpty()) {
			stackMeta.lore(descriptionList);
		}

		var maxDamage = template.getMaxDamage();
		if (maxDamage != null) {
			if (stackMeta instanceof Damageable) {
				((Damageable)stackMeta).setMaxDamage(maxDamage);
				((Damageable)stackMeta).setDamage(template.getDamage());
			}
		}

		if (template.getMaxStackSize() != null) {
			stackMeta.setMaxStackSize(template.getMaxStackSize());
		}

		if (template.getCompareKey() != null) {
			PersistentDataContainer pdc = stackMeta.getPersistentDataContainer();
			PDCHelper.addStringToPdc(pdc, "compare_key", template.getCompareKey());
		}

		stackMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE,
				ItemFlag.HIDE_DYE, ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM);
		if (hideAttributes) {
			//TODO: change if there is a need for items to be used by the player with hidden attributes
			stackMeta.setAttributeModifiers(ImmutableMultimap.of());
			stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}

		//FIXME: REFACTOR reimplement custom model data
//		stackMeta.setCustomModelData(customModelData);

		var enchantments = template.getEnchantments();
		for (Key key : enchantments.keySet()) {
			Enchantment enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
			if (enchant == null) {
				ConsoleMessenger.bug("Invalid enchantment '" + key + "' cannot be put on an item", this);
				continue;
			}
			stackMeta.addEnchant(enchant, enchantments.get(key), true);
		}
		stack.setItemMeta(stackMeta);

		return new StackHandlePaper(stack);
	}
}
