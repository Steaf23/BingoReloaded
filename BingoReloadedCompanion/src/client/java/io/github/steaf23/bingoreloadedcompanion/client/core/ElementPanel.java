package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@FunctionalInterface
public interface ElementPanel {
	void extract(GuiGraphicsExtractor extractor);
}
