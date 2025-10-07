package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HudConfigSerializer extends TypeAdapter<HudConfig> {

	@Override
	public void write(JsonWriter jsonWriter, HudConfig hudConfig) throws IOException {
		jsonWriter.beginObject();
		jsonWriter.name("elements");
		jsonWriter.beginObject();
		for (Identifier id : hudConfig.elements().keySet()) {
			jsonWriter.name(id.toString());
			writePlacement(jsonWriter, hudConfig.elements().get(id));
		}
		jsonWriter.endObject();
		writeVersion(jsonWriter, hudConfig.version());
		jsonWriter.endObject();
	}

	@Override
	public HudConfig read(JsonReader jsonReader) throws IOException {
		Map<Identifier, HudPlacement> map = new HashMap<>();
		String version = "0.0";
		jsonReader.beginObject();

		while (jsonReader.hasNext()) {
			if (!jsonReader.peek().equals(JsonToken.NAME)) {
				break;
			}

			String name = jsonReader.nextName();
			switch (name) {
				case "elements" -> {
					jsonReader.beginObject();

					while (jsonReader.hasNext()) {
						String id = jsonReader.nextName();

						HudPlacement placement = readPlacement(jsonReader);

						map.put(Identifier.of(id), placement);
					}

					jsonReader.endObject();
				}
				case "version" -> {
					version = jsonReader.nextString();
				}
			}
		}

		jsonReader.endObject();

		return new HudConfig(version, map);
	}

	protected void writePlacement(JsonWriter json, HudPlacement placement) throws IOException {
		json.beginObject();
		json.name("x");
		json.value(placement.x());
		json.name("y");
		json.value(placement.y());
		json.name("visible");
		json.value(placement.visible());
		json.name("scaleX");
		json.value(placement.scaleX());
		json.name("scaleY");
		json.value(placement.scaleY());
		json.name("transparency");
		json.value(placement.transparency());
		json.endObject();
	}

	protected HudPlacement readPlacement(JsonReader json) throws IOException {
		json.beginObject();

		double x = 0.0;
		double y = 0.0;
		boolean visible = true;
		double scaleX = 1.0;
		double scaleY = 1.0;
		double transparency = 1.0;

		while (json.hasNext()) {
			if (!json.peek().equals(JsonToken.NAME)) {
				break;
			}

			String name = json.nextName();

			boolean end = false;
			switch (name) {
				case "x" -> {
					x = Math.clamp(json.nextDouble(), 0.0, 1.0);
				}
				case "y" -> {
					y = Math.clamp(json.nextDouble(), 0.0, 1.0);
				}
				case "visible" -> {
					visible = json.nextBoolean();
				}
				case "scaleX" -> {
					scaleX = Math.clamp(json.nextDouble(), 1.0, 4.0);
				}
				case "scaleY" -> {
					scaleY = Math.clamp(json.nextDouble(), 1.0, 4.0);
				}
				case "transparency" -> {
					transparency = Math.clamp(json.nextDouble(), 0.0, 1.0);
				}
				default -> {
					end = true;
				}
			}
			if (end) break;
		}
		json.endObject();

		return new HudPlacement(x, y, visible, (float)scaleX, (float)scaleY, transparency);
	}

	protected void writeVersion(JsonWriter json, String version) throws IOException {
		json.name("version");
		json.value(version);
	}
}
