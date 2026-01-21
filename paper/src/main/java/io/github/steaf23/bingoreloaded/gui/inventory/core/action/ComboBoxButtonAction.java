package io.github.steaf23.bingoreloaded.gui.inventory.core.action;

import io.github.steaf23.bingoreloaded.gui.inventory.core.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.core.Menu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Action representing multiple options that can be clicked through. Create this class by using the builder
 */
public class ComboBoxButtonAction extends MenuAction
{
    static public class Builder
    {
        record ItemOption(String key, ItemTemplate item) {}

        private final List<ItemOption> options;
        private ComboBoxCallback callback;

        public Builder(@NotNull String key, @NotNull ItemTemplate item) {
            this.options = new ArrayList<>();
            this.callback = (old, current, args) -> true;
            addOption(key, item);
        }

        public Builder setCallback(@NotNull ComboBoxCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder addOption(@NotNull String key, @NotNull ItemTemplate item) {
            options.add(new ItemOption(key, item));
            return this;
        }

        /**
         * @param startingKey item with this key will be shown when the player sees this combo box for the first time.
         * @return built item template that can be added to a menu. Sets selected index to the item at startingKey.
         * If no valid option can be found for the given key, the first option added is used as a key.
         */
        public ComboBoxButtonAction buildAction(int slot, String startingKey) {
            int startingIndex = 0;
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).key().equals(startingKey)) {
                    startingIndex = i;
                    break;
                }
            }
            return buildAction(slot, startingIndex);
        }

        /**
         * @return built item template that can be added to a menu. Sets selected index to the item at startingKey.
         * If no valid option can be found for the given key, the first option added is used as a key.
         */
        public ComboBoxButtonAction buildAction(int slot) {
            return buildAction(slot, 0);
        }

        public ComboBoxButtonAction buildAction(int slot, int startingIndex) {
            if (startingIndex < 0 || startingIndex >= options.size()) {
                startingIndex = 0;
            }

            ItemTemplate result = options.get(startingIndex).item().setCompareKey(options.get(startingIndex).key()).copyToSlot(slot);
            ComboBoxButtonAction action = new ComboBoxButtonAction(callback);
            for (ItemOption option : options) {
                action.addOption(option.key(), option.item().copyToSlot(slot));
            }
            action.setItem(result);
            return action;
        }
    }

    private final ComboBoxCallback callback;
    private final List<String> options;
    private final List<ItemTemplate> optionItem;

    private ComboBoxButtonAction(ComboBoxCallback callback) {
        this.callback = callback;
        this.options = new ArrayList<>();
        this.optionItem = new ArrayList<>();
    }

    @Override
    public void use(ActionArguments arguments) {
        Menu menu = arguments.menu();
        if (!(menu instanceof BasicMenu basicMenu)) {
            return;
        }

        int newIndex = getSelectedOptionIndex();
        if (arguments.clickType().isLeftClick()) {
            newIndex = Math.floorMod(newIndex + 1, options.size());
        }
        else if (arguments.clickType().isRightClick()) {
            newIndex = Math.floorMod(newIndex - 1, options.size());
        }

        // schedule item change, because after the action the item is reset automatically
        final int finalNewIndex = newIndex;
        Bukkit.getScheduler().runTask(menu.getMenuBoard().plugin(), task -> {
			ItemTemplate newItem = optionItem.get(finalNewIndex);
			boolean shouldUpdate = callback.shouldUpdateItem(getSelectedOptionName(), newItem.getCompareKey(), arguments);
            if (!shouldUpdate) {
                return;
            }
            setItem(newItem);
            // This will also set the 'item' to the new value
            basicMenu.addAction(this);
        });
    }

    public int getSelectedOptionIndex() {
        return options.indexOf(getSelectedOptionName());
    }

    public String getSelectedOptionName() {
        return item.getCompareKey();
    }

    public ComboBoxButtonAction addOption(String name, ItemTemplate data) {
        options.add(name);
        optionItem.add(data);
        data.setCompareKey(name);
        return this;
    }

	@FunctionalInterface
	public interface ComboBoxCallback {
		boolean shouldUpdateItem(String currentValue, String newValue, ActionArguments arguments);
	}
}
