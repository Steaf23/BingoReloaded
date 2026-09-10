package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloadedcompanion.client.core.CustomScrollableLayout;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class CreatorCardScreen extends Screen {

	private static final Identifier TASK_ADD = Identifier.parse("bingoreloadedcompanion:task_add");
	private static final Identifier CARD_BUTTON = Identifier.parse("bingoreloadedcompanion:card_background");

	private final CreatorSuite creatorSuite;

	private CardViewPopup cardPopup = null;

	protected CreatorCardScreen(CreatorSuite suite) {
		super(Component.literal("Manage custom cards"));
		this.creatorSuite = suite;
	}

	@Override
	protected void init() {
		super.init();

		LinearLayout centerLayout = LinearLayout.vertical();
		this.cardPopup = new CardViewPopup(font, width, height, result -> {
			creatorSuite.saveCard(result.getCard());
			cardPopup.hide();
		}, centerLayout);
		cardPopup.hide();
		addWidget(cardPopup);

		ScreenHelper.centerLayout(this, cardPopup.layout());



		LinearLayout padding = LinearLayout.vertical();

		for (CustomCard card : creatorSuite.cards()) {
			padding.addChild(cardButton(card), LayoutSettings.defaults().padding(15));
		}

		padding.addChild(newCardButton(), LayoutSettings.defaults().padding(27));

		CustomScrollableLayout cardScroll = new CustomScrollableLayout(0, 0, 48 + 30 + CustomScrollableLayout.SCROLLER_WIDTH, CustomScrollableLayout.SCROLLER_WIDTH, 48 * 5 + 30, padding, CustomScrollableLayout.DEFAULT_SETTINGS);
		centerLayout.addChild(cardScroll);
		centerLayout.arrangeElements();

		ScreenHelper.centerLayout(this, centerLayout);
		centerLayout.visitWidgets(this::addRenderableWidget);

		addRenderableWidget(cardPopup);
	}

	public Button cardButton(CustomCard card) {
		ImageButton btn = new ImageButton(0, 0, 48, 48, new WidgetSprites(CARD_BUTTON),
				_ -> openCardViewLayout(card),
				Component.literal("Open " + card.name()));
		btn.setTooltip(Tooltip.create(btn.getMessage()));
		return btn;
	}

	public Button newCardButton() {
		ImageButton btn = new ImageButton(0, 0, 24, 24, new WidgetSprites(TASK_ADD),
			_ -> openCardViewLayout(new CustomCard("New Card", List.of(), "", false)),
				Component.literal("Create New Card"));

		btn.setTooltip(Tooltip.create(btn.getMessage()));

		return btn;
	}

	public void openCardViewLayout(CustomCard card) {
		cardPopup.show(card);
	}
}
