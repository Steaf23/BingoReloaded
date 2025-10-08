package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class TaskTooltipComponent implements TooltipComponent {

	private static final Identifier ICON_BACKGROUND = Identifier.ofVanilla("container/bundle/slot_highlight_back");

	private final TaskSlot task;
	private final ItemStack icon;
	private final Text taskName;

	public TaskTooltipComponent(TaskSlot task) {
		this.task = task;
		this.icon = new ItemStack(task.item(), task.completeCount() == 0 ? 1 : task.completeCount());
		this.taskName = task.name();
	}

	@Override
	public int getHeight(TextRenderer textRenderer) {
		return 26;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 20 + textRenderer.getWidth(taskName) + 4;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, x, y, 24, 24);
		context.drawItem(icon, x + 4, y + 4);
		context.drawStackOverlay(textRenderer, icon, x + 4, y + 4);
	}

	@Override
	public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
		context.drawText(textRenderer, taskName, x + 24, y, Colors.WHITE, true);
	}
}
