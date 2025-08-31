package io.github.steaf23.bingoreloaded.lib.api;


public class StatusEffectTypePaper implements StatusEffectType {

	private final org.bukkit.potion.PotionEffectType type;

	public StatusEffectTypePaper(org.bukkit.potion.PotionEffectType type) {
		this.type = type;
	}

	public org.bukkit.potion.PotionEffectType handle() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StatusEffectTypePaper other)) {
			return false;
		}
		return type.equals(other.type);
	}
}
