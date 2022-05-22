package me.steven.bingoreloaded.data;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.player.PlayerKit;
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
        ALL("all");

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
                        default -> ALL;
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
        this.teleportMaxDistance = config.getInt("teleportMaxDistance");
        this.playerTeleportStrategy = PlayerTeleportStrategy.fromName(config.getString("playerTeleportStrategy"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage");
        this.lobbySpawnHeight = config.getInt("lobbySpawnHeight");
        this.wandUp = config.getInt("GoUpWand.upDistance");
        this.wandDown = config.getInt("GoUpWand.downDistance");
        this.wandCooldown = config.getDouble("GoUpWand.cooldown");
        this.defaultKit = PlayerKit.fromConfig(config.getString("defaultKit"));
    }
}
