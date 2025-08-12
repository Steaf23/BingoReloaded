package io.github.steaf23.bingoreloaded.lib.api;

public class PotionEffectInstance {

	public static final int INFINITE_DURATION = -1;

	private final PotionEffectType effect;
	private final int durationTicks;
	private int amplifier = 1;
	private boolean ambient = false;
	private boolean particles = true;
	private boolean icon = true;

	public PotionEffectInstance(PotionEffectType effect, int durationTicks) {
		this.effect = effect;
		this.durationTicks = durationTicks;
	}

	public boolean particles() {
		return particles;
	}

	public PotionEffectInstance setParticles(boolean particles) {
		this.particles = particles;
		return this;
	}

	public boolean ambient() {
		return ambient;
	}

	public PotionEffectInstance setAmbient(boolean ambient) {
		this.ambient = ambient;
		return this;
	}

	public int amplifier() {
		return amplifier;
	}

	public PotionEffectInstance setAmplifier(int amplifier) {
		this.amplifier = amplifier;
		return this;
	}

	public int durationTicks() {
		return durationTicks;
	}

	public PotionEffectType effect() {
		return effect;
	}

	public boolean icon() {
		return icon;
	}

	public PotionEffectInstance setIcon(boolean icon) {
		this.icon = icon;
		return this;
	}
}
