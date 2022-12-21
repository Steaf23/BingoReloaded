package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.Message;
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

    public int teleportMaxDistance;
    public PlayerTeleportStrategy playerTeleportStrategy;
    public boolean teleportAfterDeath;
    public int lobbySpawnHeight;
    public int wandUp;
    public int wandDown;
    public double wandCooldown;
    public int platformLifetime;
    public PlayerKit defaultKit;
    public int gracePeriod;
    public boolean resetPlayerItems;
    public boolean resetPlayerPositions;
    public String selectedCard;
    public String language;

    public static final ConfigData instance = new ConfigData();

    public void loadConfig(FileConfiguration config)
    {
        this.language = "languages/" + config.getString("language", "en_us.yml");
        this.teleportMaxDistance = config.getInt("teleportMaxDistance", 1000000);
        this.playerTeleportStrategy = PlayerTeleportStrategy.fromName(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
        this.lobbySpawnHeight = config.getInt("lobbySpawnHeight", 128);
        this.wandUp = config.getInt("GoUpWand.upDistance", 75);
        this.wandDown = config.getInt("GoUpWand.downDistance", 5);
        this.wandCooldown = config.getDouble("GoUpWand.cooldown", 5.0);
        this.platformLifetime = config.getInt("GoUPWand.platformLifetime", 10);
        this.defaultKit = PlayerKit.fromConfig(config.getString("defaultKit", "HARDCORE"));
        this.gracePeriod = config.getInt("gracePeriod", 30);
        this.resetPlayerItems = config.getBoolean("resetPlayerItems", true);
        this.resetPlayerPositions = config.getBoolean("resetPlayerPositions", true);
        this.selectedCard = config.getString("selectedCard", "default_card");

        Message.log("" + this.teleportMaxDistance);
        Message.log("" + this.playerTeleportStrategy);
        Message.log("" + this.teleportAfterDeath);
        Message.log("" + this.lobbySpawnHeight);
        Message.log("" + this.wandUp);
        Message.log("" + this.wandDown);
        Message.log("" + this.wandCooldown);
        Message.log("" + this.platformLifetime);
        Message.log("" + this.defaultKit);
        Message.log("" + this.gracePeriod);
        Message.log("" + this.resetPlayerItems);
        Message.log("" + this.resetPlayerPositions);
        Message.log("" + this.selectedCard);
        Message.log("" + this.language);
    }
}
