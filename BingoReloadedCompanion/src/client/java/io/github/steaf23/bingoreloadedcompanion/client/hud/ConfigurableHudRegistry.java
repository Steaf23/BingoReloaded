package io.github.steaf23.bingoreloadedcompanion.client.hud;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ConfigurableHudRegistry {

	private static final Map<Identifier, ConfigurableElement> ELEMENTS = new HashMap<>();
	private static final Map<Identifier, ConfigOption> OPTIONS = new HashMap<>();

	public static Identifier registerSubElement(String hudName, String elementName, HudInfo info, HudPlacement defaultPlacement) {
		Identifier element = Identifier.parse("bingoreloadedcompanion:hud/" + hudName + "/" + elementName);
		ELEMENTS.put(element, new ConfigurableElement(info, defaultPlacement));
		return element;
	}

	public static Identifier createSimpleOption(String hudName, String name, ConfigOption defaultValue) {
		Identifier opt = Identifier.parse("bingoreloadedcompanion:hud/" + hudName + "/" + name);
		OPTIONS.put(opt, defaultValue);
		return opt;
	}

	public static @Nullable HudInfo getInfo(Identifier elementId) {
		ConfigurableElement el = ELEMENTS.get(elementId);
		return el == null ? null : el.info();
	}

	public static @Nullable HudPlacement getDefaultPlacement(Identifier elementId) {
		ConfigurableElement el = ELEMENTS.get(elementId);
		return el == null ? null : el.defaultPlacement();
	}

	public static @NonNull ConfigOption getDefaultOption(Identifier optionId) {
		return OPTIONS.getOrDefault(optionId, ConfigOption.DEFAULT);
	}

	private record ConfigurableElement(HudInfo info, HudPlacement defaultPlacement) {

	}


}
