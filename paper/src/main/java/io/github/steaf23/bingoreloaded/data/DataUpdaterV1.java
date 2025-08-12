package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloadedPaper;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.PaperApiHelper;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.data.core.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataUpdaterV1
{
    private final BingoReloadedPaper plugin;
    private final ServerSoftware server;

    public DataUpdaterV1(BingoReloadedPaper plugin) {
        this.plugin = plugin;
        this.server = plugin.getServerSoftware();
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
            Advancement advancement = Bukkit.getAdvancement(advancementKey);
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
        updateConfig();
        updateCards();
//        updateTextures();
        updateKits();
        updateLists("data/lists_1_21");
        updateStats();
        updatePlayers();
        updatePresets();
        updateTeams();
        updateScoreboards();
        updatePlaceholders();
    }

    private void updateConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            return;
        }

        YamlConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);
        String version = existingConfig.getString("version");

        if (isNewerOrEqual(version, "3.1.0")) {
            return;
        }

        if (!existingConfig.contains("voteList.gamemodes")) {
            return;
        }

        // convert old gamemodes to gamemode and card size votes (regular_5 -> regular mode and cardsize 5

        // Add to existing set of card sizes.
        Set<String> cardSizes = new HashSet<>(existingConfig.getStringList("voteList.cardsizes"));
        // Replace existing modes with new names.
        Set<String> modes = new HashSet<>();
        for (String mode : existingConfig.getStringList("voteList.gamemodes")) {
            String[] split = mode.split("_");
            if (split.length != 2) {
                continue;
            }

            mode = split[0];
            String size = split[1];
            cardSizes.add(size);
            modes.add(mode);
        }

        FileConfiguration config = plugin.getConfig();
        config.set("voteList.gamemodes", new ArrayList<>(modes));
        config.set("voteList.cardsizes", new ArrayList<>(cardSizes));
        try {
            config.save(configFile);
        } catch (IOException e) {
            ConsoleMessenger.bug("Could not update config.yml to new version", this);
        }
        ConsoleMessenger.log(Component.text("Found outdated config.yml file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updatePresets() {
        if (!new File(plugin.getDataFolder(), "data/presets.yml").exists() || new File(plugin.getDataFolder(), "data/presets.nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldBingoSettings.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/presets", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/presets", false);

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
                        oldSettings.enableCountdown() ? BingoSettings.CountdownType.DURATION : BingoSettings.CountdownType.DISABLED,
                        oldSettings.countdownDuration(),
                        oldSettings.hotswapGoal(),
                        true,
                        oldSettings.size.fullCardSize,
                        false
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

        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/players", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/players", false);

        yamlData.load();

        for (String id : yamlData.getKeys()) {
            OldPlayer player = yamlData.getSection().getSerializable(id, OldPlayer.class);
            if (player == null) {
                continue;
            }
            SerializablePlayer newPlayer = new SerializablePlayer();
            newPlayer.extensionVersion = player.pluginVersion();
            newPlayer.location = PaperApiHelper.worldPosFromLocation(player.location());
            newPlayer.playerId = player.playerId();
            newPlayer.health = player.health();
            newPlayer.hunger = player.hunger();
            newPlayer.gamemode = PlayerHandlePaper.toPlayerMode(player.mode());
            newPlayer.spawnPoint = PaperApiHelper.worldPosFromLocation(player.spawn());
            newPlayer.xpLevel = player.xpLevel();
            newPlayer.xpPoints = player.xpPoints();
            newPlayer.inventory = Arrays.stream(player.inventory()).map(stack -> new StackHandlePaper(stack)).toArray(StackHandle[]::new);
            newPlayer.enderInventory = Arrays.stream(player.enderInventory()).map(stack -> new StackHandlePaper(stack)).toArray(StackHandle[]::new);
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

        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/player_stats", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/player_stats", false);

        yamlData.load();
        for (String id : yamlData.getKeys()) {
            tagData.setString(id, yamlData.getString(id, "0;0;0;0;0"));
        }
        tagData.saveChanges();

        ConsoleMessenger.log(Component.text("Found outdated player stats configuration file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private void updateLists(String filename) {
        if (!new File(plugin.getDataFolder(), filename + ".yml").exists() || new File(plugin.getDataFolder(), filename + ".nbt").exists()) {
            return;
        }

        ConfigurationSerialization.registerClass(OldItemTask.class);
        ConfigurationSerialization.registerClass(OldStatistic.class);
        ConfigurationSerialization.registerClass(OldStatisticTask.class);
        ConfigurationSerialization.registerClass(OldAdvancementTask.class);

        YamlDataAccessor yamlData = new YamlDataAccessor(server, filename, false);
        TagDataAccessor tagData = new TagDataAccessor(server, filename, false);

        yamlData.load();

        for (String list : yamlData.getKeys()) {
            tagData.setInt(list + ".size", yamlData.getInt(list + ".size", 0));

            List<Object> tasks = (List<Object>) yamlData.getSection().getList(list + ".tasks");
            if (tasks == null) continue;

            List<TaskData> newTasks = new ArrayList<>();
            for (Object task : tasks) {
                if (task instanceof OldItemTask(int count, Material item)) {
                    newTasks.add(new ItemTask(new ItemTypePaper(item), count));
                }
                if (task instanceof OldStatisticTask(int count, OldStatistic statistic)) {
                    newTasks.add(new StatisticTask(
                            StatisticHandlePaper.create(
                                    statistic.stat(),
                                    statistic.entity(),
                                    statistic.material),
							count));
                }
                if (task instanceof OldAdvancementTask(Advancement advancement)) {
                    newTasks.add(new AdvancementTask(new AdvancementHandlePaper(advancement)));
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

        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/cards", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/cards", false);

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

        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/kits", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/kits", false);

        yamlData.load();

        for (String kit : yamlData.getKeys()) {
            OldCustomKit oldKit = yamlData.getSection().getSerializable(kit, OldCustomKit.class);
            if (oldKit == null) continue;

            tagData.setByte(kit + ".kit_id", (byte) oldKit.kitId);
            tagData.setByte(kit + ".card_slot", (byte) oldKit.cardSlot);
            tagData.setString(kit + ".name", oldKit.name);

            tagData.setSerializableList(kit + ".items", SerializableItem.class, oldKit.items.stream()
                    .map(old -> new SerializableItem(old.slot, new StackHandlePaper(old.stack)))
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

        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/teams", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/teams", false);

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
        YamlDataAccessor yamlData = new YamlDataAccessor(server, "data/textures", false);
        TagDataAccessor tagData = new TagDataAccessor(server, "data/textures", false);

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

    private void updateScoreboards() {
        YamlDataAccessor yamlData = new YamlDataAccessor(server, "scoreboards", false);
        yamlData.load();

        String version = yamlData.getString("version", "");

        if (isNewerOrEqual(version, "3.0.1")) {
            return;
        }

        yamlData.setString("version", plugin.getPluginMeta().getVersion());
        updateBoard("lobby", yamlData);
        updateBoard("game", yamlData);
        yamlData.saveChanges();

        ConsoleMessenger.log(Component.text("Found outdated scoreboards file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private @NotNull String updateConfigString(@NotNull String input) {
        input = input.replace("&", "§");
        Component legacyComponent = LegacyComponentSerializer.legacySection().deserialize(input);
        String legacyMini = MiniMessage.miniMessage().serialize(legacyComponent);

        // Variation of BingoMessage convertConfigStringToSingleMini that doesn't replace the argument brackets for scoreboard arguments.
        legacyMini = BingoMessage.replaceColors(legacyMini, color -> "<" + color + ">");
        legacyMini = BingoMessage.convertSmallCaps(legacyMini);
        legacyMini = BingoMessage.replaceSubstitutionTags(legacyMini);

        return legacyMini;
    }

    private void updateBoard(String boardName, YamlDataAccessor data) {
        String title = data.getString(boardName + ".title", "");
        List<String> sideBar = data.getList(boardName + ".sidebar", TagDataType.STRING);

        sideBar.replaceAll(this::updateConfigString);

        data.setString(boardName + ".title", this.updateConfigString(title));
        data.setList(boardName + ".sidebar", TagDataType.STRING, sideBar);
    }

    private void updatePlaceholders() {
        YamlDataAccessor yamlData = new YamlDataAccessor(server, "placeholders", false);
        yamlData.load();

        String version = yamlData.getString("version", "");

        if (isNewerOrEqual(version, "3.0.2")) {
            return;
        }

        yamlData.setString("version", plugin.getPluginMeta().getVersion());

        for (String placeholder : yamlData.getStorage("placeholders").getKeys()) {
            String format = updateConfigString(yamlData.getString("placeholders." + placeholder + ".format", ""));
            yamlData.setString("placeholders." + placeholder + ".format", format);
        }

        yamlData.setString("placeholders.setting_hotswap_expiration.format", "{0}");
        yamlData.setComment("placeholders.setting_hotswap_expiration.format", List.of(
                "The currently selected settings to determine if hotswap tasks should expire automatically, \"true\" or \"false\" depending on the value."));

        yamlData.setString("placeholders.setting_complete_winscore.format", "{0}");
        yamlData.setComment("placeholders.setting_complete_winscore.format", List.of(
                "The currently selected score to win complete-all, '-' if countdown is enabled."));

        yamlData.setString("placeholders.created_session_.format", "{0}");
        yamlData.setComment("placeholders.created_session_.format", List.of(
                "Returns formatted session name with session_name format if the session has been created and an empty string if it has not.",
                "Example usage: %bingoreloaded_created_session_My world% (returns My world if the session \"My world\" has been created)"));

        yamlData.saveChanges();

        ConsoleMessenger.log(Component.text("Found outdated placeholders file and updated it to new format (V2 -> V3)").color(NamedTextColor.GOLD));
    }

    private boolean isNewerOrEqual(@Nullable String version, String pluginVersion) {
        if (pluginVersion.isEmpty() || version == null) {
            return false;
        }
        String[] pluginVersions = pluginVersion.split("\\.");
        String[] versions = version.split("\\.");
        if (versions.length != 3 || pluginVersions.length != 3) {
            return false;
        }

        int pluginMajor = Integer.parseInt(pluginVersions[0]);
        int pluginMinor = Integer.parseInt(pluginVersions[1]);
        int pluginPatch = Integer.parseInt(pluginVersions[2]);

        int currentMajor = Integer.parseInt(versions[0]);
        int currentMinor = Integer.parseInt(versions[1]);
        int currentPatch = Integer.parseInt(versions[2]);

        return currentMajor > pluginMajor || (currentMajor == pluginMajor && currentMinor > pluginMinor) || (currentMajor == pluginMajor && currentMinor == pluginMinor && currentPatch >= pluginPatch);
    }
}
