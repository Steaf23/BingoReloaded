package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultKitData {
	private final DataAccessor data = BingoReloaded.getDataAccessor("data/default_kits");

	public record Kit(List<SerializableItem> items) {
	}

	public @Nullable Kit getKit(PlayerKit slot)
	{
		return data.getSerializable(slot.configName, Kit.class);
	}
}
