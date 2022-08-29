package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class ConfigData
{
    public enum PlayerTeleportStrategy
    {
        ALONE("alone"),
        TEAM("team"),
        ALL("all"),
        NONE("none");

        public final String name;

        PlayerTeleportStrategy(String name)
        {
            this.name = name;
        }

        static PlayerTeleportStrategy fromName(@Nullable String name)
        {
            if (name == null)
                return ALL;
            return switch (name.toLowerCase())
                    {
                        case "alone" -> ALONE;
                        case "team" -> TEAM;
                        case "all" -> ALL;
                        default -> NONE;
                    };
        }
    }

    public final int teleportMaxDistance;
    public final PlayerTeleportStrategy playerTeleportStrategy;
    public final boolean teleportAfterDeath;
    public final int lobbySpawnHeight;
    public final int wandUp;
    public final int wandDown;
    public final double wandCooldown;
    public final PlayerKit defaultKit;
    public final int gracePeriod;
    public final boolean resetPlayerItems;
    public final boolean resetPlayerPositions;
    public final String selectedCard;
    public final String language;

    private static ConfigData INSTANCE;

    public static ConfigData getConfig()
    {
        if (INSTANCE == null)
        {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            INSTANCE = new ConfigData(plugin.getConfig());
        }
        return INSTANCE;
    }

    private ConfigData(FileConfiguration config)
    {
        this.teleportMaxDistance = config.getInt("teleportMaxDistance", 1000000);
        this.playerTeleportStrategy = PlayerTeleportStrategy.fromName(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
        this.lobbySpawnHeight = config.getInt("lobbySpawnHeight", 128);
        this.wandUp = config.getInt("GoUpWand.upDistance", 75);
        this.wandDown = config.getInt("GoUpWand.downDistance", 5);
        this.wandCooldown = config.getDouble("GoUpWand.cooldown", 5.0);
        this.defaultKit = PlayerKit.fromConfig(config.getString("defaultKit", "HARDCORE"));
        this.gracePeriod = config.getInt("gracePeriod", 30);
        this.resetPlayerItems = config.getBoolean("resetPlayerItems", true);
        this.resetPlayerPositions = config.getBoolean("resetPlayerPositions", true);
        this.selectedCard = config.getString("selectedCard", "default_card");
        this.language = config.getString("language", "en_us.yml");
    }
}
