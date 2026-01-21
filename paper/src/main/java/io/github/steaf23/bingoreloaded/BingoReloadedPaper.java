package io.github.steaf23.bingoreloaded;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerShowDialog;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.steaf23.bingoreloaded.action.AutoBingoAction;
import io.github.steaf23.bingoreloaded.action.BingoActionPaper;
import io.github.steaf23.bingoreloaded.action.BingoConfigAction;
import io.github.steaf23.bingoreloaded.action.BotCommandAction;
import io.github.steaf23.bingoreloaded.action.CommandTemplate;
import io.github.steaf23.bingoreloaded.action.TeamChatCommand;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.api.TeamDisplayPaper;
import io.github.steaf23.bingoreloaded.api.network.BingoClientManager;
import io.github.steaf23.bingoreloaded.api.network.PaperClientManager;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.DataUpdaterV3_5_0;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoInteraction;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.gui.BingoCardMapRenderer;
import io.github.steaf23.bingoreloaded.gui.inventory.AdminBingoMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamCardSelectMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamEditorMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.VoteMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.GenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapGenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapTexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.TexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.core.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.core.MenuBoardPaper;
import io.github.steaf23.bingoreloaded.gui.inventory.creator.BingoCreatorMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.item.MinecraftBingoItems;
import io.github.steaf23.bingoreloaded.item.GameItem;
import io.github.steaf23.bingoreloaded.item.GoUpWand;
import io.github.steaf23.bingoreloaded.item.TeamShulker;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PaperServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.PlayerInput;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.EmptyDisplay;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.ConfigDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.events.EventListenerPaper;
import io.github.steaf23.bingoreloaded.lib.menu.ScoreboardDisplay;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.LoggerWrapper;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import io.github.steaf23.bingoreloaded.placeholder.BingoReloadedPlaceholderExpansion;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.util.bstats.Metrics;
import io.github.steaf23.bingoreloaded.world.CustomWorldCreator;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BingoReloadedPaper extends JavaPlugin implements BingoReloadedRuntime {

	private PaperServerSoftware platform;
	private BingoReloaded bingo;
	private MenuBoard menuBoard;
	private EventListenerPaper eventListener;
	private SharedDisplay gameDisplay;
	private SharedDisplay settingsDisplay;
	private BingoClientManager clientManager;

	public BingoReloadedPaper() {
	}

	@Override
	public void onLoad() {
		this.platform = new PaperServerSoftware(this, new LoggerWrapper() {

			@Override
			public void info(Component message) {
				getComponentLogger().info(message);
			}

			@Override
			public void warn(Component message) {
				getComponentLogger().warn(message);
			}

			@Override
			public void error(Component message) {
				getComponentLogger().error(message);
			}
		});
		PlatformResolver.set(platform);

		PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
		PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
				.checkForUpdates(true);
		PacketEvents.getAPI().load();


		this.bingo = new BingoReloaded(this);

		platform.saveResource("bingoreloaded.zip", true);
		platform.saveResource("bingoreloaded_lite.zip", true);

		bingo.load();

		// Data file updater (backwards compatibility)
		{
			DataUpdaterV3_5_0 updater = new DataUpdaterV3_5_0(this);
			updater.update();
		}
	}

	@Override
	public void onEnable() {
		this.menuBoard = new MenuBoardPaper(platform, this);

		bingo.enable();
		bingo.serverReady();

		if (bingo.config().getOptionValue(BingoOptions.DISABLE_CLIENT_MOD)) {
			this.clientManager = new BingoClientManager.DisabledClientManager();
		} else {
			this.clientManager = new PaperClientManager(this, bingo);
		}

		// Setup PlaceholderAPI
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new BingoReloadedPlaceholderExpansion(platform, bingo).register();

			BingoMessage.setMessagePreParser( (player, message) -> {
				if (player == null) {
					return message;
				}
				return PlaceholderAPI.setPlaceholders(((PlayerHandlePaper)player).handle(), message);
			});
		}

		eventListener = new EventListenerPaper(this, bingo.getGameManager().eventListener());

//		menuBoard.setPlayerOpenPredicate(player -> player instanceof PlayerHandle handle && this.gameManager.canPlayerOpenMenus(handle));

		Metrics bStatsMetrics = new Metrics(this, 22586);
		bStatsMetrics.addCustomChart(new Metrics.SimplePie("selected_language",
				() -> bingo.config().getOptionValue(BingoOptions.LANGUAGE).replace(".yml", "").replace("languages/", "")));
		bStatsMetrics.addCustomChart(new Metrics.SimplePie("plugin_configuration",
				() -> bingo.config().getOptionValue(BingoOptions.CONFIGURATION) == BingoOptions.PluginConfiguration.SINGULAR ? "Singular" : "Multiple"));
	}

	@Override
	public void onDisable() {
		bingo.disable();
	}

	@Override
	public void onConfigReloaded(BingoConfigurationData config) {
		if (gameDisplay != null) {
			gameDisplay.clearPlayers();
		}
		if (settingsDisplay != null) {
			settingsDisplay.clearPlayers();
		}

		if (bingo.config().getOptionValue(BingoOptions.DISABLE_SCOREBOARD_SIDEBAR)) {
			gameDisplay = new EmptyDisplay();
			settingsDisplay = new EmptyDisplay();
		} else {
			gameDisplay = new ScoreboardDisplay("game");
			settingsDisplay = new ScoreboardDisplay("lobby");
		}
	}

	@Override
	public DataAccessor getConfigData() {
		return new ConfigDataAccessor(platform);
	}

	@Override
	public Collection<DataAccessor> getDataToRegister() {
		return List.of(
				new YamlDataAccessor(platform, "scoreboards", false),
				new YamlDataAccessor(platform, "placeholders", false),
				new YamlDataAccessor(platform, "sounds", false));
	}

	@Override
	public void setupConfig() {
		saveConfig();
		saveDefaultConfig();

		// load default config
		YamlConfiguration defaultConfigFull = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("config.yml")));

		// load current user config to copy values from
		FileConfiguration userConfig = getConfig();

		for (String key : userConfig.getKeys(true)) {
			if (defaultConfigFull.contains(key)) {
				defaultConfigFull.set(key, userConfig.get(key));
			}
		}

		defaultConfigFull.set("version", getPluginMeta().getVersion());

		try {
			defaultConfigFull.save(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			ConsoleMessenger.bug("Could not update config.yml to new version", this);
		}
	}

	@Override
	public Set<EntityType> getValidEntityTypesForStatistics() {
		Set<EntityType> types = new HashSet<>();
		Arrays.stream(Material.values())
				.forEach(mat -> {
					if (mat.name().contains("_SPAWN_EGG")) {
						types.add(new EntityTypePaper(org.bukkit.entity.EntityType.valueOf(mat.name().replace("_SPAWN_EGG", ""))));
					}
				});
		return types;
	}

	@Override
	public LanguageData getLanguageData(String language) {
		var lang = new YamlDataAccessor(platform, language, false);
		var fallback = new YamlDataAccessor(platform, "languages/en_us", false);

		BingoReloaded.addDataAccessor(lang);
		BingoReloaded.addDataAccessor(fallback);

		return new LanguageData(lang, fallback);
	}

	@Override
	public void onLanguageUpdated() {
		PlayerDisplayTranslationKey.setTranslateFunction(key -> switch(key) {
			case MENU_PREVIOUS -> BingoMessage.MENU_PREV.asPhrase();
			case MENU_NEXT -> BingoMessage.MENU_NEXT.asPhrase();
			case MENU_ACCEPT -> BingoMessage.MENU_ACCEPT.asPhrase();
			case MENU_SAVE_EXIT -> BingoMessage.MENU_SAVE_EXIT.asPhrase();
			case MENU_FILTER -> BingoMessage.MENU_FILTER.asPhrase();
			case MENU_CLEAR_FILTER -> BingoMessage.MENU_CLEAR_FILTER.asPhrase();
		});

		BasicMenu.pluginTitlePrefix = BingoMessage.MENU_PREFIX.asPhrase();
	}

	@Override
	public void registerActions(BingoConfigurationData config) {
		registerCommand(true, new AutoBingoAction(platform, bingo.getGameManager()));
		registerCommand(true, new BingoConfigAction(config));
		registerCommand(false, new BingoActionPaper(bingo, config, bingo.getGameManager()));
		registerCommand(false, new BotCommandAction(bingo.getGameManager()));
//		registerCommand("bingotest", new BingoTestCommand(this));
		if (config.getOptionValue(BingoOptions.ENABLE_TEAM_CHAT)) {
			TeamChatCommand command = new TeamChatCommand(player -> bingo.getGameManager().getSessionFromWorld(player.world()));
			registerCommand(false, command);
			Bukkit.getPluginManager().registerEvents(command, this);
		}
	}

	@Override
	public WorldHandle createBingoWorld(String worldName, Key generationOptions) {
		return CustomWorldCreator.createWorld(platform, worldName, generationOptions);
	}

	@Override
	public ServerSoftware getServerSoftware() {
		return platform;
	}

	public StackHandle createCardItemForPlayer(BingoParticipant player) {
		if (player.sessionPlayer().isEmpty() || player.getCard().isEmpty() && player.getTeam() != null) {
			return MinecraftBingoItems.CARD_ITEM.buildItem();
		}

		PlayerHandle playerHandle = player.sessionPlayer().get();

		if (!bingo.config().getOptionValue(BingoOptions.USE_MAP_RENDERER) || clientManager.playerHasClient(playerHandle)) {
			return MinecraftBingoItems.CARD_ITEM.buildItem();
		}

		StackHandlePaper mapStack = (StackHandlePaper) MinecraftBingoItems.CARD_ITEM_RENDERABLE.buildItem();

		ItemStack handle = mapStack.handle();
		handle.editMeta(m -> {
			if (m instanceof MapMeta meta) {
				MapView view = Bukkit.createMap(((WorldHandlePaper) playerHandle.world()).handle());
				for (var renderer : new ArrayList<>(view.getRenderers())) {
					view.removeRenderer(renderer);
				}

				view.addRenderer(new BingoCardMapRenderer(platform, player.getCard().get(), player.getTeam()));
				meta.setMapView(view);
			} else {
				ConsoleMessenger.bug("No valid map item found to render texture to.", this);
			}
		});

		return mapStack;
	}

	@Override
	public CardMenu createMenu(boolean textured, CardDisplayInfo displayInfo) {
		if (textured) {
			if (displayInfo.mode() == BingoGamemode.HOTSWAP) {
				return new HotswapTexturedCardMenu(bingo, menuBoard, displayInfo);
			}
			return new TexturedCardMenu(bingo, menuBoard, displayInfo);
		}

		if (displayInfo.mode() == BingoGamemode.HOTSWAP) {
			return new HotswapGenericCardMenu(bingo, menuBoard, displayInfo, null);
		}

		return new GenericCardMenu(bingo, menuBoard, displayInfo, null);
	}

	@Override
	public void setPlayerUpForGame(BingoGame game, BingoParticipant participant) {
		if (participant.sessionPlayer().isEmpty())
			return;

		PlayerHandle player = participant.sessionPlayer().get();
		int cardSlot = game.getSettings().kit().getCardSlot();

		platform.runTask(player.world().uniqueId(), task -> {
			for (StackHandle itemStack : player.inventory().contents()) {
				if (MinecraftBingoItems.CARD_ITEM.isCompareKeyEqual(itemStack)) {
					player.inventory().removeItem(itemStack);
					break;
				}
			}
			StackHandle existingItem = player.inventory().getItem(cardSlot);

			player.inventory().setItem(cardSlot, createCardItemForPlayer(participant));
			if (!existingItem.type().isAir()) {
				Map<Integer, StackHandle> leftOver = player.inventory().addItem(existingItem);
				for (StackHandle stack : leftOver.values()) {
					player.world().dropItem(stack, player.position());
				}
			}
		});
	}

	@Override
	public void openBingoMenu(PlayerHandle player, BingoSession session) {
		if (player.hasPermission("bingo.admin")) {
			new AdminBingoMenu(menuBoard, session).open(player);
		} else if (player.hasPermission("bingo.player")) {
			new TeamSelectionMenu(menuBoard, session).open(player);
		}
	}

	@Override
	public void openTeamEditor(PlayerHandle player) {
		new TeamEditorMenu(menuBoard).open(player);
	}

	@Override
	public void openTeamCardSelect(PlayerHandle player, BingoSession session) {
		new TeamCardSelectMenu(menuBoard, session).open(player);
	}

	@Override
	public void openBingoCreator(PlayerHandle player) {
		new BingoCreatorMenu(menuBoard).open(player);
	}

	@Override
	public void openTeamSelector(PlayerHandle player, BingoSession session) {
		TeamSelectionMenu menu = new TeamSelectionMenu(menuBoard, session);
		menu.open(player);
	}

	@Override
	public void openVoteMenu(PlayerHandle player, PregameLobby lobby) {
		VoteMenu menu = new VoteMenu(menuBoard, bingo.config().getOptionValue(BingoOptions.VOTE_LIST), lobby);
		menu.open(player);
	}

	@Override
	public TeamDisplay createTeamDisplay(BingoSession session) {
		return new TeamDisplayPaper(session);
	}

	@Override
	public SharedDisplay gameDisplay() {
		return gameDisplay;
	}

	@Override
	public SharedDisplay settingsDisplay() {
		return settingsDisplay;
	}

	@Override
	public BingoClientManager getClientManager() {
		return clientManager;
	}

	@Override
	public StackHandle defaultStack(GameItem item) {
		if (item.key().equals(GoUpWand.ID)) {
			return MinecraftBingoItems.GO_UP_WAND.buildItem();
		}
		if (item.key().equals(TeamShulker.ID)) {
			return MinecraftBingoItems.TEAM_SHULKER.buildItem();
		}

		return StackHandle.empty();
	}

	@Override
	public void playerJoinedLobby(BingoSession session, PlayerHandle player) {
		BingoConfigurationData config = bingo.config();

		if (config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM) &&
				!config.getOptionValue(BingoOptions.VOTE_USING_COMMANDS_ONLY) &&
				!config.getOptionValue(BingoOptions.VOTE_LIST).isEmpty()) {
			player.inventory().addItem(MinecraftBingoItems.VOTE_ITEM.buildItem());
		}
		if (!config.getOptionValue(BingoOptions.SELECT_TEAMS_USING_COMMANDS_ONLY)) {
			player.inventory().addItem(MinecraftBingoItems.TEAM_ITEM.buildItem());
		}
	}

	@Override
	public void droppedItemsOnDeath(BingoSession session, PlayerHandle player, Collection<StackHandle> items) {
		for (StackHandle drop : items) {
			var data = drop.getStorage();
			if (data.getBoolean("kit_item", false)
					|| MinecraftBingoItems.CARD_ITEM.isCompareKeyEqual(drop)) {
				drop.setAmount(0);
			}
		}
	}

	@Override
	public boolean canItemBeUsedForInteraction(BingoSession session, PlayerHandle player, BingoInteraction interaction, StackHandle stack, PlayerInput input) {
		return switch (interaction) {
			case OPEN_CARD -> MinecraftBingoItems.CARD_ITEM.isCompareKeyEqual(stack);
			case START_VOTE -> MinecraftBingoItems.VOTE_ITEM.isCompareKeyEqual(stack);
			case SELECT_TEAM -> MinecraftBingoItems.TEAM_ITEM.isCompareKeyEqual(stack);
			case CANCEL_ITEM_DROP ->
					MinecraftBingoItems.CARD_ITEM.isCompareKeyEqual(stack) ||
					MinecraftBingoItems.VOTE_ITEM.isCompareKeyEqual(stack) ||
					MinecraftBingoItems.TEAM_ITEM.isCompareKeyEqual(stack);
		};
	}

	@Override
	public boolean canItemBeUsedInKit(StackHandle stack) {
		return !MinecraftBingoItems.CARD_ITEM.isCompareKeyEqual(stack);
	}

	public void registerCommand(boolean allowConsole, ActionTree action) {
		TabExecutor commandExec = new CommandTemplate(allowConsole, action);

		PluginCommand command = getCommand(action.name());
		if (command != null) {
			command.setExecutor(commandExec);
			command.setTabCompleter(commandExec);
		} else {
			ConsoleMessenger.bug("Cannot register command named '" + action.name() + "'", this);
		}
	}

	public static void showPacketDialog(PlayerHandle player, Dialog dialog) {
		WrapperPlayServerShowDialog dialogWrapper = new WrapperPlayServerShowDialog(dialog);
		PacketEvents.getAPI().getPlayerManager().sendPacket(((PlayerHandlePaper) player).handle(), dialogWrapper);
	}

}
