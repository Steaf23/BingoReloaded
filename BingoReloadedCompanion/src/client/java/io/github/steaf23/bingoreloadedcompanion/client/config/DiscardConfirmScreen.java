package io.github.steaf23.bingoreloadedcompanion.client.config;

import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Formattable;
import java.util.List;

public class DiscardConfirmScreen extends Screen {

	private final Runnable discardAction;
	private final Screen sourceScreen;
	private final Screen targetScreen;

	protected DiscardConfirmScreen(Screen sourceScreen, Screen targetScreen, @Nullable Runnable onDiscard) {
		super(Text.of("Quit and discard changes?"));
		this.sourceScreen = sourceScreen;
		this.targetScreen = targetScreen;
		this.discardAction = onDiscard == null ? () -> {} : onDiscard;
	}

	@Override
	protected void init() {
		super.init();

		ButtonWidget discardButton = new ButtonWidget.Builder(Text.of("Discard changes"), (btn) -> {
			discardAction.run();
			MinecraftClient.getInstance().setScreen(targetScreen);
		})
				.position(width / 2 - 150 - 30, height / 2)
				.build();
		addDrawableChild(discardButton);

		ButtonWidget cancelButton = new ButtonWidget.Builder(Text.of("Go back to edit"), (btn) -> {
			MinecraftClient.getInstance().setScreen(sourceScreen);
		})
				.position(width / 2 + 30, height / 2)
				.build();
		addDrawableChild(cancelButton);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

		List<Text> text = List.of(
				Text.of("You have made edits that have not been saved."),
				Text.of("Would you like to discard these changes or go back to editing?"));

		int i = height / 4;
		int lineHeight = 15;
		for (Text line : text) {
			context.drawTextWithShadow(textRenderer, line, width / 2 - textRenderer.getWidth(line) / 2, i, ScreenHelper.addAlphaToColor(Formatting.WHITE.getColorValue(), 255));
			i += lineHeight;
		}

		super.render(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
