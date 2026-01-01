package io.github.steaf23.bingoreloadedcompanion.client.hud;

import java.util.Map;
import net.minecraft.resources.Identifier;

public record HudConfig(String version, Map<Identifier, HudPlacement> elements) {

}
