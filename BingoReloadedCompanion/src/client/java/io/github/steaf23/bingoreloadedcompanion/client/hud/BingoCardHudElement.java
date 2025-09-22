package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.HotswapTaskHolder;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import io.github.steaf23.bingoreloadedcompanion.client.TextColorGradient;
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
import org.jetbrains.annotations.Nullable;

public class BingoCardHudElement implements HudElement {

	private static final Identifier GAMEMODE_LOGOS = Identifier.of("bingoreloadedcompanion:textures/gui/gamemode_logos.png");

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
				renderTask(drawContext, task, xStart, yStart, holder);
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

	protected void renderTask(DrawContext drawContext, Task task, int x, int y, @Nullable HotswapTaskHolder hotswapContext) {

		Task.TaskCompletion completion = task.completion();
		if (completion.completed()) {
			drawContext.fill(x - 1, y - 1, x + ITEM_SIZE + 1, y + ITEM_SIZE + 1, addAlphaToColor(completion.teamColor(), 200));
			drawContext.drawBorder(x - 1, y - 1, ITEM_SIZE + 2, ITEM_SIZE + 2, addAlphaToColor(completion.teamColor(), 255));
		}

		// Hotswap stuff
		if (hotswapContext != null) {
			if (hotswapContext.recovering() || hotswapContext.expires()) {
				int expireStartX = x - 1;
				int expireStartY = y - 1;
				int expireSizeX = ITEM_SIZE + 2;
				int expireSizeY = ITEM_SIZE / 4 * 3;

				if (hotswapContext.recovering()) {
					expireSizeY += 6;
					int availablePixels = expireSizeY * expireSizeX;
					int fillAmount = (int)((float)hotswapContext.currentTimeSeconds() / hotswapContext.totalTimeSeconds() * availablePixels);
					int layers = fillAmount / expireSizeX;
					int rem = fillAmount % expireSizeX;

					int color = addAlphaToColor(RECOVERY_COLOR, 255);

					drawContext.fill(expireStartX, expireStartY + (expireSizeY - layers), expireStartX + rem, expireStartY + (expireSizeY - layers) + 1, color);
					drawContext.fill(expireStartX, expireStartY + (expireSizeY - layers + 1), expireStartX + expireSizeX, expireStartY + expireSizeY, color);
				} else {
					int availablePixels = expireSizeY * expireSizeX;
					int fillAmount = (int)((float)hotswapContext.currentTimeSeconds() / hotswapContext.totalTimeSeconds() * availablePixels);
					int layers = fillAmount / expireSizeX;
					int rem = fillAmount % expireSizeX;

					int color = addAlphaToColor(HOTSWAP_EXPIRATION_GRADIENT.sample(1 - (float)fillAmount / availablePixels).getRgb(), 255);
					int blockXEnd = expireStartX + expireSizeX;
					int blockYEnd = expireStartY + layers;
					drawContext.fill(expireStartX, expireStartY, blockXEnd, blockYEnd, color);
					drawContext.fill(expireStartX, blockYEnd, expireStartX + rem, blockYEnd + 1, color);
				}
			}
		}

		if (!completion.completed() && (hotswapContext == null || !hotswapContext.recovering())){
			drawContext.fill(x, y, x + ITEM_SIZE, y + ITEM_SIZE, 0x88000000);
		}

		if (hotswapContext != null && hotswapContext.recovering()) {
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

	private int addAlphaToColor(int color, int alpha) {
		alpha = alpha << 24;
		return alpha | color;
	}
}
