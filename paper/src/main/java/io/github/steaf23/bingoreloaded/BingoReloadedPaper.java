package io.github.steaf23.bingoreloaded;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerShowDialog;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.action.AutoBingoAction;
import io.github.steaf23.bingoreloaded.action.BingoAction;
import io.github.steaf23.bingoreloaded.action.BingoConfigAction;
import io.github.steaf23.bingoreloaded.action.BotCommandAction;
import io.github.steaf23.bingoreloaded.action.CommandTemplate;
import io.github.steaf23.bingoreloaded.action.TeamChatCommand;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.DataUpdaterV1;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.api.TeamDisplayPaper;
import io.github.steaf23.bingoreloaded.gui.inventory.AdminBingoMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamCardSelectMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamEditorMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.TeamSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.VoteMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.GenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapGenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapTexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.TexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.creator.BingoCreatorMenu;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PaperServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.ConfigDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.events.EventListenerPaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoardPaper;
import io.github.steaf23.bingoreloaded.lib.menu.ScoreboardDisplay;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.util.bstats.Metrics;
import io.github.steaf23.bingoreloaded.world.CustomWorldCreator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BingoReloadedPaper extends JavaPlugin implements BingoReloadedRuntime {

	private PaperServerSoftware platform;
	private BingoReloaded bingo;
	private MenuBoard menuBoard;
	private EventListenerPaper eventListener;
	private SharedDisplay gameDisplay;
	private SharedDisplay settingsDisplay;

	public BingoReloadedPaper() {
	}

	@Override
	public void onLoad() {
		this.platform = new PaperServerSoftware(this);
		PlatformResolver.set(new PaperServerSoftware(this));

		// Data file updaters
		{
			DataUpdaterV1 updater = new DataUpdaterV1(this);
			updater.update();
		}

		PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
		PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
				.checkForUpdates(true);
		PacketEvents.getAPI().load();

		this.bingo = new BingoReloaded(this);

		bingo.load();
	}

	@Override
	public void onEnable() {
		this.menuBoard = new MenuBoardPaper(platform, this);

		gameDisplay = new ScoreboardDisplay("game");
		settingsDisplay = new ScoreboardDisplay("lobby");

		bingo.enable();

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
	public DataAccessor getConfigData() {
		return new ConfigDataAccessor(platform);
	}

	@Override
	public Collection<DataAccessor> getDataToRegister() {
		return List.of(
				new YamlDataAccessor(platform, "scoreboards", false),
				new YamlDataAccessor(platform, "placeholders", false));
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
		registerCommand(false, new BingoAction(bingo, config, bingo.getGameManager()));
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

	@Override
	public CardMenu createMenu(boolean textured, CardDisplayInfo displayInfo) {
		if (textured) {
			if (displayInfo.mode() == BingoGamemode.HOTSWAP) {
				return new HotswapTexturedCardMenu(menuBoard, displayInfo);
			}
			return new TexturedCardMenu(menuBoard, displayInfo);
		}

		if (displayInfo.mode() == BingoGamemode.HOTSWAP) {
			return new HotswapGenericCardMenu(bingo, menuBoard, displayInfo, null);
		}

		return new GenericCardMenu(bingo, menuBoard, displayInfo, null);
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

	public static boolean doesAudienceHavePermission(Audience audience, String permission) {
		if (audience instanceof CommandSender sender) {
			return sender.hasPermission(permission);
		}
		else {
			return false;
		}
	}

	public static void showPacketDialog(PlayerHandle player, Dialog dialog) {
		WrapperPlayServerShowDialog dialogWrapper = new WrapperPlayServerShowDialog(dialog);
		PacketEvents.getAPI().getPlayerManager().sendPacket(((PlayerHandlePaper) player).handle(), dialogWrapper);
	}

}
