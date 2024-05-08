package io.github.steaf23.easymenulib.menu.item.action;

import io.github.steaf23.easymenulib.menu.BasicMenu;

import java.util.List;
import java.util.function.Consumer;

public class ComboBoxButtonAction extends MenuAction
{
    private int selectedIndex;
    private final List<String> options;
    private final Consumer<String> callback;

    public ComboBoxButtonAction(List<String> options, Consumer<String> callback) {
        this.options = options;
        this.callback = callback;
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        if (arguments.clickType().isLeftClick()) {
            selectedIndex = Math.floorMod(selectedIndex + 1, options.size());
        }
        else if (arguments.clickType().isRightClick()) {
            selectedIndex = Math.floorMod(selectedIndex - 1, options.size());
        }

        callback.accept(options.get(selectedIndex));
    }
}
