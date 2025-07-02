package io.github.steaf23.bingoreloaded.lib.dialog;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TextInputDialog extends DialogMenu {

	private final String initialValue;
	private final Dialog dialog;
	private final Consumer<String> callback;

	private static final Key INPUT_ACCEPT_KEY = Key.key("playerdisplay", "text_input_accept");

	private static final Component BUTTON_TITLE = PlayerDisplayTranslationKey.MENU_ACCEPT
			.translate()
			.color(NamedTextColor.GREEN)
			.decorate(TextDecoration.BOLD);

	public TextInputDialog(MenuBoard menuBoard, String initialValue, Consumer<String> callback, @Nullable Component label, @Nullable Component description) {
		super(menuBoard);

		DialogBuilder d = new DialogBuilder(Component.text("Player Text Input"));
		if (description != null) {
			d.addMessageBody(description);
		}
		this.dialog = d.addTextInput(new DialogBuilder.TextInputBuilder("input", label)
						.initial(initialValue))
				.buildNotice(DialogBuilder.ActionButtonBuilder.dynamicCustomAction(BUTTON_TITLE, INPUT_ACCEPT_KEY, null).build());
		this.callback = callback;
		this.initialValue = initialValue;
	}

	@Override
	public Dialog getDialog() {
		return dialog;
	}

	@Override
	public void onCustomAction(Key key, DataStorage payload) {
		if (key != INPUT_ACCEPT_KEY) {
			return;
		}

		callback.accept(payload.getString("input", initialValue));
	}
}
