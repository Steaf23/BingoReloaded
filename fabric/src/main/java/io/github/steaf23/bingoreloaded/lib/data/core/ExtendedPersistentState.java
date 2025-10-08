package io.github.steaf23.bingoreloaded.lib.data.core;

import com.mojang.serialization.Codec;
import io.github.steaf23.bingoreloaded.lib.api.FabricServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.UUID;

public class ExtendedPersistentState extends PersistentState {

	private static final Codec<ExtendedPersistentState> CODEC = Uuids.CODEC.fieldOf("uuid").codec().xmap(
			ExtendedPersistentState::new,
			ExtendedPersistentState::uuid
	);

	private final UUID uuid;

	public ExtendedPersistentState() {
		this.uuid = UUID.randomUUID();
	}

	public ExtendedPersistentState(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID uuid() {
		return uuid;
	}

	public static final PersistentStateType<ExtendedPersistentState> TYPE = new PersistentStateType<>(
			((FabricServerSoftware) PlatformResolver.get()).modId(),
			ExtendedPersistentState::new,
			CODEC,
			null);
}
