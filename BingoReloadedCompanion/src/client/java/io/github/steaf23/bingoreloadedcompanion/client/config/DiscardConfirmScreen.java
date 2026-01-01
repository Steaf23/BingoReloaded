package io.github.steaf23.bingoreloadedcompanion.client.config;

import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DiscardConfirmScreen extends Screen {

	private final Runnable discardAction;
	private final Screen sourceScreen;
	private final Screen targetScreen;

	protected DiscardConfirmScreen(Screen sourceScreen, Screen targetScreen, @Nullable Runnable onDiscard) {
		super(Component.nullToEmpty("Quit and discard changes?"));
		this.sourceScreen = sourceScreen;
		this.targetScreen = targetScreen;
		this.discardAction = onDiscard == null ? () -> {} : onDiscard;
	}

	@Override
	protected void init() {
		super.init();

		Button discardButton = new Button.Builder(Component.nullToEmpty("Discard changes"), (btn) -> {
			discardAction.run();
			Minecraft.getInstance().setScreen(targetScreen);
		})
				.pos(width / 2 - 150 - 30, height / 2)
				.build();
		addRenderableWidget(discardButton);

		Button cancelButton = new Button.Builder(Component.nullToEmpty("Go back to edit"), (btn) -> {
			Minecraft.getInstance().setScreen(sourceScreen);
		})
				.pos(width / 2 + 30, height / 2)
				.build();
		addRenderableWidget(cancelButton);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {

		List<Component> text = List.of(
				Component.nullToEmpty("You have made edits that have not been saved."),
				Component.nullToEmpty("Would you like to discard these changes or go back to editing?"));

		int i = height / 4;
		int lineHeight = 15;
		for (Component line : text) {
			context.drawString(font, line, width / 2 - font.width(line) / 2, i, ScreenHelper.addAlphaToColor(ChatFormatting.WHITE.getColor(), 255));
			i += lineHeight;
		}

		super.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
