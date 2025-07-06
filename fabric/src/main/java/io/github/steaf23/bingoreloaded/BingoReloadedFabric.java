package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import net.fabricmc.api.ModInitializer;

import java.util.Collection;
import java.util.List;

public class BingoReloadedFabric implements ModInitializer, BingoReloadedRuntime {

	private final BingoReloaded bingo;

	public BingoReloadedFabric() {
		PlatformResolver.set(new FabricPlatformBridge());

		this.bingo = new BingoReloaded(this);
	}

	@Override
	public void onInitialize() {
		bingo.load();
		bingo.enable();
	}

	@Override
	public DataAccessor getConfigData() {
		return null;
	}

	@Override
	public Collection<DataAccessor> getDataToRegister() {
		return List.of();
	}
}
