package io.github.steaf23.bingoreloaded.lib.item.action;

import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SpinBoxButtonAction extends MenuAction
{
    private int value;
    private int min;
    private int max;
    private Consumer<Integer> callback;

    public SpinBoxButtonAction(int minValue, int maxValue, int initialValue, @NotNull Consumer<Integer> callback) {
        maxValue = Math.min(maxValue, 64);
        this.min = Math.clamp(minValue, 1, maxValue);
        this.max = Math.clamp(maxValue, minValue, 64);
        this.value = Math.clamp(initialValue, min, max);
        this.callback = callback;
    }

    public SpinBoxButtonAction(int minValue, int maxValue, int initialValue) {
        maxValue = Math.min(maxValue, 64);
        this.min = Math.clamp(minValue, 1, maxValue);
        this.max = Math.clamp(maxValue, minValue, 64);
        this.value = Math.clamp(initialValue, min, max);
        this.callback = null;
    }

    @Override
    public void setItem(@NotNull ItemTemplate item) {
        super.setItem(item);
        item.setAmount(value);

        item.addDescription("input", 10,
                InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("increase")),
                InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("decrease")),
                InventoryMenu.INPUT_SHIFT_CLICK.append(Component.text("edit faster")));
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
        updateItem();
    }

    public int getValue() {
        return value;
    }

    public void setCallback(@NotNull Consumer<Integer> callback) {
        this.callback = callback;
    }

    public void setMax(int max) {
        this.max = Math.clamp(max, min, 64);

        int newValue = Math.clamp(value, this.min, this.max);
        if (newValue != value) {
            this.value = newValue;
            updateItem();
        }
    }

    public void setMin(int min) {
        this.min = Math.clamp(min, 1, max);

        int newValue = Math.clamp(value, this.min, this.max);
        if (newValue != value) {
            this.value = newValue;
            updateItem();
        }
    }

    private void updateItem() {
        if (item != null) {
            item.setAmount(value);
        }
        if (callback != null) {
            callback.accept(value);
        }
    }
}
