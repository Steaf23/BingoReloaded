package io.github.steaf23.bingoreloaded;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.api.network.BingoClientManager;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.record.LeaderboardData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.gui.inventory.AdminBingoMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.LeaderboardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamCardSelectMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamEditorMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.VoteMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.GenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapGenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.creator.BingoCreatorMenu;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionInfo;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.inventory.CapacityInventoryProvider;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricResources;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricStatics;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricTaskScheduler;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.player.EmptyDisplay;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.SnakeYamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoardFabric;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BingoReloadedFabric implements ModInitializer, BingoReloadedRuntime {

	private static final String MOD_ID = "bingoreloaded";

	private BingoReloaded bingo;
	private FabricResources resources;
	private FabricTaskScheduler tasks;
	private MenuBoard menuBoard;
	private FabricServer server;

	@Override
	public void onInitialize() {
		PlatformResolver.set(new FabricStatics(MOD_ID));

		this.tasks = new FabricTaskScheduler();
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			tasks.tick(server.getTickCount());
		});

		this.resources = new FabricResources(MOD_ID);
		this.bingo = new BingoReloaded(this);
		bingo.load(createExtensionInfo());
		bingo.enable(resources);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			this.server = new FabricServer(server, tasks);
			this.menuBoard = new MenuBoardFabric(new GameContext(this.server, bingo));
			bingo.reloadManager(this.server);
		});
	}

	@Override
	public DataAccessor getConfigData() {
		DataAccessor config = new SnakeYamlDataAccessor(resources, "config");
		config.load();
		return config;
	}

	@Override
	public Collection<DataAccessor> getDataToRegister() {
		return List.of(
				new SnakeYamlDataAccessor(resources, "scoreboards"),
				new SnakeYamlDataAccessor(resources, "placeholders"),
				new SnakeYamlDataAccessor(resources, "sounds"));
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
		var lang = new SnakeYamlDataAccessor(resources, language);
		var fallback = new SnakeYamlDataAccessor(resources, "languages/en_us");

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
	public void registerAction(boolean allowConsole, ActionTree action) {
		CommandRegistrationCallback.EVENT.register(((dispatcher, buildContext, selection) -> {
			dispatcher.register(createActionsRecurse(action, action));
		}));
	}

	LiteralArgumentBuilder<CommandSourceStack> createActionsRecurse(ActionTree mainAction, ActionTree action) {
		var command = Commands.literal(action.name()).executes(ctx -> executeCommand(mainAction, action, ctx));
		for (ActionTree subAction : action.subActions()) {
			command.then(createActionsRecurse(mainAction, subAction));
		}
		return command;
	}

	public int executeCommand(ActionTree mainAction, ActionTree action, CommandContext<CommandSourceStack> context) {
		MinecraftServer server = context.getSource().getServer();
		FabricServer serverWrapper = new FabricServer(server, tasks);
		ActionUser user = new PlayerHandleFabric(serverWrapper, context.getSource().getPlayer());
		mainAction.setLastUser(user);
		action.setLastUser(user);
		action.getAction().execute(new GameContext(serverWrapper, bingo), new String[]{""});

		return Command.SINGLE_SUCCESS;
	}

	@Override
	public void registerExtraActions(BingoConfigurationData config) {
	}

	@Override
	public @Nullable WorldHandle createBingoOverworld(Key worldKey, Key generationOptions) {
		return null;
	}

	@Override
	public CardMenu createMenu(boolean textured, CardDisplayInfo displayInfo) {
		boolean useHotswapMenu = displayInfo.mode() == BingoGamemodes.HOTSWAP || displayInfo.mode() == BingoGamemodes.BLITZ;
		if (useHotswapMenu) {
			return new HotswapGenericCardMenu(bingo, menuBoard, displayInfo, null);
		}

		return new GenericCardMenu(bingo, menuBoard, displayInfo, null);
	}

	@Override
	public StackHandle createCardItemForPlayer(BingoParticipant player) {
		return PlayerKit.CARD_ITEM.buildItem(server);
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
			public InventoryTemplate create() {
				return null;
			}
		};
	}

	@Override
	public void openBingoMenu(PlayerHandle player, BingoSession session) {
		if (BingoReloaded.isHost(player)) {
			new AdminBingoMenu(menuBoard, session).open(player);
		} else if (BingoReloaded.isPlayer(player)) {
			new TeamSelectionMenu(menuBoard, session).open(player);
		}
	}

	@Override
	public void openTeamEditor(PlayerHandle player) {
		new TeamEditorMenu(menuBoard).open(player);
	}

	@Override
	public void openBingoCreator(PlayerHandle player) {
		new BingoCreatorMenu(menuBoard).open(player);
	}

	@Override
	public void openTeamCardSelect(PlayerHandle player, BingoSession session) {
		new TeamCardSelectMenu(menuBoard, session).open(player);
	}

	@Override
	public void openTeamSelector(PlayerHandle player, BingoSession session) {
		new TeamSelectionMenu(menuBoard, session).open(player);
	}

	@Override
	public void openVoteMenu(PlayerHandle player, PregameLobby lobby) {
		new VoteMenu(menuBoard, bingo.config().getOptionValue(BingoOptions.VOTE_LIST), lobby).open(player);
	}

	@Override
	public void openLeaderboard(PlayerHandle player, LeaderboardData historyData, boolean categorizeByPresets) {
		new LeaderboardMenu(menuBoard, historyData, player, categorizeByPresets, bingo.config().getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS)).open(player);
	}

	@Override
	public void editCardDescription(PlayerHandle playerHandle, String currentName, String currentDescription, BasicMenu parentMenu, CardDescriptionEditor callback) {
		callback.edit(playerHandle, parentMenu, currentName, currentName, currentDescription);
	}

	@Override
	public void givePlayerCardItem(PlayerHandle player, int cardSlot, StackHandle stack) {

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

	private @Nullable ExtensionInfo createExtensionInfo() {
		ModContainer container = FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null);
		if (container == null) {
			return null;
		}

		List<String> authors = container.getMetadata().getAuthors().stream()
				.map(Person::getName)
				.toList();

		return new ExtensionInfo(container.getMetadata().getName(), container.getMetadata().getVersion().getFriendlyString(), authors);
	}
}
