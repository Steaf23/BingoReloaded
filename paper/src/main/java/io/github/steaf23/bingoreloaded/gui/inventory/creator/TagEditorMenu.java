package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.ColorPickerMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.NameEditAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class TagEditorMenu extends PaginatedSelectionMenu
{
    private final TaskTagData tagData;

    private static final TaskTagData.TaskTag DEFAULT_NEW_TAG = new TaskTagData.TaskTag(TextColor.fromHexString("#808080"));
    private static final String DEFAULT_NEW_TAG_NAME = "my_tag";

    private static final ItemTemplate CREATE_TAG = new ItemTemplate(6, 5, ItemTypePaper.of(Material.EMERALD),
            Component.text("Create New Tag").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    public TagEditorMenu(MenuBoard manager, TaskTagData tagData) {
        super(manager, Component.text("Edit Tags"), new ArrayList<>(), FilterType.DISPLAY_NAME);
        this.tagData = tagData;

        addAction(CREATE_TAG, arguments -> createTagEditor(DEFAULT_NEW_TAG_NAME).open(arguments.player()));
    }

    public void updateDisplay() {
        clearFilter();
        clearItems();
        List<ItemTemplate> items = new ArrayList<>();

        var tagMap = tagData.getCustomTags();
        for (String key : tagMap.keySet()) {
            TaskTagData.TaskTag tag = tagMap.get(key);
            items.add(new ItemTemplate(ItemTypePaper.of(Material.NAME_TAG))
                    .setName(Component.text("<" + key + ">").color(tag.color()))
                    .setLore(Component.text("id: ").append(Component.text(key).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)))
                    .setCompareKey(key)
                    .addDescription("input", 5,
                            InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("edit tag")),
                            InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("remove tag"))));
        }
        addItemsToSelect(items);
    }

    public BasicMenu createTagEditor(@NotNull String tagKey) {
        return new TagEdit(getMenuBoard(), tagKey, DEFAULT_NEW_TAG, tagData::addTag);
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player) {
        String key = clickedOption.getCompareKey();
        if (event.getClick() == ClickType.RIGHT) {
            tagData.removeTag(key);
            updateDisplay();
        } else {
            createTagEditor(key).open(player);
        }
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        updateDisplay();
        super.beforeOpening(player);
    }

    static class TagEdit extends BasicMenu
    {
        private final BiConsumer<String, TaskTagData.TaskTag> finishedCallback;
        private String tagName;
        private TaskTagData.TaskTag tagToEdit;

        public TagEdit(MenuBoard manager, String name, TaskTagData.TaskTag tag, BiConsumer<String, TaskTagData.TaskTag> callback) {
            super(manager, Component.text("Edit tag"), 3);
            this.tagToEdit = tag;
            this.tagName = name;
            this.finishedCallback = callback;

            addAction(getTagNameAction());
            addAction(tagColorAction());

            addCloseAction(new ItemTemplate(6, 1, ItemTypePaper.of(Material.BARRIER),
                    BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        }

        private @NotNull MenuAction tagColorAction() {
            // Add action to change the team's color.
            ItemTemplate teamColorItem = new ItemTemplate(4, 1, ItemTypePaper.of(Material.LEATHER_CHESTPLATE), Component.text("Color").color(tagToEdit.color()).decorate(TextDecoration.BOLD))
                    .setLeatherColor(tagToEdit.color());

            MenuAction action = new MenuAction() {
                @Override
                public void use(ActionArguments arguments) {
                    new ColorPickerMenu(getMenuBoard(), Component.text("Pick tag color"), (result) -> {
                        // Update template
                        tagToEdit = new TaskTagData.TaskTag(result);

                        // Update menu item
                        teamColorItem.setLeatherColor(tagToEdit.color())
                                .setName(Component.text("Color").color(tagToEdit.color()).decorate(TextDecoration.BOLD));
                        addItem(teamColorItem);
                    }).open(arguments.player());
                }
            };

            action.setItem(teamColorItem);
            return action;
        }

        private @NotNull MenuAction getTagNameAction() {
            ItemTemplate teamNameItem = new ItemTemplate(2, 1, ItemTypePaper.of(Material.WRITABLE_BOOK),
                    Component.text(tagName));

            MenuAction action = new NameEditAction(Component.text("Edit tag name"), getMenuBoard(), tagName, (value, item) -> {
                tagName = value.replace(" ", "_").toLowerCase();
                addItem(item);
            });
            action.setItem(teamNameItem);
            return action;
        }

        @Override
        public void beforeClosing(PlayerHandle player) {
            super.beforeClosing(player);
            finishedCallback.accept(tagName, tagToEdit);
        }
    }
}
