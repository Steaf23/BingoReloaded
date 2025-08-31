package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.FabricServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.SnakeYamlDataAccessor;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.fabricmc.api.ModInitializer;
import net.kyori.adventure.key.Key;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BingoReloadedFabric implements ModInitializer, BingoReloadedRuntime {

	private static final String MOD_ID = "bingoreloaded";

	private final FabricServerSoftware platform;
	private final BingoReloaded bingo;

	public BingoReloadedFabric() {
		this.platform = new FabricServerSoftware(MOD_ID);
		PlatformResolver.set(platform);

		this.bingo = new BingoReloaded(this);
	}

	@Override
	public void onInitialize() {
		bingo.load();
		bingo.enable();
	}

	@Override
	public DataAccessor getConfigData() {
		return new SnakeYamlDataAccessor(platform, "config");
	}

	@Override
	public Collection<DataAccessor> getDataToRegister() {
		return List.of(
				new SnakeYamlDataAccessor(platform, "scoreboards"),
				new SnakeYamlDataAccessor(platform, "placeholders"),
				new SnakeYamlDataAccessor(platform, "sounds"));
	}

	@Override
	public void setupConfig() {

	}

	@Override
	public Set<EntityType> getValidEntityTypesForStatistics() {
		return Set.of();
	}

	@Override
	public LanguageData getLanguageData(String language) {
		var lang = new SnakeYamlDataAccessor(platform, language);
		var fallback = new SnakeYamlDataAccessor(platform, "languages/en_us");

		BingoReloaded.addDataAccessor(lang);
		BingoReloaded.addDataAccessor(fallback);

		return new LanguageData(lang, fallback);
	}

	@Override
	public void onLanguageUpdated() {

	}

	@Override
	public void onConfigReloaded(BingoConfigurationData config) {

	}

	@Override
	public void registerActions(BingoConfigurationData config) {

	}

	@Override
	public WorldHandle createBingoWorld(String worldName, Key generationOptions) {
		return null;
	}

	@Override
	public ServerSoftware getServerSoftware() {
		return platform;
	}

	@Override
	public CardMenu createMenu(boolean textured, CardDisplayInfo displayMode) {
		return null;
	}

	@Override
	public StackHandle createCardItemForPlayer(BingoParticipant player) {
		return PlayerKit.CARD_ITEM.buildItem();
	}

	@Override
	public void openBingoMenu(PlayerHandle player, BingoSession session) {

	}

	@Override
	public void openTeamEditor(PlayerHandle player) {

	}

	@Override
	public void openBingoCreator(PlayerHandle player) {

	}

	@Override
	public void openTeamCardSelect(PlayerHandle player, BingoSession session) {

	}

	@Override
	public void openTeamSelector(PlayerHandle player, BingoSession session) {

	}

	@Override
	public void openVoteMenu(PlayerHandle player, PregameLobby lobby) {

	}

	@Override
	public TeamDisplay createTeamDisplay(BingoSession session) {
		return null;
	}

	@Override
	public SharedDisplay gameDisplay() {
		return null;
	}

	@Override
	public SharedDisplay settingsDisplay() {
		return null;
	}
}
