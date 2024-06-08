package io.github.steaf23.easymenulib.inventory.item.action;


import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.UserInputMenu;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.HumanEntity;

import java.util.function.BiConsumer;

public class NameEditAction extends MenuAction
{
    private String value;
    private final BiConsumer<String, ItemTemplate> callback;
    private final MenuBoard board;
    private final String prompt;

    public NameEditAction(String prompt, MenuBoard board, BiConsumer<String, ItemTemplate> callback) {
        this.callback = callback;
        this.board = board;
        this.prompt = prompt;
        this.value = "";
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        renameItem("", arguments.player());
    }

    protected void renameItem(String format, HumanEntity player) {
        new UserInputMenu(board, prompt, (result) -> {
            value = result;
            item.setName(TextComponent.fromLegacyText(format + value));
            callback.accept(value, item);
        }, player, value);
    }
}
