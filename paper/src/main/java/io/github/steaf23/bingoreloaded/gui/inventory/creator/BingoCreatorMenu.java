package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.gui.inventory.TagExclusionMenu;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.UserInputMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class BingoCreatorMenu extends BasicMenu {

	private final BingoCardData cardsData;
	private final TaskFormatting formatting;
	public static final ItemTemplate CARD = new ItemTemplate(1, 1, ItemTypePaper.of(Material.FILLED_MAP), BingoReloaded.applyTitleFormat("Edit Cards"), Component.text("Click to view and edit bingo cards!"));
	public static final ItemTemplate LIST = new ItemTemplate(4, 1, ItemTypePaper.of(Material.PAPER), BingoReloaded.applyTitleFormat("Edit Lists"), Component.text("Click to view and edit bingo lists!"));
	public static final ItemTemplate TAGS = new ItemTemplate(7, 1, ItemTypePaper.of(Material.NAME_TAG), BingoReloaded.applyTitleFormat("Edit Tags"), Component.text("Click to view and edit task tags!"));

	private static final ItemType REMOVE_ICON = ItemTypePaper.of(Material.BARRIER);
	private static final ItemType COPY_ICON = ItemTypePaper.of(Material.SHULKER_SHELL);
	private static final ItemType RENAME_ICON = ItemTypePaper.of(Material.WRITABLE_BOOK);
	private static final ItemType TAGS_ICON = ItemTypePaper.of(Material.NAME_TAG);
	private static final ItemType SAVE_ICON = ItemTypePaper.of(Material.DIAMOND);

	public BingoCreatorMenu(MenuBoard manager) {
		super(manager, Component.text("Card Creator"), 3);
		this.cardsData = new BingoCardData();
		this.formatting = TaskFormatting.fromDataAccessor();
		addAction(CARD, arguments -> createCardPicker().open(arguments.player()));
		addAction(LIST, arguments -> createListPicker().open(arguments.player()));
		addAction(TAGS, arguments -> createTagPicker().open(arguments.player()));
	}

	private BasicMenu createCardPicker() {
		return new PaginatedDataMenu.TextDataMenu(getMenuBoard(), Component.text("Choose A Card"), cardsData.getCardNames()) {
			@Override
			public void onOptionClickedDelegate(MenuAction.ActionArguments args, String clickedOption) {
				if (args.isLeftClick() && !cardsData.isDefaultCard(clickedOption)) {
					openCardEditor(clickedOption, args.player());
				} else if (args.isRightClick()) {
					createCardContext(clickedOption).open(args.player());
				}
			}

			@Override
			public Material material(String cardName, boolean selected) {
				return Material.FLOWER_BANNER_PATTERN;
			}

			@Override
			public Component displayName(String cardName, boolean selected) {
				return Component.text(cardName);
			}

			@Override
			public ItemTemplate editItem(ItemTemplate item, String cardName, boolean selected) {
				List<Component> fullDescription = new ArrayList<>();
				fullDescription.add(Component.text("Description: "));
				fullDescription.addAll(Arrays.stream(BingoMessage.configStringAsMultiline(cardsData.getDescription(cardName), Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC))).toList());

				List<String> excludedTags = cardsData.excludedTags(cardName);
				if (!excludedTags.isEmpty()) {
					item.setLore(Component.text("This card contains " + cardsData.getListNames(cardName).size() + " list(s)"),
									cardsData.tags().tagDescription(excludedTags))
							.addDescription("description", 1, fullDescription);
				} else {
					item.setLore(Component.text("This card contains " + cardsData.getListNames(cardName).size() + " list(s)"))
							.addDescription("description", 1, fullDescription);
				}

				if (cardsData.isDefaultCard(cardName)) {
					item.addDescription("input", 5,
							Component.text("Cannot edit default card, use right click to duplicate them instead!").color(NamedTextColor.RED),
							InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")));
				} else {
					item.addDescription("input", 5,
							InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("edit distribution")),
							InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")));
				}
				return item;
			}

			private static final ItemTemplate CREATE_CARD = new ItemTemplate(6, 5, ItemTypePaper.of(Material.EMERALD),
					Component.text("New Card").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

			@Override
			public void beforeOpening(PlayerHandle player) {
				addAction(CREATE_CARD, args -> createCard(args.player()));
				setData(cardsData.getCardNames());
			}
		};
	}

	private BasicMenu createListPicker() {
		return new PaginatedDataMenu.TextDataMenu(getMenuBoard(), Component.text("Choose A List"), cardsData.lists().getListNames()) {
			@Override
			public void onOptionClickedDelegate(MenuAction.ActionArguments event, String clickedOption) {
				if (event.isLeftClick() && !TaskListData.DEFAULT_LIST_NAMES.contains(clickedOption)) {
					openListEditor(clickedOption, event.player());
				} else if (event.isRightClick()) {
					createListContext(clickedOption).open(event.player());
				}
			}

			@Override
			public Material material(String listName, boolean selected) {
				return Material.BOOK;
			}

			@Override
			public Component displayName(String listName, boolean selected) {
				return Component.text(listName);
			}

			@Override
			public ItemTemplate editItem(ItemTemplate item, String listName, boolean selected) {
				item.setLore(Component.text("This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)"));

				if (TaskListData.DEFAULT_LIST_NAMES.contains(listName)) {
					item.addDescription("input", 5,
							Component.text("Cannot edit default list, use right click to duplicate them instead!").color(NamedTextColor.RED),
							InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options"))
					);
				} else {
					item.addDescription("input", 5,
							InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("edit tasks")),
							InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")));
				}
				return item;
			}

			private static final ItemTemplate CREATE_LIST = new ItemTemplate(6, 5, ItemTypePaper.of(Material.EMERALD),
					Component.text("New List").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

			@Override
			public void beforeOpening(PlayerHandle player) {
				addAction(CREATE_LIST, args -> createList(args.player()));
				setData(cardsData.lists().getListNames());
			}
		};
	}

	public BasicMenu createTagPicker() {
		return new TagEditorMenu(getMenuBoard(), cardsData.tags());
	}

	public void createCard(PlayerHandle player) {
		editCardMenu(this, player, "name", "", this::createCardCallback);
	}

	public void createList(PlayerHandle player) {
		new UserInputMenu(getMenuBoard(), Component.text("Enter new list name"), (input) -> {
			if (!input.isEmpty())
				openListEditor(input, player);
		}, "name")
				.open(player);
	}

	private void openCardEditor(String cardName, PlayerHandle player) {
		if (cardsData.isDefaultCard(cardName)) {
			BingoPlayerSender.sendMessage(Component.text("Cannot edit default card, use right click to duplicate them instead!").color(NamedTextColor.RED), player);
			return;
		}
		CardEditorMenu editor = new CardEditorMenu(getMenuBoard(), cardName, cardsData);
		editor.open(player);
	}

	private void openListEditor(String listName, PlayerHandle player) {
		if (TaskListData.DEFAULT_LIST_NAMES.contains(listName)) {
			BingoPlayerSender.sendMessage(Component.text("Cannot edit default lists, use right click to duplicate them instead!").color(NamedTextColor.RED), player);
			return;
		}
		ListEditorMenu editor = new ListEditorMenu(getMenuBoard(), listName, formatting);
		editor.open(player);
	}


	public BasicMenu createCardContext(String cardName) {
		BasicMenu context = new BasicMenu(getMenuBoard(), Component.text(cardName), 1);

		int slot = 0;
		if (!cardsData.isDefaultCard(cardName)) {
			context.addAction(new ItemTemplate(slot, REMOVE_ICON, BingoReloaded.applyTitleFormat("Remove")), (args) -> {
				cardsData.removeCard(cardName);
				context.close(args.player());
			});
			slot++;
		}

		context.addAction(new ItemTemplate(slot, COPY_ICON, BingoReloaded.applyTitleFormat("Duplicate")), (args) -> {
			cardsData.duplicateCard(cardName);
			context.close(args.player());
		});
		slot++;

		if (!cardsData.isDefaultCard(cardName)) {
			context.addAction(new ItemTemplate(slot, RENAME_ICON, BingoReloaded.applyTitleFormat("Change Description")), (args) -> {
				editCardMenu(context, args.player(), cardName, cardsData.getDescription(cardName), this::renameCard);
			});
			slot++;

			context.addAction(new ItemTemplate(slot, TAGS_ICON, BingoReloaded.applyTitleFormat("Exclude certain tags")), (args) -> {
				new TagExclusionMenu(getMenuBoard(), cardsData, cardName).open(args.player());
			});
			slot++;
		}

		context.addCloseAction(new ItemTemplate(8, SAVE_ICON, BingoReloaded.applyTitleFormat(BingoMessage.MENU_EXIT.asPhrase())));
		return context;
	}

	public BasicMenu createListContext(String listName) {
		TaskListData listsData = cardsData.lists();

		BasicMenu context = new BasicMenu(getMenuBoard(), Component.text(listName), 1);

		int slot = 0;
		if (!TaskListData.DEFAULT_LIST_NAMES.contains(listName)) {
			context.addAction(new ItemTemplate(slot, REMOVE_ICON, BingoReloaded.applyTitleFormat("Remove")), (args) -> {
				listsData.removeList(listName);
				context.close(args.player());
			});
			slot++;
		}

		context.addAction(new ItemTemplate(slot, COPY_ICON, BingoReloaded.applyTitleFormat("Duplicate")), (args) -> {
			listsData.duplicateList(listName);
			context.close(args.player());
		});
		slot++;

		if (!TaskListData.DEFAULT_LIST_NAMES.contains(listName)) {
			context.addAction(new ItemTemplate(slot, RENAME_ICON, BingoReloaded.applyTitleFormat("Change Name")), (args) -> {
				new UserInputMenu(getMenuBoard(), Component.text("Change name to"), (input) -> {
					listsData.renameList(listName, input);
					context.close(args.player());
				}, listName)
						.open(args.player());
			});
			slot++;
		}
		context.addCloseAction(new ItemTemplate(8, SAVE_ICON, BingoReloaded.applyTitleFormat(BingoMessage.MENU_EXIT.asPhrase())));
		return context;
	}

	@FunctionalInterface
	interface CardDescriptionEditor {

		void edit(PlayerHandle player, BasicMenu parentMenu, String oldName, String newName, String newDescription);
	}

	@SuppressWarnings("UnstableApiUsage")
	void editCardMenu(BasicMenu parentMenu, PlayerHandle playerHandle, String currentName, String currentDescription, CardDescriptionEditor callback) {
		Dialog dialog = Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text("Change card name/ description"))
						.inputs(List.of(
										DialogInput.text("name", Component.text("Name"))
												.initial(currentName)
												.build(),
										DialogInput.text("description", Component.text("Description"))
												.initial(currentDescription)
												.multiline(TextDialogInput.MultilineOptions.create(4, 100))
												.maxLength(300)
												.build()
								)
						)
						.build()
				).type(DialogType.confirmation(
						ActionButton.create(
								Component.translatable("selectWorld.edit.save"),
								Component.empty(),
								100,
								DialogAction.customClick((view, audience) -> {
									callback.edit(playerHandle, parentMenu, currentName, view.getText("name"), view.getText("description"));
								}, ClickCallback.Options.builder().build())
						),
						ActionButton.create(
								Component.translatable("gui.cancel"),
								Component.empty(),
								100,
								null
						)
				))
		);

		((PlayerHandlePaper) playerHandle).handle().showDialog(dialog);
	}

	public void renameCard(PlayerHandle player, BasicMenu parentMenu, String oldName, String newName, String description) {
		cardsData.setDescription(oldName, description);
		cardsData.renameCard(oldName, newName);
		parentMenu.close(player);
	}

	public void createCardCallback(PlayerHandle player, BasicMenu parentMenu, String oldName, String newName, String description) {
		cardsData.setDescription(newName, description);
		openCardEditor(newName, player);
	}
}