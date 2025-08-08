package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.key.Key;

import java.util.Collection;
import java.util.Set;

/**
 * Used by BingoReloaded to set up features that are implemented by each platform separately.
 */
public interface BingoReloadedRuntime {
	DataAccessor getConfigData();
	Collection<DataAccessor> getDataToRegister();
	void setupConfig();

	Set<EntityType> getValidEntityTypesForStatistics();

	record LanguageData(DataAccessor selectedLanguage, DataAccessor backupLanguage){};
	LanguageData getLanguageData(String language);
	void onLanguageUpdated();

	void registerActions(BingoConfigurationData config);

	WorldHandle createBingoWorld(String worldName, Key generationOptions);

	ServerSoftware getServerSoftware();

	CardMenu createMenu(boolean textured, CardDisplayInfo displayInfo);

	void openBingoMenu(PlayerHandle player, BingoSession session);
	void openTeamEditor(PlayerHandle player);
	void openBingoCreator(PlayerHandle player);
	void openTeamCardSelect(PlayerHandle player, BingoSession session);
	void openTeamSelector(PlayerHandle player, BingoSession session);
	void openVoteMenu(PlayerHandle player, PregameLobby lobby);

	TeamDisplay createTeamDisplay(BingoSession session);
}
