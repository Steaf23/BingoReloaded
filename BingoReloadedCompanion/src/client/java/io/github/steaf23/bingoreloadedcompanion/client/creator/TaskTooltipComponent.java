package io.github.steaf23.bingoreloadedcompanion.client.creator;

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

public class TaskTooltipComponent implements ClientTooltipComponent {

	private static final Component ADD_TEXT = Component.empty().append(ExtraComponents.INPUT_LEFT_CLICK).append(Component.literal("Add task to list").withColor(TextColor.GREEN));
	private static final Component REMOVE_TEXT = Component.empty().append(ExtraComponents.INPUT_LEFT_CLICK).append(Component.literal("Remove task from list")).withColor(TextColor.RED);
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
		return ((font.lineHeight + 2) * (addHint ? 2 : 1)) + 16;
	}

	@Override
	public int getWidth(Font font) {
		int topWidth = 20 + font.width(taskName);

		if (addHint) {
			return Math.max(topWidth, task.count() == 0 ? font.width(ADD_TEXT) : font.width(REMOVE_TEXT)) + 4;
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
			context.text(font, task.count() == 0 ? ADD_TEXT : REMOVE_TEXT, x + 2, y + 16 + 1 + font.lineHeight, CommonColors.WHITE, true);
		}
	}
}
