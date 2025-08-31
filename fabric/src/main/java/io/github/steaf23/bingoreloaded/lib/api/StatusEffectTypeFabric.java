package io.github.steaf23.bingoreloaded.lib.api;

import net.minecraft.entity.effect.StatusEffect;

public class StatusEffectTypeFabric implements StatusEffectType {

	private final StatusEffect effect;

	public StatusEffectTypeFabric(StatusEffect effect) {
		this.effect = effect;
	}

	public StatusEffect handle() {
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
