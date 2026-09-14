package io.github.steaf23.bingoreloadedcompanion.client.mixin.accessor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsExtractorAccessor {

	@Invoker("setTooltipForNextFrameInternal")
	void bingoreloadedcompanion$setTooltipForNextFrameInternal(final Font font,
	                                                           final List<ClientTooltipComponent> lines,
	                                                           final int xo,
	                                                           final int yo,
	                                                           final ClientTooltipPositioner positioner,
	                                                           final @Nullable Identifier style,
	                                                           final boolean replaceExisting);
}
