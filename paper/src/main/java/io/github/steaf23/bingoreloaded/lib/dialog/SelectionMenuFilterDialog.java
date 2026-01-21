package io.github.steaf23.bingoreloaded.lib.dialog;

import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import io.github.steaf23.bingoreloaded.gui.inventory.core.FilterType;
import io.github.steaf23.bingoreloaded.gui.inventory.core.MenuFilterSettings;
import io.github.steaf23.bingoreloaded.gui.inventory.core.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.Consumer;

public class SelectionMenuFilterDialog extends DialogMenu {

	private static final Key ACCEPT_KEY = Key.key("playerdisplay", "filter_accept");
	private static final Key CANCEL_KEY = Key.key("playerdisplay", "filter_cancel");
	private final Consumer<MenuFilterSettings> callback;
	private final Dialog dialog;

	public SelectionMenuFilterDialog(MenuBoard menuBoard, List<FilterType> filterTypes, MenuFilterSettings initialSettings, Consumer<MenuFilterSettings> callback) {
		super(menuBoard);

		this.dialog = createFilterDialog(filterTypes, initialSettings);
		this.callback = callback;
	}

	@Override
	public Dialog getDialog() {
		return dialog;
	}

	@Override
	public void onCustomAction(Key key, DataStorage payload) {
		if (key.equals(ACCEPT_KEY)) {
			ConsoleMessenger.log("Received " + key.asMinimalString() + " and " + payload.getString("filter", ""));

			String filterTypeStr = payload.getString("filter_option", "NONE");

			try {
				var settings = new MenuFilterSettings(FilterType.valueOf(filterTypeStr),
						payload.getString("filter", ""));
				callback.accept(settings);
			} catch (IllegalArgumentException illegalFilterTypeException) {
				ConsoleMessenger.bug("Unknown filter type '" + filterTypeStr + "' from filter dialog", PaginatedSelectionMenu.class);
			}
		}
	}

	private static Dialog createFilterDialog(List<FilterType> availableFilterTypes, MenuFilterSettings currentSettings) {
		var filterTypes = new DialogBuilder.SingleOptionInputBuilder("filter_option", Component.text("Filter by"));
		for (var type : availableFilterTypes) {
			filterTypes.addOption(type.name(),Component.text(type.toString()), currentSettings.filterType() == type);
		}
		return new DialogBuilder(Component.text("Filter options"))
				.addMessageBody(Component.text("Filter options in the menu by the criteria listed below, or clear the currently applied filter."))
				.addSingleOptionInput(filterTypes)
				.addTextInput(new DialogBuilder.TextInputBuilder("filter", Component.text("Text to filter on:"))
						.initial(currentSettings.name()))
				.buildConfirmation(
						DialogBuilder.ActionButtonBuilder.dynamicCustomAction(Component.text("Accept new filter"),
										ACCEPT_KEY, null)
								.build(),
						DialogBuilder.ActionButtonBuilder.customAction(Component.text("Cancel"),
										CANCEL_KEY, null)
								.tooltip(Component.text("Filter will remain as {" + currentSettings.name() + "}"))
								.build()
				);
	}
}
