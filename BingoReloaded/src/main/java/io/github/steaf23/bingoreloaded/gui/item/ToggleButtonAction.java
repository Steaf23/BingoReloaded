package io.github.steaf23.bingoreloaded.gui.item;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuAction;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;

import java.util.function.Consumer;

public class ToggleButtonAction implements MenuAction
{
    private boolean enabled;
    private final Consumer<Boolean> callback;

    public ToggleButtonAction(Consumer<Boolean> callback) {
        this(false, callback);
    }

    public ToggleButtonAction(boolean startEnabled, Consumer<Boolean> callback) {
        this.enabled = startEnabled;
        this.callback = callback;
    }

    @Override
    public void use(MenuItem item, BasicMenu.ActionArguments arguments) {
        enabled = !enabled;
        item.setGlowing(enabled);
        callback.accept(enabled);
    }
}
