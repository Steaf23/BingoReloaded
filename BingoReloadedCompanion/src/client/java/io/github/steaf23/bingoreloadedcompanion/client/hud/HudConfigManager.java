package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
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

	public void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				HudConfig config = GSON.fromJson(reader, HudConfig.class);
				elementPlaces = new HashMap<>(config.elements());
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
			GSON.toJson(new HudConfig(BingoReloadedCompanion.modVersion(), elementPlaces), writer);
			savedConfig = new HudConfig(savedConfig.version(), new HashMap<>(elementPlaces));
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
		Window window = MinecraftClient.getInstance().getWindow();

		HudPlacement place = getHudPlacement(id);
		Rect usedRect = getUsedRectOfElement(id);
		//TODO: fix crash when borderY/X is smaller than usedRect
		elementPlaces.put(id, place.move(
				Math.clamp((double)toX / window.getScaledWidth(), 0, ((double)borderX - usedRect.width()) / window.getScaledWidth()),
				Math.clamp((double)toY / window.getScaledHeight(), 0, ((double)borderY - usedRect.height()) / window.getScaledHeight())
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

	public void resetElement(Identifier id) {
		elementPlaces.remove(id);
	}

	public void resetAllElements() {
		elementPlaces.clear();
	}

	public @NotNull Rect getUsedRectOfElement(Identifier id) {
		HudInfo info = ConfigurableHudRegistry.getInfo(id);
		HudPlacement placement = getHudPlacement(id);

		Window window = MinecraftClient.getInstance().getWindow();

		if (info == null) {
			return new Rect(0, 0, 0, 0);
		}

		return new Rect((int)(placement.x() * window.getScaledWidth()), (int)(placement.y() * window.getScaledHeight()),
				(int)(info.minSizeX() * (1.0 / window.getScaleFactor() * placement.scaleX())),
				(int)(info.minSizeY() * (1.0 / window.getScaleFactor() * placement.scaleY())));
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
