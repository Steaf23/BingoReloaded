package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloadedPaper;
import io.github.steaf23.bingoreloaded.data.serializers.ItemStorageSerializer;
import io.github.steaf23.bingoreloaded.item.BingoItems;
import io.github.steaf23.bingoreloaded.item.GoUpWand;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializerRegistry;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataAccessor;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataUpdaterV3_5_0 extends DataUpdaterV3_3_0 {

	public DataUpdaterV3_5_0(BingoReloadedPaper plugin) {
		super(plugin);
	}

	@Override
	protected void updateKits() {
		super.updateKits();

		BingoItems items = new BingoItems();

		DataStorageSerializerRegistry.addSerializer(new ItemStorageSerializer(), SerializableItem.class);

		TagDataAccessor tagData = new TagDataAccessor(server, "data/kits", false);
		tagData.load();

		for (String kitName : tagData.getKeys()) {
			List<SerializableItem> immutableList = tagData.getSerializableList(kitName + ".items", SerializableItem.class);

			List<SerializableItem> itemsCopy = new ArrayList<>(immutableList);
			boolean updated = false;
			int index = 0;
			for (SerializableItem item : immutableList) {
				if (item.stack().compareKey().equals("wand")) {
					itemsCopy.remove(index);
					itemsCopy.add(index, new SerializableItem(item.slot(), items.createStack(GoUpWand.ID, null)));
					updated = true;
				}

				index++;
			}
			if (updated) {
				tagData.setSerializableList(kitName + ".items", SerializableItem.class, itemsCopy);
			}
		}

		tagData.saveChanges();
	}

	@Override
	protected void updateCards() {
		super.updateCards();

		TagDataAccessor tagData = new TagDataAccessor(server, "data/cards", false);
		tagData.load();

		tagData.erase("default_card");
		tagData.erase("default_card_hardcore");

		for (String card : new HashSet<>(tagData.getKeys())) {
			if (tagData.contains(card + ".lists") && tagData.contains(card + ".description")) {
				continue;
			}

			DataStorage lists = tagData.getStorage(card);
			tagData.erase(card);
			tagData.setStorage(card + ".lists", lists);
			tagData.setString(card + ".description", "");
		}

		tagData.saveChanges();
	}

	@Override
	protected void updateConfig() {
		super.updateConfig();

		File configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			return;
		}

		YamlConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);
		String version = existingConfig.getString("version");

		if (isNewerOrEqual(version, "3.5.0")) {
			return;
		}

		FileConfiguration config = plugin.getConfig();
		if (existingConfig.get("defaultWorldName", "world").equals("world")) {
			config.set("defaultWorldName", "minecraft:overworld");
		}

		try {
			config.save(configFile);
		} catch (IOException e) {
			ConsoleMessenger.bug("Could not update config.yml to new version", this);
		}
		ConsoleMessenger.log(Component.text("Found outdated config.yml file and updated it to new format (V? -> V3.5.0").color(NamedTextColor.GOLD));

	}
}
