package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.core.configuration.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataAccessor;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataUpdaterV1
{
    private final BingoReloaded plugin;

    public DataUpdaterV1(BingoReloaded plugin) {
        this.plugin = plugin;
    }

    @SerializableAs("Bingo.CustomKit")
    public record OldCustomKit(int cardSlot, int kitId, String name,
                               List<OldMenuItem> items) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldCustomKit deserialize(Map<String, Object> values) {
            int slot = (int) values.get("slot");
            int cardSlot = (int) values.get("card_slot");
            String name = (String) values.get("name");
            List<OldMenuItem> items = (List<OldMenuItem>) values.get("items");

            return new OldCustomKit(cardSlot, slot, name, items);
        }
    }

    @SerializableAs("Bingo.MenuItem")
    public record OldMenuItem(int slot, ItemStack stack) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldMenuItem deserialize(Map<String, Object> values) {
            ItemStack stack = (ItemStack) values.get("stack");
            int slot = (int) values.get("slot");
            return new OldMenuItem(slot, stack);
        }
    }

    @SerializableAs("Bingo.ItemTask")
    public record OldItemTask(int count, Material item) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldItemTask deserialize(Map<String, Object> values) {
            Material stack = Material.valueOf((String) values.get("item"));
            int count = (int) values.get("count");
            return new OldItemTask(count, stack);
        }
    }

    @SerializableAs("Bingo.Statistic")
    public record OldStatistic(Statistic stat, EntityType entity,
                               Material material) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldStatistic deserialize(Map<String, Object> values) {
            Statistic stat = Statistic.valueOf((String) values.get("statistic"));
            String matString = (String) values.getOrDefault("item", null);
            String entityString = (String) values.getOrDefault("entity", null);
            Material material = matString == null || matString.isEmpty() ? null : Material.valueOf(matString);
            EntityType entity = entityString == null || entityString.isEmpty() ? null : EntityType.valueOf(entityString);
            return new OldStatistic(stat, entity, material);
        }
    }

    @SerializableAs("Bingo.StatisticTask")
    public record OldStatisticTask(int count, OldStatistic statistic) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldStatisticTask deserialize(Map<String, Object> values) {
            int count = (int) values.get("count");
            OldStatistic statistic = (OldStatistic) values.get("statistic");
            return new OldStatisticTask(count, statistic);
        }
    }

    @SerializableAs("Bingo.AdvancementTask")
    public record OldAdvancementTask(Advancement advancement) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldAdvancementTask deserialize(Map<String, Object> values) {
            NamespacedKey advancementKey = NamespacedKey.fromString((String) values.get("advancement"));
            if (advancementKey == null)
                return new OldAdvancementTask(null);
            Advancement advancement = Registry.ADVANCEMENT.get(advancementKey);
            return new OldAdvancementTask(advancement);
        }
    }

    @SerializableAs("Player")
    public record OldPlayer(String pluginVersion, UUID playerId, Location location,
                            double health, int hunger, GameMode mode, Location spawn, int xpLevel, float xpPoints,
                            ItemStack[] inventory, ItemStack[] enderInventory) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldPlayer deserialize(Map<String, Object> data) {
            return new OldPlayer(
                    (String) data.getOrDefault("version", "-"),
                    UUID.fromString((String) data.getOrDefault("uuid", "")),
                    (Location) data.getOrDefault("location", new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)),
                    (Double) data.getOrDefault("health", 0.0),
                    (Integer) data.getOrDefault("hunger", 0),
                    GameMode.valueOf((String) data.getOrDefault("gamemode", "SURVIVAL")),
                    (Location) data.getOrDefault("spawn_point", new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)),
                    (Integer) data.getOrDefault("xp_level", 0),
                    ((Double) (data.getOrDefault("xp_points", 0.0f))).floatValue(),
                    ((ArrayList<ItemStack>) data.getOrDefault("inventory", null)).toArray(new ItemStack[]{}),
                    ((ArrayList<ItemStack>) data.getOrDefault("ender_inventory", null)).toArray(new ItemStack[]{}));
        }
    }

    @SerializableAs("BingoSettings")
    public record OldBingoSettings(String card,
                                   BingoGamemode mode,
                                   CardSize size,
                                   int seed,
                                   PlayerKit kit,
                                   EnumSet<EffectOptionFlags> effects,
                                   int maxTeamSize,
                                   boolean enableCountdown,
                                   int countdownDuration,
                                   int hotswapGoal) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldBingoSettings deserialize(Map<String, Object> data) {
            EnumSet<EffectOptionFlags> result = EnumSet.noneOf(EffectOptionFlags.class);
            ((List<String>) data.get("effects")).forEach(entry -> result.add(Enum.valueOf(EffectOptionFlags.class, entry)));
            return new OldBingoSettings(
                    (String) data.get("card"),
                    BingoGamemode.fromDataString((String) data.get("mode")),
                    CardSize.fromWidth((int) data.get("size")),
                    (int) data.get("seed"),
                    PlayerKit.fromConfig((String) data.get("kit")),
                    result,
                    (int) data.get("team_size"),
                    (boolean) data.get("countdown"),
                    (int) data.get("duration"),
                    (int) data.getOrDefault("hotswap_goal", 10)
            );
        }
    }

    @SerializableAs("TeamTemplate")
    public record OldTeamTemplate(String name, String hexColor) implements ConfigurationSerializable
    {
        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }

        public static OldTeamTemplate deserialize(Map<String, Object> data) {
            return new OldTeamTemplate((String) data.get("name"), (String) data.get("color"));
        }
    }

    public void update() {
        updateCards();
//        updateTextures();
        updateKits();
        updateLists("data/lists_1_21");
        updateStats();
        updatePlayers();
        updatePresets();
        updateTeams();
    }

    private void updatePresets() {
        if (!new File(plugin.getDataFolder(), "data/presets.yml").exists() || new File(plugin.getDataFolder(), "data/presets.nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldBingoSettings.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/presets");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/presets");

        yamlData.load();
        for (String key : yamlData.getKeys()) {
            if (key.equals("default")) {
                tagData.setString("default", yamlData.getString("default", "default_settings"));
            } else {
                OldBingoSettings oldSettings = yamlData.getSection().getSerializable(key, OldBingoSettings.class);
                if (oldSettings == null) continue;
                tagData.setSerializable("presets." + key, BingoSettings.class, new BingoSettings(
                        oldSettings.card(),
                        oldSettings.mode(),
                        oldSettings.size(),
                        oldSettings.seed(),
                        oldSettings.kit(),
                        oldSettings.effects(),
                        oldSettings.maxTeamSize(),
                        oldSettings.enableCountdown(),
                        oldSettings.countdownDuration(),
                        oldSettings.hotswapGoal()
                ));
            }
        }

        tagData.saveChanges();

        ConfigurationSerialization.unregisterClass(OldBingoSettings.class);

        ConsoleMessenger.log(Component.text("Found outdated settings preset configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updatePlayers() {
        if (!new File(plugin.getDataFolder(), "data/players.yml").exists() || new File(plugin.getDataFolder(), "data/players.nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldPlayer.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/players");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/players");

        yamlData.load();

        for (String id : yamlData.getKeys()) {
            OldPlayer player = yamlData.getSection().getSerializable(id, OldPlayer.class);
            if (player == null) {
                continue;
            }
            SerializablePlayer newPlayer = new SerializablePlayer();
            newPlayer.pluginVersion = player.pluginVersion();
            newPlayer.location = player.location();
            newPlayer.playerId = player.playerId();
            newPlayer.health = player.health();
            newPlayer.hunger = player.hunger();
            newPlayer.gamemode = player.mode();
            newPlayer.spawnPoint = player.spawn();
            newPlayer.xpLevel = player.xpLevel();
            newPlayer.xpPoints = player.xpPoints();
            newPlayer.inventory = player.inventory();
            newPlayer.enderInventory = player.enderInventory();
            tagData.setSerializable(id, SerializablePlayer.class, newPlayer);
        }

        tagData.saveChanges();

        ConfigurationSerialization.unregisterClass(OldPlayer.class);

        ConsoleMessenger.log(Component.text("Found outdated stored-players configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateStats() {
        if (!new File(plugin.getDataFolder(), "data/player_stats.yml").exists() || new File(plugin.getDataFolder(), "data/player_stats.nbt").exists()) {
            return;
        }

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/player_stats");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/player_stats");

        yamlData.load();
        for (String id : yamlData.getKeys()) {
            tagData.setString(id, yamlData.getString(id, "0;0;0;0;0"));
        }
        tagData.saveChanges();

        ConsoleMessenger.log(Component.text("Found outdated player stats configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateLists(String filename) {
        if (!new File(plugin.getDataFolder(), filename + ".yml").exists() || new File(plugin.getDataFolder(),filename + ".nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldItemTask.class);
        ConfigurationSerialization.registerClass(OldStatistic.class);
        ConfigurationSerialization.registerClass(OldStatisticTask.class);
        ConfigurationSerialization.registerClass(OldAdvancementTask.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, filename);
        TagDataAccessor tagData = new TagDataAccessor(plugin, filename);

        yamlData.load();

        for (String list : yamlData.getKeys()) {
            tagData.setInt(list + ".size", yamlData.getInt(list + ".size", 0));

            List<Object> tasks = (List<Object>) yamlData.getSection().getList(list + ".tasks");
            if (tasks == null) continue;

            List<TaskData> newTasks = new ArrayList<>();
            for (Object task : tasks) {
                if (task instanceof OldItemTask itemTask) {
                    newTasks.add(new ItemTask(itemTask.item, itemTask.count));
                }
                if (task instanceof OldStatisticTask statisticTask) {
                    newTasks.add(new StatisticTask(
                            new BingoStatistic(
                                    statisticTask.statistic.stat,
                                    statisticTask.statistic.entity,
                                    statisticTask.statistic.material),
                            statisticTask.count));
                }
                if (task instanceof OldAdvancementTask advancementTask) {
                    newTasks.add(new AdvancementTask(advancementTask.advancement));
                }
            }
            tagData.setSerializableList(list + ".tasks", TaskData.class, newTasks);
        }

        tagData.saveChanges();

        ConfigurationSerialization.unregisterClass(OldItemTask.class);
        ConfigurationSerialization.unregisterClass(OldStatistic.class);
        ConfigurationSerialization.unregisterClass(OldStatisticTask.class);
        ConfigurationSerialization.unregisterClass(OldAdvancementTask.class);

        ConsoleMessenger.log(Component.text("Found outdated list configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateCards() {
        if (!new File(plugin.getDataFolder(), "data/cards.yml").exists() || new File(plugin.getDataFolder(), "data/cards.nbt").exists()) {
            return;
        }

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/cards");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/cards");

        yamlData.load();

        for (String cardName : yamlData.getKeys()) {
            for (String listName : yamlData.getStorage(cardName).getKeys()) {
                tagData.setByte(cardName + "." + listName + ".min", (byte) yamlData.getInt(cardName + "." + listName + ".min", -1));
                tagData.setByte(cardName + "." + listName + ".max", (byte) yamlData.getInt(cardName + "." + listName + ".max", -1));
            }
        }
        tagData.saveChanges();

        ConsoleMessenger.log(Component.text("Found outdated card configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateKits() {
        if (!new File(plugin.getDataFolder(), "data/kits.yml").exists() || new File(plugin.getDataFolder(), "data/kits.nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldMenuItem.class);
        ConfigurationSerialization.registerClass(OldCustomKit.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/kits");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/kits");

        yamlData.load();

        for (String kit : yamlData.getKeys()) {
            OldCustomKit oldKit = yamlData.getSection().getSerializable(kit, OldCustomKit.class);
            if (oldKit == null) continue;

            tagData.setByte(kit + ".kit_id", (byte) oldKit.kitId);
            tagData.setByte(kit + ".card_slot", (byte) oldKit.cardSlot);
            tagData.setString(kit + ".name", oldKit.name);

            tagData.setSerializableList(kit + ".items", SerializableItem.class, oldKit.items.stream()
                    .map(old -> new SerializableItem(old.slot, old.stack))
                    .toList());
        }

        tagData.saveChanges();
        ConfigurationSerialization.unregisterClass(OldMenuItem.class);
        ConfigurationSerialization.unregisterClass(OldCustomKit.class);

        ConsoleMessenger.log(Component.text("Found outdated kit configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateTeams() {
        if (!new File(plugin.getDataFolder(), "data/teams.yml").exists() || new File(plugin.getDataFolder(), "data/teams.nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldTeamTemplate.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/teams");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/teams");

        yamlData.load();

        for (String key : yamlData.getKeys()) {
            OldTeamTemplate template = yamlData.getSection().getSerializable(key, OldTeamTemplate.class);
            if (template == null) {
                continue;
            }
            tagData.setSerializable(key, TeamData.TeamTemplate.class, new TeamData.TeamTemplate(template.name(), TextColor.fromHexString(template.hexColor())));
        }

        tagData.saveChanges();

        ConfigurationSerialization.unregisterClass(OldTeamTemplate.class);

        ConsoleMessenger.log(Component.text("Found outdated teams configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateTextures() {
        YamlDataAccessor yamlData = new YamlDataAccessor(plugin, "data/textures");
        TagDataAccessor tagData = new TagDataAccessor(plugin, "data/textures");

        yamlData.load();

        for (String texture : yamlData.getKeys()) {
            ConsoleMessenger.log(yamlData.getString(texture + ".char", ""));
            ConsoleMessenger.log("" + yamlData.getInt(texture + ".texture_end", -1));
            tagData.setString(texture + ".char", yamlData.getString(texture + ".char", ""));
            tagData.setInt(texture + ".texture_end", yamlData.getInt(texture + ".texture_end", -1));
            if (yamlData.contains(texture + ".menu_offset")) {
                tagData.setInt(texture + ".menu_offset", yamlData.getInt(texture + ".menu_offset", -1));
            }
        }
        tagData.saveChanges();
    }
}
