package io.github.steaf23.bingoreloaded.lib.gui.menu;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ValueListHUD implements PlayerHUD {

	protected final Map<String, Component[]> registeredFields = new HashMap<>();
	private final List<PlayerHUD> huds = new ArrayList<>();

	public void addField(String key, Component... text) {
		registeredFields.put(key, text);
	}

}
