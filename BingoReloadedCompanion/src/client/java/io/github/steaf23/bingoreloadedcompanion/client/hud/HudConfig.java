package io.github.steaf23.bingoreloadedcompanion.client.hud;

import net.minecraft.util.Identifier;

import java.util.Map;

public record HudConfig(Map<Identifier, HudPlacement> elements) {

}
