package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.Window;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HudConfigManager {

	public record Rect(int x, int y, int width, int height) {

		public int endX() {
			return x + width;
		}

		public int endY() {
			return y + height;
		}
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
			.registerTypeAdapter(HudConfig.class, new HudConfigSerializer())
			.create();

	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("bingoreloadedcompanion.json");

	private HudConfig savedConfig;

	private Map<Identifier, HudPlacement> elementPlaces = new HashMap<>();
	private Map<Identifier, ConfigOption> options = new HashMap<>();

	public void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				HudConfig config = GSON.fromJson(reader, HudConfig.class);
				elementPlaces = new HashMap<>(config.elements());
				options = new HashMap<>(config.options());
				savedConfig = config;
				if (BingoReloadedCompanion.isCurrentVersionNewer(config.version())) {
					updateConfig();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			save();
		}
	}

	public void save() {
		try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(new HudConfig(BingoReloadedCompanion.modVersion(), elementPlaces, options), writer);
			savedConfig = new HudConfig(BingoReloadedCompanion.modVersion(), new HashMap<>(elementPlaces), new HashMap<>(options));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean hasChanged() {
		if (savedConfig.elements().size() != elementPlaces.size()) {
			return true;
		}

		for (Identifier id : savedConfig.elements().keySet()) {
			if (!elementPlaces.containsKey(id)) {
				return true;
			}

			HudPlacement currentPlacement = elementPlaces.get(id);
			HudPlacement savedPlacement = savedConfig.elements().get(id);

			if (!currentPlacement.equals(savedPlacement)) {
				return true;
			}
		}

		for (Identifier id : savedConfig.options().keySet()) {
			if (!options.containsKey(id)) {
				return true;
			}

			ConfigOption current = options.get(id);
			ConfigOption saved = savedConfig.options().get(id);

			if (!current.equals(saved)) {
				return true;
			}
		}

		return false;
	}

	public void updateConfig() {
		// TODO: implement
	}

	public void moveElement(Identifier id, double toX, double toY) {
		HudPlacement place = getHudPlacement(id);
		elementPlaces.put(id, place.move(toX, toY));
	}

	/**
	 * Moves element in absolute window positions
	 */
	public void moveElement(Identifier id, int toX, int toY, int borderX, int borderY) {
		Window window = Minecraft.getInstance().getWindow();

		HudPlacement place = getHudPlacement(id);
		Rect usedRect = getUsedRectOfElement(id);
		//TODO: fix crash when borderY/X is smaller than usedRect
		elementPlaces.put(id, place.move(
				Math.clamp((double)toX / window.getGuiScaledWidth(), 0, ((double)borderX - usedRect.width()) / window.getGuiScaledWidth()),
				Math.clamp((double)toY / window.getGuiScaledHeight(), 0, ((double)borderY - usedRect.height()) / window.getGuiScaledHeight())
		));
	}

	public void setElementVisible(Identifier id, boolean visible) {
		HudPlacement placement = getHudPlacement(id);
		elementPlaces.put(id, placement.setVisible(visible));
	}

	public void toggleElementVisible(Identifier id) {
		HudPlacement placement = getHudPlacement(id);
		elementPlaces.put(id, placement.setVisible(!placement.visible()));
	}

	public void setElementScale(Identifier id, float scaleX, float scaleY) {
		HudPlacement placement = getHudPlacement(id);
		elementPlaces.put(id, placement.setScale(scaleX, scaleY));
	}

	public void setElementTransparency(Identifier id, double transparency) {
		HudPlacement placement = getHudPlacement(id);
		elementPlaces.put(id, placement.setTransparency(transparency));
	}

	public boolean getBooleanOption(Identifier id) {
		return options.getOrDefault(id, ConfigurableHudRegistry.getDefaultOption(id)).boolOption();
	}

	public String getStringOption(Identifier id) {
		return options.getOrDefault(id, ConfigurableHudRegistry.getDefaultOption(id)).stringOption();
	}

	public int getIntOption(Identifier id) {
		return options.getOrDefault(id, ConfigurableHudRegistry.getDefaultOption(id)).intOption();
	}

	public void setBooleanOption(Identifier id, boolean value) {
		options.put(id, new ConfigOption(value));
	}

	public void setStringOption(Identifier id, String value) {
		options.put(id, new ConfigOption(value));
	}

	public void setIntOption(Identifier id, int value) {
		options.put(id, new ConfigOption(value));
	}

	public void resetElement(Identifier id) {
		elementPlaces.remove(id);
	}

	public void resetAllElements() {
		elementPlaces.clear();
	}

	public @NotNull Rect getUsedRectOfElement(Identifier id) {
		HudInfo info = ConfigurableHudRegistry.getInfo(id);
		HudPlacement placement = getHudPlacement(id);

		Window window = Minecraft.getInstance().getWindow();

		if (info == null) {
			return new Rect(0, 0, 0, 0);
		}

		return new Rect((int)(placement.x() * window.getGuiScaledWidth()), (int)(placement.y() * window.getGuiScaledHeight()),
				(int)(info.minSizeX() * (1.0 / window.getGuiScale() * placement.scaleX())),
				(int)(info.minSizeY() * (1.0 / window.getGuiScale() * placement.scaleY())));
	}

	public @NotNull HudPlacement getHudPlacement(Identifier id) {
		if (elementPlaces.containsKey(id)) {
			return elementPlaces.get(id);
		} else {
			HudPlacement def = ConfigurableHudRegistry.getDefaultPlacement(id);
			return def == null ? new HudPlacement(0, 0, true, 3.0f, 3.0f, 1.0) : def;
		}
	}
}
