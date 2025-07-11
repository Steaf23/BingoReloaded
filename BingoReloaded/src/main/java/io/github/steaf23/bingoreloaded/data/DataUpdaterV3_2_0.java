package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataAccessor;

public class DataUpdaterV3_2_0 extends DataUpdaterV1 {

	public DataUpdaterV3_2_0(BingoReloaded plugin) {
		super(plugin);
	}

	@Override
	protected void updateLists(String filename) {
		super.updateLists(filename);

		TagDataAccessor tagData = new TagDataAccessor(plugin, filename, false);
		tagData.load();
		tagData.erase("default_items");
		tagData.erase("default_advancements");
		tagData.erase("default_statistics");
		tagData.erase("default_items_hardcore");
		tagData.erase("default_advancements_hardcore");
		tagData.erase("default_statistics_hardcore");
		tagData.saveChanges();
	}
}
