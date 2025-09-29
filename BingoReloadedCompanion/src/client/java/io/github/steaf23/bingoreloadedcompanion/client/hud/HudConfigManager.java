package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
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

	private Map<Identifier, HudPlacement> elementPlaces = new HashMap<>();

	public void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				elementPlaces = GSON.fromJson(reader, HudConfig.class).elements();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			save();
		}
	}

	public void save() {
		try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(new HudConfig(elementPlaces), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void moveElement(Identifier id, int toX, int toY) {
		HudPlacement place = getHudPlacement(id);
		elementPlaces.put(id, place.move(toX, toY));
	}

	public void moveElement(Identifier id, int toX, int toY, int borderX, int borderY) {
		HudPlacement place = getHudPlacement(id);
		Rect usedRect = getUsedRectOfElement(id);
		elementPlaces.put(id, place.move(
				Math.clamp(toX, 0, borderX - usedRect.width()),
				Math.clamp(toY, 0, borderY - usedRect.height())
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

	public void resetElement(Identifier id) {
		elementPlaces.remove(id);
	}

	public @NotNull Rect getUsedRectOfElement(Identifier id) {
		HudInfo info = ConfigurableHudRegistry.getInfo(id);
		HudPlacement placement = getHudPlacement(id);

		if (info == null) {
			return new Rect(0, 0, 0, 0);
		}

		return new Rect(placement.x(), placement.y(),
				Math.max(info.minSizeX(), placement.sizeX()),
				Math.max(info.minSizeY(), placement.sizeY()));
	}

	public @NotNull HudPlacement getHudPlacement(Identifier id) {
		if (elementPlaces.containsKey(id)) {
			return elementPlaces.get(id);
		} else {
			HudPlacement def = ConfigurableHudRegistry.getDefaultPlacement(id);
			return def == null ? new HudPlacement(0, 0, true, 0, 0) : def;
		}
	}
}
