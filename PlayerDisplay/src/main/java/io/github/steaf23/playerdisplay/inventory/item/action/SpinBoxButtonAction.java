package io.github.steaf23.playerdisplay.inventory.item.action;

import io.github.steaf23.playerdisplay.inventory.Menu;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SpinBoxButtonAction extends MenuAction
{
    private int value;
    private final int min;
    private final int max;
    private final Consumer<Integer> callback;

    public SpinBoxButtonAction(int minValue, int maxValue, int initialValue, Consumer<Integer> callback) {
        this.min = Math.clamp(minValue, 1, maxValue);
        this.max = Math.clamp(maxValue, minValue, 64);
        this.value = Math.clamp(initialValue, min, max);
        this.callback = callback;
    }

    @Override
    public void setItem(@NotNull ItemTemplate item) {
        super.setItem(item);
        item.setAmount(value);

        item.addDescription("input", 10,
                Menu.INPUT_LEFT_CLICK.append(Component.text("increase")),
                Menu.INPUT_RIGHT_CLICK.append(Component.text("decrease")),
                Menu.INPUT_SHIFT_CLICK.append(Component.text("edit faster")));
    }

    @Override
    public void use(ActionArguments arguments) {
        int changeBy = 1;
        if (arguments.clickType().isShiftClick()) {
            changeBy = 10;
        }

        if (arguments.clickType().isRightClick()) {
            changeBy *= -1;
        }

        value = Math.clamp(value + changeBy, min, max);
        if (item != null) {
            item.setAmount(value);
        }
        callback.accept(value);
    }

    public int getValue() {
        return value;
    }
}
