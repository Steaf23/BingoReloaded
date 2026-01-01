package io.github.steaf23.bingoreloadedcompanion.client.hud;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;

public class ConfigurableHudRegistry {

	private static final Map<Identifier, ConfigurableElement> ELEMENTS = new HashMap<>();

	public static Identifier registerSubElement(String hudName, String elementName, HudInfo info, HudPlacement defaultPlacement) {
		Identifier element = Identifier.parse("bingoreloadedcompanion:hud/" + hudName + "/" + elementName);
		ELEMENTS.put(element, new ConfigurableElement(info, defaultPlacement));
		return element;
	}

	public static @Nullable HudInfo getInfo(Identifier elementId) {
		ConfigurableElement el = ELEMENTS.get(elementId);
		return el == null ? null : el.info();
	}

	public static @Nullable HudPlacement getDefaultPlacement(Identifier elementId) {
		ConfigurableElement el = ELEMENTS.get(elementId);
		return el == null ? null : el.defaultPlacement();
	}

	private record ConfigurableElement(HudInfo info, HudPlacement defaultPlacement) {

	}


}
