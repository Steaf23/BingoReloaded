package io.github.steaf23.bingoreloaded.lib.api;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class StatusEffectTypeFabric implements StatusEffectType {

	private final Holder<MobEffect> effect;

	public StatusEffectTypeFabric(Holder<MobEffect> effect) {
		this.effect = effect;
	}

	public Holder<MobEffect> handle() {
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
