package io.github.steaf23.bingoreloaded.lib.api;

import net.minecraft.world.effect.MobEffect;

public class StatusEffectTypeFabric implements StatusEffectType {

	private final MobEffect effect;

	public StatusEffectTypeFabric(MobEffect effect) {
		this.effect = effect;
	}

	public MobEffect handle() {
		return effect;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StatusEffectTypeFabric other)) {
			return false;
		}
		return effect.equals(other.effect);
	}

}
