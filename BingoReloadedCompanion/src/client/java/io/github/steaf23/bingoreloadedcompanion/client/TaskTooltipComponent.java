package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;

public class TaskTooltipComponent implements ClientTooltipComponent {

	private static final Identifier ICON_BACKGROUND = Identifier.withDefaultNamespace("container/bundle/slot_highlight_back");

	private final TaskSlot task;
	private final ItemStack icon;
	private final Component taskName;

	public TaskTooltipComponent(TaskSlot task) {
		this.task = task;
		this.icon = new ItemStack(task.item(), task.completeCount() == 0 ? 1 : task.completeCount());
		this.taskName = task.name();
	}

	@Override
	public int getHeight(Font textRenderer) {
		return 26;
	}

	@Override
	public int getWidth(Font textRenderer) {
		return 20 + textRenderer.width(taskName) + 4;
	}

	@Override
	public void renderImage(Font textRenderer, int x, int y, int width, int height, GuiGraphics context) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, x, y, 24, 24);
		context.renderItem(icon, x + 4, y + 4);
		context.renderItemDecorations(textRenderer, icon, x + 4, y + 4);
	}

	@Override
	public void renderText(GuiGraphics context, Font textRenderer, int x, int y) {
		context.drawString(textRenderer, taskName, x + 24, y, CommonColors.WHITE, true);
	}
}
