package io.github.steaf23.bingoreloaded.lib.inventory.action;


import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.UserInputMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import net.kyori.adventure.text.Component;

import java.util.function.BiConsumer;

/**
 * Action to edit the name of an item by clicking on it. Supports minimessage serialization
 */
public class NameEditAction extends MenuAction
{
    private String value;
    private final BiConsumer<String, ItemTemplate> callback;
    private final MenuBoard board;
    private final Component prompt;

    public NameEditAction(Component prompt, MenuBoard board, BiConsumer<String, ItemTemplate> callback) {
        this(prompt, board, "", callback);
    }

    public NameEditAction(Component prompt, MenuBoard board, String startingValue, BiConsumer<String, ItemTemplate> callback) {
        this.callback = callback;
        this.board = board;
        this.prompt = prompt;
        this.value = startingValue;
    }

    @Override
    public void use(ActionArguments arguments) {
        renameItem(arguments.player());
    }

    protected void renameItem(PlayerHandle player) {
        new UserInputMenu(board, prompt, (result) -> {
            value = result;
            item.setName(ComponentUtils.MINI_BUILDER.deserialize(value));
            callback.accept(value, item);
        }, value)
                .open(player);
    }
}
