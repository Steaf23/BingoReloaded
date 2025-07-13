package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.AutoBingoAction;
import io.github.steaf23.bingoreloaded.command.BingoAction;
import io.github.steaf23.bingoreloaded.command.BingoConfigAction;
import io.github.steaf23.bingoreloaded.command.BingoTestCommand;
import io.github.steaf23.bingoreloaded.command.CommandTemplate;
import io.github.steaf23.bingoreloaded.command.TeamChatCommand;
import io.github.steaf23.bingoreloaded.data.DataUpdaterV1;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.PaperServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.ConfigDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.util.bstats.Metrics;
import io.github.steaf23.bingoreloaded.world.CustomWorldCreator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

public class BingoReloadedPaper extends JavaPlugin implements BingoReloadedRuntime {

	private final PaperServerSoftware platform;
	private final BingoReloaded bingo;

	public BingoReloadedPaper() {
		this.platform = new PaperServerSoftware(this);
		PlatformResolver.set(new PaperServerSoftware(this));

		this.bingo = new BingoReloaded(this);
	}

	@Override
	public void onLoad() {
		// Data file updaters
		{
			DataUpdaterV1 updater = new DataUpdaterV1(this);
			updater.update();
		}

		bingo.load();
	}

	@Override
	public void onEnable() {
		bingo.enable();

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
	public LanguageData getLanguageData(String language) {
		return new LanguageData(
				new YamlDataAccessor(platform, language, false),
				new YamlDataAccessor(platform, "languages/en_us", false));
	}

	@Override
	public void registerActions(BingoConfigurationData config) {
		registerCommand(true, new AutoBingoAction(platform, bingo.getGameManager()));
		registerCommand(true, new BingoConfigAction(config));
		registerCommand(false, new BingoAction(bingo, config, bingo.getGameManager(), menuboard));
//		registerCommand("bingotest", new BingoTestCommand(this));
		if (config.getOptionValue(BingoOptions.ENABLE_TEAM_CHAT)) {
			TeamChatCommand command = new TeamChatCommand(player -> bingo.getGameManager().getSessionFromWorld(player.world()));
			registerCommand("btc", command);
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


}
