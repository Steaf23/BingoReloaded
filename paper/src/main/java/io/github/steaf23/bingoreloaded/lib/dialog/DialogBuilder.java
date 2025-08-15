package io.github.steaf23.bingoreloaded.lib.dialog;


import com.github.retrooper.packetevents.protocol.chat.clickevent.ChangePageClickEvent;
import com.github.retrooper.packetevents.protocol.chat.clickevent.CopyToClipboardClickEvent;
import com.github.retrooper.packetevents.protocol.chat.clickevent.CustomClickEvent;
import com.github.retrooper.packetevents.protocol.chat.clickevent.OpenUrlClickEvent;
import com.github.retrooper.packetevents.protocol.chat.clickevent.RunCommandClickEvent;
import com.github.retrooper.packetevents.protocol.chat.clickevent.ShowDialogClickEvent;
import com.github.retrooper.packetevents.protocol.chat.clickevent.SuggestCommandClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import com.github.retrooper.packetevents.protocol.dialog.ConfirmationDialog;
import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.github.retrooper.packetevents.protocol.dialog.DialogListDialog;
import com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import com.github.retrooper.packetevents.protocol.dialog.NoticeDialog;
import com.github.retrooper.packetevents.protocol.dialog.ServerLinksDialog;
import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.DialogTemplate;
import com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import com.github.retrooper.packetevents.protocol.dialog.action.DynamicRunCommandAction;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.github.retrooper.packetevents.protocol.dialog.body.DialogBody;
import com.github.retrooper.packetevents.protocol.dialog.body.ItemDialogBody;
import com.github.retrooper.packetevents.protocol.dialog.body.PlainMessage;
import com.github.retrooper.packetevents.protocol.dialog.body.PlainMessageDialogBody;
import com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import com.github.retrooper.packetevents.protocol.dialog.input.BooleanInputControl;
import com.github.retrooper.packetevents.protocol.dialog.input.Input;
import com.github.retrooper.packetevents.protocol.dialog.input.NumberRangeInputControl;
import com.github.retrooper.packetevents.protocol.dialog.input.SingleOptionInputControl;
import com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.mapper.MappedEntityRefSet;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DialogBuilder {
	private final Component title;

	private @Nullable Component externalTitle = null;
	private boolean canCloseWithEscape = true;
	private boolean pause = true;
	private DialogAction afterAction = DialogAction.CLOSE;
	private final List<DialogBody> body = new ArrayList<>();
	private final List<Input> inputs = new ArrayList<>();

	public DialogBuilder(@NotNull Component title) {
		this.title = title;
	}

	public DialogBuilder externalTitle(@NotNull Component externalTitle) {
		this.externalTitle = externalTitle;
		return this;
	}

	public DialogBuilder canCloseWithEscape(boolean canCloseWithEscape) {
		this.canCloseWithEscape = canCloseWithEscape;
		return this;
	}

	public DialogBuilder pause(boolean pause) {
		this.pause = pause;
		return this;
	}

	public DialogBuilder afterAction(DialogAction afterAction) {
		this.afterAction = afterAction;
		return this;
	}

	public NoticeDialog buildNotice(ActionButton action) {
		return new NoticeDialog(buildCommonData(), action);
	}

	public ConfirmationDialog buildConfirmation(ActionButton yes, ActionButton no) {
		return new ConfirmationDialog(buildCommonData(), yes, no);
	}

	public MultiActionDialog buildMultiAction(ActionButton exitAction, int amountOfActionColumns, ActionButton... actions) {
		return new MultiActionDialog(buildCommonData(), List.of(actions), exitAction, amountOfActionColumns);
	}

	public ServerLinksDialog buildServerLinks(ActionButton exitAction, int numActionColumns, int buttonWidth) {
		return new ServerLinksDialog(buildCommonData(), exitAction, numActionColumns, buttonWidth);
	}

	public ServerLinksDialog buildServerLinks(ActionButton exitAction, int numActionColumns) {
		return buildServerLinks(exitAction, numActionColumns, 150);
	}

	public DialogListDialog buildDialogList(ActionButton exitAction, MappedEntityRefSet<@NotNull Dialog> dialogs, int numActionColumns, int buttonWidth) {
		return new DialogListDialog(buildCommonData(), dialogs, exitAction, numActionColumns, buttonWidth);
	}

	public DialogListDialog buildDialogList(ActionButton exitAction, MappedEntityRefSet<@NotNull Dialog> dialogs, int numActionColumns) {
		return buildDialogList(exitAction, dialogs, numActionColumns, 150);
	}

	protected CommonDialogData buildCommonData() {
		return new CommonDialogData(title, externalTitle, canCloseWithEscape, pause, afterAction, body, inputs);
	}

	public DialogBuilder addMessageBody(@NotNull Component contents, int width) {
		this.body.add(new PlainMessageDialogBody(new PlainMessage(contents, width)));

		return this;
	}

	public DialogBuilder addMessageBody(@NotNull Component contents) {
		return addMessageBody(contents, 200);
	}

	public DialogBuilder addItemBody(ItemTemplate item, ItemInfoBuilder builder) {
		this.body.add(builder.build(item));
		return this;
	}

	public DialogBuilder addItemBody(ItemDialogBody body) {
		this.body.add(body);
		return this;
	}

	public DialogBuilder addTextInput(String key, @Nullable Component label, int width, String initial, int maxLength, int maxLines, int lineHeight) {
		return addTextInput(new TextInputBuilder(key, label)
				.width(width)
				.initial(initial)
				.maxLength(maxLength)
				.multilineOptions(maxLines, lineHeight));
	}

	public DialogBuilder addTextInput(TextInputBuilder builder) {
		inputs.add(builder.build());
		return this;
	}

	public DialogBuilder addBooleanInput(String key, @NotNull Component label, boolean initial, String onFalse, String onTrue) {
		inputs.add(new Input(key, new BooleanInputControl(label, initial, onTrue, onFalse)));
		return this;
	}

	public DialogBuilder addBooleanInput(String key, @NotNull Component label, boolean initial) {
		inputs.add(new Input(key, new BooleanInputControl(label, initial, "true", "false")));
		return this;
	}

	public DialogBuilder addSingleOptionInput(String key, @Nullable Component label, @NotNull List<SingleOptionInputControl.Entry> options, int width) {
		return addSingleOptionInput(new SingleOptionInputBuilder(key, label)
				.width(width)
				.options(options));
	}

	public DialogBuilder addSingleOptionInput(SingleOptionInputBuilder builder) {
		inputs.add(builder.build());
		return this;
	}

	public DialogBuilder addNumberRangeInput(String key, @NotNull Component label, float rangeStart, float rangeEnd, int width, int initial, @Nullable String labelFormat, float step) {
		return addNumberRangeInput(new NumberRangeInputBuilder(key, label, rangeStart, rangeEnd)
				.width(width)
				.initial((float)initial)
				.labelFormat(labelFormat)
				.step(step));
	}

	public DialogBuilder addNumberRangeInput(NumberRangeInputBuilder builder) {
		inputs.add(builder.build());
		return this;
	}

	public static class ItemInfoBuilder {
		private PlainMessage description;
		private boolean showDecoration = true;
		private boolean showTooltip = true;
		private int width = 16;
		private int height = 16;

		public ItemInfoBuilder description(@NotNull Component component, int width) {
			this.description = new PlainMessage(component, width);
			return this;
		}

		public ItemInfoBuilder description(@NotNull Component component) {
			return description(component, 200);
		}

		public ItemInfoBuilder showDecoration(boolean showDecoration) {
			this.showDecoration = showDecoration;
			return this;
		}

		public ItemInfoBuilder showTooltip(boolean showTooltip) {
			this.showTooltip = showTooltip;
			return this;
		}

		public ItemInfoBuilder width(int width) {
			this.width = width;
			return this;
		}

		public ItemInfoBuilder height(int height) {
			this.height = height;
			return this;
		}

		public ItemDialogBody build(ItemTemplate item) {
			return new ItemDialogBody(new ItemStack.Builder().type(ItemTypes.DIRT).build(), description, showDecoration, showTooltip, width, height);
		}
	}

	public static class TextInputBuilder {
		private final String key;
		private final @Nullable Component label;

		private int width = 200;
		private String initial = "";
		private int maxLength = 32;
		private boolean hasMultilineOptions = false;
		private int maxLines = -1;
		private int lineHeight = -1; //TODO: this value is probably wrong...

		public TextInputBuilder(String key, @Nullable Component label) {
			this.key = key;
			this.label = label;
		}

		public TextInputBuilder width(int width) {
			this.width = width;
			return this;
		}

		public TextInputBuilder initial(String initial) {
			this.initial = initial;
			return this;
		}

		public TextInputBuilder maxLength(int maxLength) {
			this.maxLength = maxLength;
			return this;
		}

		public TextInputBuilder multilineOptions(int maxLines, int lineHeight) {
			this.maxLines = maxLines;
			this.lineHeight = lineHeight;
			this.hasMultilineOptions = true;
			return this;
		}

		/**
		 * @return Newly created textInputControl based on built settings
		 */
		public Input build() {
			return new Input(key, new TextInputControl(
					width,
					label != null ? label : Component.text(""),
					label != null,
					initial,
					maxLength,
					hasMultilineOptions ? new TextInputControl.MultilineOptions(maxLines, lineHeight) : null));
		}
	}

	public static class SingleOptionInputBuilder {
		private final String key;
		private final @Nullable Component label;

		private List<SingleOptionInputControl.Entry> options = new ArrayList<>();
		private int width = 200;

		public SingleOptionInputBuilder(String key, @Nullable Component label) {
			this.key = key;
			this.label = label;
		}

		public SingleOptionInputBuilder width(int width) {
			this.width = width;
			return this;
		}

		public SingleOptionInputBuilder options(List<SingleOptionInputControl.Entry> options) {
			this.options = options;
			return this;
		}

		public SingleOptionInputBuilder addOption(String id, @NotNull Component display, boolean initial) {
			this.options.add(new SingleOptionInputControl.Entry(id, display, initial));
			return this;
		}

		public Input build() {
			return new Input(key, new SingleOptionInputControl(
					width,
					options,
					label != null ? label : Component.text(""),
					label != null));
		}
	}

	public static class NumberRangeInputBuilder {
		private final String key;
		private final Component label;
		private final float rangeStart;
		private final float rangeEnd;

		private int width = 200;
		private Float step = null;
		private Float initial = null;
		private String labelFormatKey = "options.generic_value";

		public NumberRangeInputBuilder(String key, @NotNull Component label, float rangeStart, float rangeEnd) {
			this.key = key;
			this.label = label;
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		public NumberRangeInputBuilder width(int width) {
			this.width = width;
			return this;
		}

		public NumberRangeInputBuilder step(Float step) {
			this.step = step;
			return this;
		}

		public NumberRangeInputBuilder initial(Float initial) {
			this.initial = initial;
			return this;
		}

		public NumberRangeInputBuilder labelFormat(String labelFormatKey) {
			this.labelFormatKey = labelFormatKey;
			return this;
		}

		public Input build() {
			return new Input(key, new NumberRangeInputControl(
					width,
					label,
					labelFormatKey,
					new NumberRangeInputControl.RangeInfo(rangeStart, rangeEnd, initial, step)));
		}
	}

	public static class ActionButtonBuilder {
		private final @NotNull Component label;
		private final @NotNull Action action;

		private @Nullable Component tooltip = null;
		private int width = 150;

		private ActionButtonBuilder(@NotNull Component label, @NotNull Action action) {
			this.label = label;
			this.action = action;
		}

		public static ActionButtonBuilder openUrlAction(@NotNull Component label, String url) {
			return new ActionButtonBuilder(label, new StaticAction(new OpenUrlClickEvent(url)));
		}

		public static ActionButtonBuilder runCommandAction(@NotNull Component label, String command) {
			return new ActionButtonBuilder(label, new StaticAction(new RunCommandClickEvent(command)));
		}

		public static ActionButtonBuilder suggestCommandAction(@NotNull Component label, String command) {
			return new ActionButtonBuilder(label, new StaticAction(new SuggestCommandClickEvent(command)));
		}

		/**
		 * Only works when inside a written book.
		 */
		public static ActionButtonBuilder changePageAction(@NotNull Component label, int newPage) {
			return new ActionButtonBuilder(label, new StaticAction(new ChangePageClickEvent(newPage)));
		}

		public static ActionButtonBuilder copyToClipboardAction(@NotNull Component label, String textToCopy) {
			return new ActionButtonBuilder(label, new StaticAction(new CopyToClipboardClickEvent(textToCopy)));
		}

		public static ActionButtonBuilder showDialogAction(@NotNull Component label, Dialog dialog) {
			return new ActionButtonBuilder(label, new StaticAction(new ShowDialogClickEvent(dialog)));
		}

		public static ActionButtonBuilder customAction(@NotNull Component label, Key id, @Nullable NBT payload) {
			return new ActionButtonBuilder(label, new StaticAction(new CustomClickEvent(new ResourceLocation(id), payload)));
		}

		public static ActionButtonBuilder dynamicRunCommandAction(@NotNull Component label, String commandTemplate) {
			return new ActionButtonBuilder(label, new DynamicRunCommandAction(new DialogTemplate(commandTemplate)));
		}

		public static ActionButtonBuilder dynamicCustomAction(@NotNull Component label, Key id, @Nullable NBTCompound additionalPayload) {
			return new ActionButtonBuilder(label, new DynamicCustomAction(new ResourceLocation(id), additionalPayload));
		}

		public ActionButtonBuilder tooltip(@Nullable Component tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public ActionButtonBuilder width(int width) {
			this.width = width;
			return this;
		}

		public ActionButton build() {
			return new ActionButton(new CommonButtonData(label, tooltip, width), action);
		}
	}

}
