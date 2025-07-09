package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.event.entity.EntityDamageEvent;

public class DamageTypePaper implements DamageType {

	private final EntityDamageEvent.DamageCause cause;

	public DamageTypePaper(EntityDamageEvent.DamageCause cause) {
		this.cause = cause;
	}

	@Override
	public boolean isFallDamage() {
		return cause == EntityDamageEvent.DamageCause.FALL;
	}
}
