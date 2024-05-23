package io.github.steaf23.easymenulib.inventory.item.action;

import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.Menu;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
    public void setItem(@NotNull ItemTemplate item) {
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
