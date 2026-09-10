package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.data.card.ListReference;
import io.github.steaf23.bingoreloadedcompanion.client.core.InputLayout;
import io.github.steaf23.bingoreloadedcompanion.client.core.LayoutBackground;
import io.github.steaf23.bingoreloadedcompanion.client.core.Popup;
import io.github.steaf23.bingoreloadedcompanion.client.core.SpinBoxWidget;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class CardViewPopup extends Popup<LinearLayout> {

	private final Font font;

	private CustomCard card = null;

	private final StringWidget cardNameWidget;
	private final MultiLineEditBox descriptionWidget;
	private final LinearLayout listsLayout;

	public CardViewPopup(Font font, int width, int height, Consumer<CardViewPopup> onFinished, Layout siblingLayout) {
		super(width, height, LinearLayout.vertical(), siblingLayout);

		this.font = font;
		layout().addChild(new LayoutBackground(layout(), ScreenHelper::extractInnerInventoryBackground));
		cardNameWidget = new StringWidget(Component.empty().withStyle(ScreenHelper.INVENTORY_STYLE), font);
		layout().addChild(new InputLayout(font, Component.literal("Name: ").withStyle(ScreenHelper.INVENTORY_STYLE), cardNameWidget), LayoutSettings.defaults().padding(3));
		layout().addChild(new StringWidget(Component.literal("Description: ").withStyle(ScreenHelper.INVENTORY_STYLE), font), LayoutSettings.defaults().padding(3));
		descriptionWidget = new MultiLineEditBox.Builder().build(font, 200, 30, Component.empty());
		layout().addChild(descriptionWidget);
		listsLayout = LinearLayout.vertical();
		layout().addChild(listsLayout);

		layout().addChild(Button.builder(Component.literal("Save & Exit"), _ -> {
			onFinished.accept(this);
		}).build());

		layout().arrangeElements();
	}

	public CustomCard getCard() {
		return card;
	}

	public void hide() {
		card = null;
		super.hide();
	}

	public void show(CustomCard card) {
		this.card = card;

		cardNameWidget.setMessage(Component.empty().withStyle(ScreenHelper.INVENTORY_STYLE));
		listsLayout.removeChildren();

		for (ListReference list : card.lists()) {
			LinearLayout listLayout = LinearLayout.horizontal();
			listLayout.addChild(new StringWidget(Component.literal("Min").withStyle(ScreenHelper.INVENTORY_STYLE), font), LayoutSettings.defaults().paddingHorizontal(3).paddingVertical(5));
			listLayout.addChild(SpinBoxWidget.defaultIntegerBox(font, (newValue) -> {

					})
					.startValue(list.min())
					.minValue(1)
					.maxValue(36), LayoutSettings.defaults().padding(2));

			listLayout.addChild(new StringWidget(Component.literal("Max").withStyle(ScreenHelper.INVENTORY_STYLE), font), LayoutSettings.defaults().paddingHorizontal(3).paddingVertical(5));
			listLayout.addChild(SpinBoxWidget.defaultIntegerBox(font, (newValue) -> {

					})
					.startValue(list.max())
					.minValue(1)
					.maxValue(36), LayoutSettings.defaults().padding(2));

			listsLayout.addChild(listLayout);
		}

		layout().arrangeElements();
		show();
	}
}
