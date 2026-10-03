package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface DeferredExtraction {
	void extractAtTheEnd(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);
}
