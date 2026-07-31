package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.ColorPickerMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.NameEditAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TagEditorMenu extends PaginatedDataMenu.TextDataMenu
{
    private final TaskTagData tagData;

    private Map<String, TaskTagData.TaskTag> tagCache = new HashMap<>();

    private static final TaskTagData.TaskTag DEFAULT_NEW_TAG = new TaskTagData.TaskTag(TextColor.fromHexString("#808080"));
    private static final String DEFAULT_NEW_TAG_NAME = "my_tag";

    private static final ItemTemplate CREATE_TAG = new ItemTemplate(6, 5, VanillaItems.EMERALD.type(),
            Component.text("Create New Tag").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    public TagEditorMenu(MenuBoard manager, TaskTagData tagData) {
        super(manager, Component.text("Edit Tags"), new ArrayList<>());
        this.tagData = tagData;

        addAction(CREATE_TAG, arguments -> createTagEditor(DEFAULT_NEW_TAG_NAME).open(arguments.player()));
    }

    public void updateDisplay() {
        tagCache = tagData.getCustomTags();
        setData(tagCache.keySet());
    }

    public BasicMenu createTagEditor(@NotNull String tagKey) {
        return new TagEdit(getMenuBoard(), tagKey, DEFAULT_NEW_TAG, tagData::addTag);
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        updateDisplay();
        super.beforeOpening(player);
    }

    @Override
    public void onOptionClickedDelegate(MenuAction.ActionArguments args, String clickedOption) {
        if (args.isRightClick()) {
            tagData.removeTag(clickedOption);
            updateDisplay();
        } else {
            createTagEditor(clickedOption).open(args.player());
        }
    }

    @Override
    public ItemType itemType(String s, boolean selected) {
        return VanillaItems.NAME_TAG.type();
    }

    @Override
    public Component displayName(String key, boolean selected) {
        TaskTagData.TaskTag tag = tagCache.get(key);
        return Component.text("<" + key + ">").color(tag.color());
    }

    @Override
    public ItemTemplate editItem(ItemTemplate item, String key, boolean selected) {
        return item.setLore(Component.text("id: ").append(Component.text(key).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)))
                .addDescription("input", 5,
                        Menu.INPUT_LEFT_CLICK.append(Component.text("edit tag")),
                        Menu.INPUT_RIGHT_CLICK.append(Component.text("remove tag")));
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

            addCloseAction(new ItemTemplate(6, 1, VanillaItems.BARRIER.type(),
                    BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        }

        private @NotNull MenuAction tagColorAction() {
            // Add action to change the team's color.
            ItemTemplate teamColorItem = new ItemTemplate(4, 1, VanillaItems.LEATHER_CHESTPLATE.type(), Component.text("Color").color(tagToEdit.color()).decorate(TextDecoration.BOLD))
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
            ItemTemplate teamNameItem = new ItemTemplate(2, 1, VanillaItems.WRITABLE_BOOK.type(),
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
