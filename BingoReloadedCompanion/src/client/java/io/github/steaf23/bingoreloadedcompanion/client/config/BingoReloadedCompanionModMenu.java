package io.github.steaf23.bingoreloadedcompanion.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.steaf23.bingoreloadedcompanion.client.BingoReloadedCompanionClient;

public class BingoReloadedCompanionModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return s -> new BingoConfigScreen(s, BingoReloadedCompanionClient.getHudConfig());
	}
}
