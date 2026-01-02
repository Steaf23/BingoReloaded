package io.github.steaf23.bingoreloadedcompanion.client.hud;

import net.minecraft.resources.Identifier;

import java.util.Map;

public record HudConfig(String version, Map<Identifier, HudPlacement> elements) {

}
