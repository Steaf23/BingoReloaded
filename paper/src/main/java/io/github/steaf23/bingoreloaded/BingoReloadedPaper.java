package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.PaperServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.data.core.ConfigDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.util.bstats.Metrics;
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
}
