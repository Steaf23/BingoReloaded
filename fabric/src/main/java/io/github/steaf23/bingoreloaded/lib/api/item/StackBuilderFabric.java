package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class StackBuilderFabric implements StackBuilder {

	private final PlatformServer server;

	public StackBuilderFabric(PlatformServer server) {
		this.server = server;
	}

	@Override
	public StackHandle buildItem(ItemTemplate template, boolean hideAttributes, boolean customTextures) {
		//		if (textured && texturedVariant != null) {
//			return buildItem(hideAttributes, false);
//		}

		List<Component> descriptionList = template.buildDescriptionList();

		ItemStack stack;

		if (template.isDummy()) {
			stack = new ItemStack(Items.POISONOUS_POTATO, template.getAmount());
			stack.remove(DataComponents.CONSUMABLE);
			stack.set(DataComponents.ITEM_MODEL, FabricTypes.idFromKey(template.getItemType().key()));
		}
		else {
			stack = new ItemStack(((ItemTypeFabric)template.getItemType()).handle(), template.getAmount());
		}


		if (template.getName() != null) {
			stack.set(DataComponents.CUSTOM_NAME, FabricTypes.toNativeComponent(server, template.getName().colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
		}
		stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, template.isGlowing());
		stack.set(DataComponents.LORE, new ItemLore(descriptionList.stream()
				.map(c -> FabricTypes.toNativeComponent(server, c))
				.toList()));

		var maxDamage = template.getMaxDamage();
		if (maxDamage != null) {
			stack.set(DataComponents.MAX_DAMAGE, maxDamage);
			stack.set(DataComponents.DAMAGE, template.getDamage());
		}

		if (template.getMaxStackSize() != null) {
			stack.set(DataComponents.MAX_STACK_SIZE, template.getMaxStackSize());
		}

		var tooltipDisplay = new TooltipDisplay(template.hasNoTooltip(), new LinkedHashSet<>(List.of(
				DataComponents.UNBREAKABLE,
				DataComponents.DYED_COLOR,
				DataComponents.STORED_ENCHANTMENTS,
				DataComponents.PROVIDES_TRIM_MATERIAL,
				DataComponents.BUNDLE_CONTENTS,
				DataComponents.BLOCK_STATE,
				DataComponents.POTION_CONTENTS,
				DataComponents.BEES
		)));

		if (hideAttributes) {
			tooltipDisplay.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true);
		}
		stack.set(DataComponents.TOOLTIP_DISPLAY, tooltipDisplay);

		MinecraftServer fabricServer = ((FabricServer)server).handle();
		var enchantmentBuilder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		var enchantments = template.getEnchantments();
		for (Key key : enchantments.keySet()) {
			Holder<Enchantment> enchant = fabricServer.registryAccess().get(ResourceKey.create(Registries.ENCHANTMENT, FabricTypes.idFromKey(key))).orElse(null);
			if (enchant == null) {
				ConsoleMessenger.bug("Invalid enchantment '" + key + "' cannot be put on an item", this);
				continue;
			}

			enchantmentBuilder.set(enchant, enchantments.get(key));
		}
		stack.set(DataComponents.ENCHANTMENTS, enchantmentBuilder.toImmutable());
		if (template.getLeatherColor() != null) {
			stack.set(DataComponents.DYED_COLOR, new DyedItemColor(template.getLeatherColor().value()));
		}

		stack.set(DataComponents.USE_COOLDOWN, new UseCooldown(1, Optional.ofNullable(FabricTypes.idFromKey(template.getCooldownGroup()))));

		stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(template.getCustomModelData()), List.of()));

		if (template.getCompareKey() != null) {
			CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
			data.update(tag -> {
				tag.putString("compare_key", template.getCompareKey());
			});
			stack.set(DataComponents.CUSTOM_DATA, data);
		}

		// TODO: add if a fabric item editor is needed.
//		PaperItemEditor stackEditor = template.getCustomizer(PaperItemEditor.class);
//		if (stackEditor != null) {
//			stackEditor.edit(stack);
//		}

		StackHandle handle = new StackHandleFabric(stack);
		if (template.getExtraData() != null) {
			handle.setStorage(template.getExtraData());
		}

		return handle;
	}
}
