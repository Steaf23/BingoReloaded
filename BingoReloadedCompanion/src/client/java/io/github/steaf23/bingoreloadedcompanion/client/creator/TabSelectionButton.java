package io.github.steaf23.bingoreloadedcompanion.client.creator;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class TabSelectionButton extends AbstractWidget {

	public record TaskTab(int index, Item icon, Component name, TaskType type) {}

	private static final TaskTab[] TABS = new TaskTab[]{
			new TaskTab(0, Items.APPLE, Component.nullToEmpty("Items"), TaskType.ITEM),
			new TaskTab(1, Items.ENDER_EYE, Component.nullToEmpty("Advancements"), TaskType.ADVANCEMENT),
			new TaskTab(2, Items.GLOBE_BANNER_PATTERN, Component.nullToEmpty("Statistics"), TaskType.STATISTIC)
	};

	private static final Identifier TAB_SELECTED = Identifier.parse("bingoreloadedcompanion:tab_selected");
	private static final Identifier TAB_BACKGROUND = Identifier.parse("bingoreloadedcompanion:tab_background");

	private static final int TAB_WIDTH = 20;
	private static final int TAB_INTERVAL = 18;
	private static final int TAB_HEIGHT = 20;

	private int selectedIndex = 0;
	private final TabChangedCallback tabChanged;

	public TabSelectionButton(TabChangedCallback onTabChanged) {
		super(0, 0, TAB_INTERVAL * TABS.length, TAB_HEIGHT, Component.empty());
		this.tabChanged = onTabChanged;
	}

	public TaskTab getSelectedTab() {
		return TABS[selectedIndex];
	}

	public void switchTab(int newIndex) {
		int clamped = Math.clamp(newIndex, 0, 2);
		if (clamped == selectedIndex) {
			return;
		}

		selectedIndex = clamped;
		tabChanged.tabChanged(selectedIndex, getSelectedTab());
	}

	public boolean isMouseOverTab(int tabIndex, double mouseX, double mouseY) {
		return isMouseOver(mouseX, mouseY)
				&& mouseX >= getX() + tabIndex * TAB_INTERVAL
				&& mouseX < getX() + (tabIndex + 1) * TAB_INTERVAL;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_BACKGROUND, getX(), getY(), getWidth(), getHeight());

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_SELECTED, getTabStartX(selectedIndex), getY(), TAB_WIDTH, TAB_HEIGHT);

		// Tooltip

		for (TaskTab tab : TABS) {
			graphics.item(tab.icon.getDefaultInstance(), getTabStartX(tab.index) + 2, getY() + 2);

			if (isMouseOverTab(tab.index, mouseX, mouseY))
			{
				graphics.setTooltipForNextFrame(tab.name, mouseX, mouseY);
				if (tab.index != selectedIndex) {
					graphics.requestCursor(CursorTypes.POINTING_HAND);
				}
			}
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {

		if (doubleClick) return true;

		int button = event.button();
		double mouseX = event.x();
		double mouseY = event.y();

		if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return false;
		}

		for (TaskTab tab : TABS) {
			if (isMouseOverTab(tab.index, mouseX, mouseY)) {
				if (tab.index != selectedIndex) {
					switchTab(tab.index);
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		if (isMouseOver(x, y)) {
			switchTab(selectedIndex + (scrollY > 0 ? 1 : -1));
		}

		return true;
	}

	private int getTabStartX(int index) {
		return getX() + TAB_INTERVAL * index;
	}

	@FunctionalInterface
	public interface TabChangedCallback {
		void tabChanged(int newIndex, TaskTab newTab);
	}
}
