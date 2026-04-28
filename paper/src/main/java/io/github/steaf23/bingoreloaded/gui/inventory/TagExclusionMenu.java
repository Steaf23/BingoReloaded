package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ScrollableItemBar;
import io.github.steaf23.bingoreloaded.lib.inventory.group.SelectionModel;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TagExclusionMenu extends BasicMenu {

	private final AdminBingoMenu adminMenu;

	private final BingoCardData cardData;
	private final String cardName;

	public TagExclusionMenu(AdminBingoMenu adminMenu, BingoCardData cardData, String cardName) {
		super(adminMenu.getMenuBoard(), Component.text("Excluded tags"), 1);
		this.cardData = cardData;
		this.cardName = cardName;
		this.adminMenu = adminMenu;
	}

	@Override
	public void beforeOpening(PlayerHandle player) {
		super.beforeOpening(player);

		ScrollableItemBar<String> tagGroup = new ScrollableItemBar<>(this, 0, 0, 8, SelectionModel.SelectMode.MULTIPLE_OR_NONE);

		ItemTemplate closeItem = new ItemTemplate(8, 0, ItemTypePaper.of(Material.EMERALD), Component.text("Play with ").append(Component.text(cardName).color(NamedTextColor.YELLOW)));
		// Action to use when the player finishes selecting the card and excluded tags.
		MenuAction closeAction = new MenuAction() {
			@Override
			public void use(ActionArguments arguments) {
				adminMenu.cardAndTagSelected(cardName, tagGroup.selectedData());
				close(player);
			}
		};

		// Fill in all the available tags that can be excluded
		List<ItemTemplate> templates = new ArrayList<>();
		var availableTags = cardData.tags().getAllTags();
		List<String> tags = new ArrayList<>();
		for (String tagName : availableTags.keySet()) {
			TaskTagData.TaskTag tag = availableTags.get(tagName);
			ItemTemplate tagItem = ItemTemplate.createColoredLeather(tag.color(), ItemTypePaper.of(Material.LEATHER_HELMET))
					.setName(Component.text("<" + tagName + ">").color(tag.color()));
			tagItem = updateTagItemLore(tagItem, false, tagName, availableTags);
			templates.add(tagItem);
			tags.add(tagName);
		}
		tagGroup.setItems(templates, tags);

		// Action to use when the player toggles a tag to exclude, mainly used to re-render the accept item with correct description.
		tagGroup.setItemClickedCallback((index, item, s) -> {
			List<String> selectedTags = tagGroup.selectedData();
			ItemTemplate newCloseItem = closeItem.copy();
			if (!selectedTags.isEmpty()) {
				newCloseItem.setLore(adminMenu.tagDescription(new HashSet<>(selectedTags)));
			}
			this.addItem(newCloseItem, closeAction);
			return updateTagItemLore(item, selectedTags.contains(s), s, availableTags);
		});
		this.addItem(closeItem, closeAction);
	}

	ItemTemplate updateTagItemLore(ItemTemplate existingItem, boolean selected, String tag, Map<String, TaskTagData.TaskTag> allTags) {
		Component tagComponent = Component.text("<" + tag + ">").color(allTags.getOrDefault(tag, new TaskTagData.TaskTag(NamedTextColor.WHITE)).color());
		ItemTemplate newItem = existingItem.copy();
		if (selected) {
			newItem.setLore(Component.text("Tasks tagged with ")
							.append(tagComponent)
							.append(ComponentUtils.MINI_BUILDER.deserialize(" are <red>excluded</red> from this card")))
					.addDescription("input", 10, InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("Include tasks instead")));
		} else {
			newItem.setLore(Component.text("Tasks tagged with ")
							.append(tagComponent)
							.append(ComponentUtils.MINI_BUILDER.deserialize(" are <green>included</green> with this card")))
					.addDescription("input", 10, InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("Exclude tasks instead")));
		}
		return newItem;
	}
}
