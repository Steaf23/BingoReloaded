package io.github.steaf23.easymenulib.menu.item.action;

import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.Menu;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class ToggleButtonAction extends MenuAction
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
    public void setItem(@NotNull MenuItem item) {
        super.setItem(item);
        item.setGlowing(enabled);

        item.addDescription("input", 10,
                Menu.inputButtonText("Left Click") + "increase",
                Menu.inputButtonText("Right Click") + "decrease",
                Menu.inputButtonText("Hold Shift") + "edit faster");
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        enabled = !enabled;
        if (item != null) {
            item.setGlowing(enabled);
        }
        callback.accept(enabled);
    }
}
