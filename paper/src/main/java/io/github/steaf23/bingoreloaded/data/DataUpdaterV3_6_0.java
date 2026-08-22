package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloadedPaper;
import io.github.steaf23.bingoreloaded.lib.api.BukkitStatistics;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Statistic;

import java.util.List;

public class DataUpdaterV3_6_0 extends DataUpdaterV3_5_0 {

	public DataUpdaterV3_6_0(BingoReloadedPaper plugin) {
		super(plugin);
	}

	@Override
	protected void updateLists(String filename) {
		super.updateLists(filename);

		TagDataAccessor tagData = new TagDataAccessor(resources, filename, false);
		tagData.load();
		int patch = tagData.getInt(TaskListData.PATCH_LEVEL_KEY, 0);
		if (patch >= 1) {
			return;
		}

		tagData.setInt(TaskListData.PATCH_LEVEL_KEY, 1);

		for (String list : tagData.getKeys().stream()
				.filter(key -> !key.equals(TaskListData.PATCH_LEVEL_KEY))
				.toList()) {
			List<DataStorage> tasks = tagData.getList(list + ".tasks");
			for (DataStorage storage : tasks) {
				if (storage.contains("statistic")) {
					Key key = storage.getKey("statistic.stat_type");
					if (key == null) {
						continue;
					}
					storage.setKey("statistic.item_type", BukkitStatistics.getVanillaStatistic(Statistic.valueOf(key.value())).key());
				}
			}
			tagData.setList(list, tasks);
		}
		tagData.saveChanges();

		ConsoleMessenger.log(Component.text("Found outdated list configuration file and updated it to new format (0 -> 1)").color(NamedTextColor.GOLD));
	}

}
