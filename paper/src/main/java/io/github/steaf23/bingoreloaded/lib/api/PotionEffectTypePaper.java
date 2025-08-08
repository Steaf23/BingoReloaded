package io.github.steaf23.bingoreloaded.lib.api;


public class PotionEffectTypePaper implements PotionEffectType {

	private final org.bukkit.potion.PotionEffectType type;

	public PotionEffectTypePaper(org.bukkit.potion.PotionEffectType type) {
		this.type = type;
	}

	public org.bukkit.potion.PotionEffectType handle() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PotionEffectTypePaper other)) {
			return false;
		}
		return type.equals(other.type);
	}
}
