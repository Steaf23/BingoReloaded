package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloadedPaper;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataAccessor;

public class DataUpdaterV3_3_0 extends DataUpdaterV1 {

	public DataUpdaterV3_3_0(BingoReloadedPaper plugin) {
		super(plugin);
	}

	@Override
	protected void updateLists(String filename) {
		super.updateLists(filename);

		TagDataAccessor tagData = new TagDataAccessor(server, filename, false);
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
