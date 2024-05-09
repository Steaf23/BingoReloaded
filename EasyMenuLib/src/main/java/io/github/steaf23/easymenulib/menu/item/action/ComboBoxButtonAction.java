package io.github.steaf23.easymenulib.menu.item.action;

import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ComboBoxButtonAction extends MenuAction
{
    private int selectedIndex;
    private final Consumer<String> callback;
    private final List<String> options;
    private final List<ItemStack> stacks;

    public ComboBoxButtonAction(Consumer<String> callback) {
        this.callback = callback;
        this.options = new ArrayList<>();
        this.stacks = new ArrayList<>();
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        if (arguments.clickType().isLeftClick()) {
            selectedIndex = Math.floorMod(selectedIndex + 1, options.size());
        }
        else if (arguments.clickType().isRightClick()) {
            selectedIndex = Math.floorMod(selectedIndex - 1, options.size());
        }

        item.replaceStack(stacks.get(selectedIndex));
        callback.accept(options.get(selectedIndex));
    }

    public ComboBoxButtonAction addOption(String name, ItemStack stack) {
        options.add(name);
        stacks.add(stack);
        return this;
    }

    /**
     * Selects an option without calling the callback. Useful when creating this action.
     * @param option
     */
    public ComboBoxButtonAction selectOption(String option) {
        if (!options.contains(option)) {
            return this;
        }

        int index = options.indexOf(option);
        selectedIndex = index;
        return this;
    }

    @Override
    public void setItem(@NotNull MenuItem item) {
        super.setItem(item);
        item.replaceStack(stacks.get(selectedIndex));
    }
}
