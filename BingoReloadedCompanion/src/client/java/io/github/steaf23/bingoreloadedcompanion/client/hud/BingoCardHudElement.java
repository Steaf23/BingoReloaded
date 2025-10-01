package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.HotswapTaskHolder;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import io.github.steaf23.bingoreloadedcompanion.client.ExtraMath;
import io.github.steaf23.bingoreloadedcompanion.client.TextColorGradient;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class BingoCardHudElement implements HudElement {

	private static final Identifier GAMEMODE_LOGOS = Identifier.of("bingoreloadedcompanion:textures/gui/gamemode_logos.png");
	private static final Identifier TASK_BACKGROUND = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_background.png");
	private static final Identifier TASK_BACKGROUND_ADVANCEMENT = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_background_advancement.png");
	private static final Identifier TASK_BACKGROUND_STATISTIC = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_background_statistic.png");
	private static final Identifier TASK_BACKGROUND_HOTSWAP = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_background_hotswap.png");
	private static final Identifier TASK_ADVANCEMENT_ICON = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_advancement_icon.png");
	private static final Identifier TASK_STATISTIC_ICON = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_statistic_icon.png");
	private static final Identifier TASK_COMPLETED_OVERLAY = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/task_completed_overlay.png");

	private static final Integer RECOVERY_COLOR = TextColor.parse("#5cb1ff").getOrThrow().getRgb();
	private static final TextColorGradient HOTSWAP_EXPIRATION_GRADIENT = new TextColorGradient()
			.addColor(TextColor.parse("#ffffff").getOrThrow(), 0.00f)
			.addColor(TextColor.parse("#3cff00").getOrThrow(), 0.05f)
			.addColor(TextColor.parse("#ffd200").getOrThrow(), 0.25f)
			.addColor(TextColor.parse("#e85e21").getOrThrow(), 0.5f)
			.addColor(TextColor.parse("#750e0e").getOrThrow(), 0.8f)
			.addColor(TextColor.fromFormatting(Formatting.DARK_GRAY), 1.0f);


	private static final int ITEM_SIZE = 16;

	private final HudConfigManager hudConfig;
	private @Nullable BingoCard card;
	private @Nullable ImmutableList<HotswapTaskHolder> hotswapTaskHolders;
	private long lastHotswapUpdateTick = 0;

	boolean renderingInScreen = false;

	private static final Identifier TASKS_ELEMENT = Identifier.of("bingoreloadedcompanion:hud/bingocard/tasks");
	private static final Identifier GAMEMODE_ELEMENT = Identifier.of("bingoreloadedcompanion:hud/bingocard/gamemode");

	public BingoCardHudElement(HudConfigManager hudConfig) {
		this.hudConfig = hudConfig;
	}

	public void setCard(@Nullable BingoCard card) {
		this.card = card;
		if (card == null) {
			hotswapTaskHolders = null;
		}
	}

	public void setHotswapHolders(ImmutableList<HotswapTaskHolder> holders) {
		this.hotswapTaskHolders = holders;
		lastHotswapUpdateTick = HudTimer.getTicks();
	}

	public void renderFromScreen(DrawContext drawContext, float tickDelta) {
		if (!renderingInScreen) {
			return;
		}
		renderElement(drawContext, tickDelta);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (renderingInScreen) {
			return;
		}
		renderElement(drawContext, renderTickCounter.getDynamicDeltaTicks());
	}

	public void setRenderingInScreen(boolean renderingInScreen) {
		this.renderingInScreen = renderingInScreen;
	}

	public void renderElement(DrawContext drawContext, float tickDelta) {
		if (card == null) {
			return;
		}

		HudPlacement tasksPlacement = hudConfig.getHudPlacement(TASKS_ELEMENT);
		if (tasksPlacement.visible()) {
			renderTasks(drawContext, tickDelta);
		}

		HudPlacement bannerPlacement = hudConfig.getHudPlacement(GAMEMODE_ELEMENT);
		if (bannerPlacement.visible()) {
			renderBanner(drawContext, tickDelta);
		}
	}

	private void renderTasks(DrawContext context, float tickDelta) {

		HudConfigManager.Rect tasksRect = hudConfig.getUsedRectOfElement(TASKS_ELEMENT);
		HudPlacement placement = hudConfig.getHudPlacement(TASKS_ELEMENT);

		float scaleFactorX = 1.0f / MinecraftClient.getInstance().getWindow().getScaleFactor() * placement.scaleX();
		float scaleFactorY = 1.0f / MinecraftClient.getInstance().getWindow().getScaleFactor() * placement.scaleY();

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(tasksRect.x(), tasksRect.y());
		matrices.scale(scaleFactorX, scaleFactorY);
		matrices.translate(-tasksRect.x(), -tasksRect.y());

		if (card.tasks().isEmpty()) {
			return;
		}

		int spacing = 6;
		int startOffsetX = tasksRect.x();
		int startOffsetY = tasksRect.y();

		int taskIdx = 0;
		for (int y = 0; y < card.size(); y++) {
			for (int x = 0; x < card.size(); x++) {
				int xStart = startOffsetX + spacing * x + x * ITEM_SIZE;
				int yStart = startOffsetY + spacing * y + y * ITEM_SIZE;

				if (card.size() == 3) {
					xStart += spacing + ITEM_SIZE;
					yStart += spacing + ITEM_SIZE;
				}

				Task task = card.tasks().get(taskIdx);
				HotswapTaskHolder holder = null;
				if (hotswapTaskHolders != null && taskIdx < hotswapTaskHolders.size()) {
					holder = hotswapTaskHolders.get(taskIdx);
				}
				renderTask(context, task, xStart, yStart, holder, tickDelta);
				taskIdx++;
			}
		}
		matrices.popMatrix();
	}

	private void renderBanner(DrawContext context, float tickDelta) {

		HudConfigManager.Rect gamemodeRect = hudConfig.getUsedRectOfElement(GAMEMODE_ELEMENT);
		HudPlacement placement = hudConfig.getHudPlacement(GAMEMODE_ELEMENT);

		float scaleFactorX = 1.0f / MinecraftClient.getInstance().getWindow().getScaleFactor() * placement.scaleX();
		float scaleFactorY = 1.0f / MinecraftClient.getInstance().getWindow().getScaleFactor() * placement.scaleY();

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(gamemodeRect.x(), gamemodeRect.y());
		matrices.scale(scaleFactorX, scaleFactorY);
		matrices.translate(-gamemodeRect.x(), -gamemodeRect.y());

		int textureIndex = card.mode().getIndex();
		int gamemodeBannerSizeX = 128;
		int gamemodeBannerSizeY = 32;
		int gamemodeStartX = gamemodeRect.x();
		int gamemodeStartY = gamemodeRect.y();
		context.drawTexture(RenderPipelines.GUI_TEXTURED, GAMEMODE_LOGOS, gamemodeStartX, gamemodeStartY,
				0, textureIndex * gamemodeBannerSizeY, gamemodeBannerSizeX, gamemodeBannerSizeY,
				gamemodeBannerSizeX, gamemodeBannerSizeY, 128, 128);

		matrices.popMatrix();
	}

	protected void renderTask(DrawContext drawContext, @NotNull Task task, int x, int y, @Nullable HotswapTaskHolder hotswapContext, float delta) {

		boolean hotswapRecovering = hotswapContext != null && hotswapContext.recovering();
		boolean hotswapExpires = hotswapContext != null && hotswapContext.expires();

		int borderX = x;
		int borderY = y;
		int taskX = x + 2;
		int taskY = y + 2;

		// Task background
		Task.TaskCompletion completion = task.completion();
		if (hotswapRecovering) {
			float predictedTime = hotswapContext.currentTimeSeconds() - ((HudTimer.getTicks() - lastHotswapUpdateTick) / 20.0f) + delta;
			int color = ScreenHelper.addAlphaToColor(RECOVERY_COLOR, 255);
			renderHotswapBackground(drawContext, (int) hotswapContext.totalTimeSeconds(), predictedTime, borderX, borderY, color, false);
			return;
		} else if (hotswapExpires) {
			float predictedTime = hotswapContext.currentTimeSeconds() - ((HudTimer.getTicks() - lastHotswapUpdateTick) / 20.0f) + delta;
			int color = ScreenHelper.addAlphaToColor(HOTSWAP_EXPIRATION_GRADIENT.sample(1 - predictedTime / hotswapContext.totalTimeSeconds()).getRgb(), 200);
			renderHotswapBackground(drawContext, (int) hotswapContext.totalTimeSeconds(), predictedTime, borderX, borderY, color, true);
		} else {
			String taskType = task.taskType().toString();
			Identifier backgroundTexture = switch (taskType) {
				case "bingoreloaded:advancement" -> TASK_BACKGROUND_ADVANCEMENT;
				case "bingoreloaded:statistic" -> TASK_BACKGROUND_STATISTIC;
				default -> TASK_BACKGROUND;
			};

			drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, backgroundTexture, borderX, borderY, 0, 0, 21, 21, 21, 21, ScreenHelper.addAlphaToColor(0xFFFFFF, 128));
		}

		// Completion overlay
		if (completion.completed()) {
			drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, TASK_COMPLETED_OVERLAY, borderX, borderY, 0, 0, 21, 21, 21, 21, ScreenHelper.addAlphaToColor(completion.teamColor(), 255));
		}

		// Actual item representation of the task
		ItemStack stack = new ItemStack(task.itemType(), task.requiredAmount());
		drawContext.drawItem(stack, taskX, taskY);

		// Draw statistic/ advancement overlay sprite last
		String taskType = task.taskType().toString();
		switch (taskType) {
			case "bingoreloaded:advancement" -> {
				drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, TASK_ADVANCEMENT_ICON, borderX, borderY, 0, 0, 21, 24, 21, 24);
			}
			case "bingoreloaded:statistic" -> {
				drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, TASK_STATISTIC_ICON, borderX, borderY, 0, 0, 21, 24, 21, 24);
			}
		}

		// Lastly draw the required amount of the task.
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		drawContext.drawStackOverlay(textRenderer, stack, taskX, taskY);
	}

	private void renderHotswapBackground(DrawContext context, float startTime, float currentTime, int x, int y, int color, boolean reverse) {
		int frameWidth = 21;
		int frameHeight = 21;
		int totalFrames = 62;

		int currentFrame = (int)ExtraMath.map(currentTime, 0, startTime, 0, totalFrames);
		currentFrame = reverse ? totalFrames - currentFrame : currentFrame;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TASK_BACKGROUND_HOTSWAP, x, y, currentFrame * frameWidth, frameHeight, frameWidth, frameHeight, frameWidth * totalFrames, frameHeight, color);
	}
}
