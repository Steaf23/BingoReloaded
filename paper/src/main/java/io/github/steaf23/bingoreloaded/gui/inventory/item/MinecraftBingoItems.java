package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

public class MinecraftBingoItems {

	public static final ItemTemplate GO_UP_WAND = new ItemTemplate(
			ItemTypePaper.of(Material.WARPED_FUNGUS_ON_A_STICK),
					BingoMessage.WAND_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
					BingoMessage.WAND_ITEM_DESC.asMultiline())
			.addEnchantment(Key.key("minecraft:unbreaking"), 3)
			.setCompareKey("go_up_wand");

	public static final ItemTemplate TEAM_SHULKER = new ItemTemplate(
			ItemTypePaper.of(Material.RED_SHULKER_BOX))
			.setDummy(true)
			.setCompareKey("team_shulker");

	public static final ItemTemplate CARD_ITEM_RENDERABLE = new ItemTemplate(
			ItemTypePaper.of(Material.FILLED_MAP),
			BingoMessage.CARD_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
			BingoMessage.CARD_ITEM_DESC.asMultiline())
			.setGlowing(true)
			.setCompareKey("card");

	public static final ItemTemplate CARD_ITEM = new ItemTemplate(
			ItemTypePaper.of(Material.FLOWER_BANNER_PATTERN),
			BingoMessage.CARD_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
			BingoMessage.CARD_ITEM_DESC.asMultiline())
			.setGlowing(true)
			.setCompareKey("card");

	public static final ItemTemplate VOTE_ITEM = new ItemTemplate(
			ItemTypePaper.of(Material.EMERALD),
			BingoMessage.VOTE_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
			BingoMessage.VOTE_ITEM_DESC.asMultiline())
			.setCompareKey("vote")
			.addEnchantment(Key.key("minecraft:vanishing_curse"), 1);

	public static final ItemTemplate TEAM_ITEM = new ItemTemplate(
			ItemTypePaper.of(Material.WHITE_GLAZED_TERRACOTTA),
			BingoMessage.TEAM_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
			BingoMessage.TEAM_ITEM_DESC.asMultiline())
			.setCompareKey("team")
			.addEnchantment(Key.key("minecraft:vanishing_curse"), 1);
}
