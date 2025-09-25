package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.HotswapTaskHolder;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import io.github.steaf23.bingoreloadedcompanion.client.ExtraMath;
import io.github.steaf23.bingoreloadedcompanion.client.TextColorGradient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BingoCardHudElement implements HudElement {

	private static final Identifier GAMEMODE_LOGOS = Identifier.of("bingoreloadedcompanion:textures/gui/gamemode_logos.png");
	private static final Identifier SLOT_BACKGROUND = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/card_slot.png");
	private static final Identifier TIMER_ROUND = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/timer.png");
	private static final Identifier TIMER_SQUARE = Identifier.of("bingoreloadedcompanion:textures/gui/sprites/timer_square.png");

	private static final Integer ADVANCEMENT_COLOR = Formatting.GREEN.getColorValue();
	private static final Integer STATISTIC_COLOR = Formatting.LIGHT_PURPLE.getColorValue();
	private static final Integer RECOVERY_COLOR = TextColor.parse("#5cb1ff").getOrThrow().getRgb();
	private static final TextColorGradient HOTSWAP_EXPIRATION_GRADIENT = new TextColorGradient()
			.addColor(TextColor.parse("#ffd200").getOrThrow(), 0.0f)
			.addColor(TextColor.parse("#e85e21").getOrThrow(), 0.5f)
			.addColor(TextColor.parse("#750e0e").getOrThrow(), 0.8f)
			.addColor(TextColor.fromFormatting(Formatting.DARK_GRAY), 1.0f);


	private static final int ITEM_SIZE = 16;

	private @Nullable BingoCard card;
	private @Nullable ImmutableList<HotswapTaskHolder> hotswapTaskHolders;
	private long lastHotswapUpdateTick = 0;

	boolean renderingInScreen = false;

	private static final Identifier TASKS_ELEMENT = Identifier.of("bingoreloadedcompanion:hud/bingocard/tasks");
	private static final Identifier GAMEMODE_ELEMENT = Identifier.of("bingoreloadedcompanion:hud/bingocard/gamemode");

	public BingoCardHudElement() {
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
		HudInfo tasksInfo = ConfigurableHudRegistry.getInfo(TASKS_ELEMENT);
		HudPlacement tasksPlacement = ConfigurableHudRegistry.getDefaultPlacement(TASKS_ELEMENT);

		if (card == null || card.tasks().isEmpty() || tasksInfo == null || tasksPlacement == null) {
			return;
		}

		int spacing = 3;
		int startOffsetX = tasksPlacement.x();
		int startOffsetY = tasksPlacement.y();

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
				renderTask(drawContext, task, xStart, yStart, holder, tickDelta);
				taskIdx++;
			}
		}

		HudInfo gamemodeInfo = ConfigurableHudRegistry.getInfo(GAMEMODE_ELEMENT);
		HudPlacement gamemodePlacement = ConfigurableHudRegistry.getDefaultPlacement(GAMEMODE_ELEMENT);

		int textureIndex = card.mode().getIndex();
		int gamemodeBannerSizeX = 128;
		int gamemodeBannerSizeY = 32;
		int gamemodeStartX = gamemodePlacement.x();
		int gamemodeStartY = gamemodePlacement.y();
		drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, GAMEMODE_LOGOS, gamemodeStartX, gamemodeStartY,
				0, textureIndex * gamemodeBannerSizeY, gamemodeBannerSizeX, gamemodeBannerSizeY,
				gamemodeBannerSizeX, gamemodeBannerSizeY, 128, 128);

	}

	protected void renderTask(DrawContext drawContext, Task task, int x, int y, @Nullable HotswapTaskHolder hotswapContext, float delta) {

		boolean hotswapRecovering = hotswapContext != null && hotswapContext.recovering();
		boolean hotswapExpires = hotswapContext != null && hotswapContext.expires();

		Task.TaskCompletion completion = task.completion();
		if (completion.completed() && !hotswapRecovering) {
			drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND, x, y, 0, 0, 17, 17, 17, 17, addAlphaToColor(completion.teamColor(), 200));
		}

		if (!completion.completed() && !hotswapRecovering) {
			drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND, x, y, 0, 0, 17, 17, 17, 17, 0x88000000);
		} else if (hotswapRecovering) {
			float predictedTime = hotswapContext.currentTimeSeconds() - ((HudTimer.getTicks() - lastHotswapUpdateTick) / 20.0f) + delta;
			int color = addAlphaToColor(RECOVERY_COLOR, 255);
			renderTimerRound(drawContext, (int) hotswapContext.totalTimeSeconds(), predictedTime, x, y, color, false);
		} else if (hotswapExpires) {
			float predictedTime = hotswapContext.currentTimeSeconds() - ((HudTimer.getTicks() - lastHotswapUpdateTick) / 20.0f) + delta;
			int color = addAlphaToColor(HOTSWAP_EXPIRATION_GRADIENT.sample(1 - predictedTime / hotswapContext.totalTimeSeconds()).getRgb(), 200);
			renderTimerSquare(drawContext, (int) hotswapContext.totalTimeSeconds(), predictedTime, x, y, color, true);
		}

		if (hotswapRecovering) {
			return;
		}

		ItemStack stack = new ItemStack(task.itemType(), task.requiredAmount());
		drawContext.drawItem(stack, x, y);

		String taskType = task.taskType().toString();
		int bannerColor = switch (taskType) {
			case "bingoreloaded:advancement" -> addAlphaToColor(ADVANCEMENT_COLOR, 255);
			case "bingoreloaded:statistic" -> addAlphaToColor(STATISTIC_COLOR, 255);
			default -> 0;
		};

		int bannerStartX = x - 1;
		int bannerStartY = y + (ITEM_SIZE / 4 * 3);

		drawContext.fill(bannerStartX, bannerStartY, bannerStartX + ITEM_SIZE + 2, bannerStartY + ITEM_SIZE / 4, bannerColor);
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		drawContext.drawStackOverlay(textRenderer, stack, x, y);
	}

	private void renderTimerRound(DrawContext context, float startTime, float currentTime, int x, int y, int color, boolean reverse) {
		renderTimer(TIMER_ROUND, context, startTime, currentTime, x, y, color, reverse);
	}

	private void renderTimerSquare(DrawContext context, float startTime, float currentTime, int x, int y, int color, boolean reverse) {
		renderTimer(TIMER_SQUARE, context, startTime, currentTime, x, y, color, reverse);
	}

	private void renderTimer(Identifier type, DrawContext context, float startTime, float currentTime, int x, int y, int color, boolean reverse) {
		int frameSize = 17;
		int frame = (int)ExtraMath.map(currentTime, 0, startTime, 0, 41);
		frame = reverse ? 41 - frame : frame;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, type, x, y, frame * frameSize, frameSize, frameSize, frameSize, frameSize * 41, frameSize, color);
	}

	private int addAlphaToColor(int color, int alpha) {
		alpha = alpha << 24;
		return alpha | color;
	}
}
