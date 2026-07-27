package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.api.network.BingoClientManager;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.record.LeaderboardData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.FabricServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.CapacityInventoryProvider;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.EmptyDisplay;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.SnakeYamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.menu.InfoMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BingoReloadedFabric implements ModInitializer, BingoReloadedRuntime {

	private static final String MOD_ID = "bingoreloaded";

	private FabricServerSoftware platform;
	private BingoReloaded bingo;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			this.platform = new FabricServerSoftware(server, MOD_ID);
			PlatformResolver.set(platform);

			this.bingo = new BingoReloaded(this);
			bingo.load();
			bingo.enable();
		});
	}

	@Override
	public DataAccessor getConfigData() {
		DataAccessor config = new SnakeYamlDataAccessor(platform, "config");
		config.load();
		return config;
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
	public @Nullable WorldHandle createBingoOverworld(Key worldKey, Key generationOptions) {
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
	public CapacityInventoryProvider getPouchInventoryProvider() {
		return new CapacityInventoryProvider() {
			@Override
			public void setSlotCount(int slots) {

			}

			@Override
			public void setTitle(Component title) {

			}

			@Override
			public InventoryHandle create() {
				return null;
			}
		};
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
	public void openLeaderboard(PlayerHandle player, LeaderboardData historyData, boolean categorizeByPresets) {

	}

	@Override
	public TeamDisplay createTeamDisplay(BingoSession session) {
		return null;
	}

	@Override
	public SharedDisplay gameDisplay() {
		return new EmptyDisplay();
	}

	@Override
	public SharedDisplay settingsDisplay() {
		return new EmptyDisplay();
	}

	@Override
	public BingoClientManager getClientManager() {
		return null;
	}
}
