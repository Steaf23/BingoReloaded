package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.api.network.BingoClientManager;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.gameloop.BingoInteraction;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.item.GameItem;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
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
	void onConfigReloaded(BingoConfigurationData config);

	void registerActions(BingoConfigurationData config);

	WorldHandle createBingoWorld(String worldName, Key generationOptions);

	ServerSoftware getServerSoftware();

	CardMenu createMenu(boolean textured, CardDisplayInfo displayInfo);
	void setPlayerUpForGame(BingoGame game, BingoParticipant participant);

	void openBingoMenu(PlayerHandle player, BingoSession session);
	void openTeamEditor(PlayerHandle player);
	void openBingoCreator(PlayerHandle player);
	void openTeamCardSelect(PlayerHandle player, BingoSession session);
	void openTeamSelector(PlayerHandle player, BingoSession session);
	void openVoteMenu(PlayerHandle player, PregameLobby lobby);

	TeamDisplay createTeamDisplay(BingoSession session);
	SharedDisplay gameDisplay();
	SharedDisplay settingsDisplay();

	BingoClientManager getClientManager();

	StackHandle defaultStack(GameItem item);

	void playerJoinedLobby(BingoSession session, PlayerHandle player);
	void droppedItemsOnDeath(BingoSession session, PlayerHandle player, Collection<StackHandle> items);
	boolean canItemBeUsedForInteraction(BingoSession session, PlayerHandle player, BingoInteraction interaction, StackHandle stack, PlayerInput input);
	boolean canItemBeUsedInKit(StackHandle stack);
}
