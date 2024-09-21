package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUpdaterV1
{
    private static final List<String> filesToUpdate = List.of(
            "data/cards.yml",
            "data/kits.yml",
            "data/lists_1_21.yml",
            "data/player_stats.yml",
            "data/players.yml",
            "data/presets.yml",
            "data/teams.yml"
    );

    private final BingoReloaded plugin;

    public DataUpdaterV1(BingoReloaded plugin) {
        this.plugin = plugin;
    }

    public void update() {
        updateCards();
        updateKits();
        updateLists();
        updateStats();
        updatePlayers();
        updatePresets();
        updateKits();
    }

    private void updatePresets() {
    }

    private void updatePlayers() {
    }

    private void updateStats() {
    }

    private void updateLists() {
    }

    private void updateCards() {
        FileConfiguration config = loadYmlData("data/cards.yml");
        if (config == null) {
            return;
        }

        record CardList (int min, int max) {}

        Map<String, Map<String, CardList>> cards = new HashMap<>();
        for (String cardName : config.getKeys(false)) {
            Map<String, CardList> card = new HashMap<>();
            for (String listName : config.getConfigurationSection(cardName).getKeys(false)) {
                CardList list = new CardList(
                        config.getInt(cardName + "." + listName + ".min"),
                        config.getInt(cardName + "." + listName + ".max"));
                card.put(listName, list);
            }
            cards.put(cardName, card);
        }
        ConsoleMessenger.log("CARDS: " + cards);

        DataAccessor cardsData = BingoReloaded.getDataAccessor("data/cards");
        cardsData.clear();
        for (String cardName : cards.keySet()) {
            for (String listName : cards.get(cardName).keySet()) {
                CardList cardList = cards.get(cardName).get(listName);
                cardsData.setInt(cardName + "." + listName + ".min", cardList.min);
                cardsData.setInt(cardName + "." + listName + ".max", cardList.max);
            }
        }
        cardsData.saveChanges();

        ConsoleMessenger.log("Found outdated card configuration file and updated it to new format.");
    }

    private void updateKits() {
    }

    private @Nullable FileConfiguration loadYmlData(String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            return null;
        }
    }
}
