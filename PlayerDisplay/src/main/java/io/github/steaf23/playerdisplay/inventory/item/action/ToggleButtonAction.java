package io.github.steaf23.playerdisplay.inventory.item.action;

import io.github.steaf23.playerdisplay.inventory.Menu;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
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
                Menu.INPUT_LEFT_CLICK.append(Component.text("toggle")));
    }

    @Override
    public void use(ActionArguments arguments) {
        enabled = !enabled;
        if (item != null) {
            item.setGlowing(enabled);
        }
        callback.accept(enabled);
    }
}
