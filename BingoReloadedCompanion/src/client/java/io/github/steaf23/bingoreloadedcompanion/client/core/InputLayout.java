package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class InputLayout extends LinearLayout {

	public InputLayout(Font font, Component message, AbstractWidget input) {
		super(0, 0, Orientation.HORIZONTAL);

		addChild(new StringWidget(message, font));
		addChild(input);
	}
}
