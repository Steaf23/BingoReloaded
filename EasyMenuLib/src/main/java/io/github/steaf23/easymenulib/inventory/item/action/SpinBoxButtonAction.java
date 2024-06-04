package io.github.steaf23.easymenulib.inventory.item.action;

import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.Menu;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ExtraMath;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SpinBoxButtonAction extends MenuAction
{
    private int value;
    private final int min;
    private final int max;
    private final Consumer<Integer> callback;

    public SpinBoxButtonAction(int minValue, int maxValue, int initialValue, Consumer<Integer> callback) {
        this.min = ExtraMath.clamped(minValue, 1, maxValue);
        this.max = ExtraMath.clamped(maxValue, minValue, 64);
        this.value = ExtraMath.clamped(initialValue, min, max);
        this.callback = callback;
    }

    @Override
    public void setItem(@NotNull ItemTemplate item) {
        super.setItem(item);
        item.setAmount(value);

        item.addDescription("input", 10,
                Menu.INPUT_LEFT_CLICK + "increase",
                Menu.INPUT_RIGHT_CLICK + "decrease",
                Menu.INPUT_SHIFT_CLICK + "edit faster");
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        int changeBy = 1;
        if (arguments.clickType().isShiftClick()) {
            changeBy = 10;
        }

        if (arguments.clickType().isRightClick()) {
            changeBy *= -1;
        }

        value = ExtraMath.clamped(value + changeBy, min, max);
        if (item != null) {
            item.setAmount(value);
        }
        callback.accept(value);
    }
}
