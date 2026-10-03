package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist;

import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.util.ExtraComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TaskTooltipComponent implements ClientTooltipComponent {

	private static final Component ADD_TEXT = Component.empty().append(ExtraComponents.INPUT_LEFT_CLICK).append(Component.literal("Add task to list").withColor(TextColor.GREEN));
	private static final Component INCREMENT_TEXT = Component.empty().append(ExtraComponents.INPUT_LEFT_CLICK).append(Component.literal("Add 1 to the complete count").withColor(TextColor.GREEN));
	private static final Component REMOVE_TEXT = Component.empty().append(ExtraComponents.INPUT_RIGHT_CLICK).append(Component.literal("Remove task from list")).withColor(TextColor.RED);
	private static final Component DECREMENT_TEXT = Component.empty().append(ExtraComponents.INPUT_RIGHT_CLICK).append(Component.literal("Remove 1 from the complete count")).withColor(TextColor.RED);
	private static final Identifier ICON_BACKGROUND = Identifier.withDefaultNamespace("container/bundle/slot_highlight_back");

	private final TaskWithCount task;
	private final ItemStack icon;
	private final Component taskName;
	private final boolean addHint;

	public TaskTooltipComponent(TaskWithCount task, Component name) {
		this(task, name, true);
	}

	public TaskTooltipComponent(TaskWithCount task, Component name, boolean addHint) {
		this.task = task;
		this.icon = task.createStack();
		this.taskName = name;
		this.addHint = addHint;
	}

	@Override
	public int getHeight(Font font) {
		int lines = 0;
		if (task.count() != 0) {
			lines++;
		}
		if (task.count() < task.task().maxCount()) {
			lines++;
		}
		return ((font.lineHeight + 2) * lines) * (addHint ? 1 : 0) + 28;
	}

	@Override
	public int getWidth(Font font) {
		int topWidth = 20 + font.width(taskName);

		if (addHint) {
			int addWidth = 0, removeWidth = 0;
			if (task.count() != task.task().maxCount()) {
				addWidth = task.count() == 0 ? font.width(ADD_TEXT) : font.width(INCREMENT_TEXT);
			}
			if (task.count() > 0) {
				removeWidth = task.count() == 1 ? font.width(REMOVE_TEXT) : font.width(DECREMENT_TEXT);
			}
			return Math.max(topWidth, Math.max(addWidth, removeWidth) + 4);
		}
		return topWidth + 4;
	}

	@Override
	public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor context) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, x, y, 24, 24);
		context.item(icon, x + 4, y + 4);
		context.itemDecorations(font, icon, x + 4, y + 4);
	}

	@Override
	public void extractText(GuiGraphicsExtractor context, Font font, int x, int y) {
		context.text(font, taskName, x + 24, y + 8, CommonColors.WHITE, true);
		if (addHint) {
			List<Component> lines = new ArrayList<>();
			if (task.count() != task.task().maxCount()) {
				lines.add(task.count() == 0 ? ADD_TEXT : INCREMENT_TEXT);
			}
			if (task.count() > 0) {
				lines.add(task.count() == 1 ? REMOVE_TEXT : DECREMENT_TEXT);
			}

			int i = 0;
			for (Component line : lines) {
				context.text(font, line, x + 2, y + 24 + ((font.lineHeight + 2) * i + 2), CommonColors.WHITE, true);
				i++;
			}
		}
	}
}
