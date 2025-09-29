package io.github.steaf23.bingoreloadedcompanion.client.hud;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
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
			jsonWriter.beginObject();

			HudPlacement placement = hudConfig.elements().get(id);
			jsonWriter.name("x");
			jsonWriter.value(placement.x());
			jsonWriter.name("y");
			jsonWriter.value(placement.y());
			jsonWriter.name("visible");
			jsonWriter.value(placement.visible());
			jsonWriter.name("sizeX");
			jsonWriter.value(placement.sizeX());
			jsonWriter.name("sizeY");
			jsonWriter.value(placement.sizeY());

			jsonWriter.endObject();
		}
		jsonWriter.endObject();
		jsonWriter.endObject();
	}

	@Override
	public HudConfig read(JsonReader jsonReader) throws IOException {
		Map<Identifier, HudPlacement> map = new HashMap<>();

		jsonReader.beginObject();
		jsonReader.nextName();
		jsonReader.beginObject();

		while (jsonReader.hasNext()) {
			String id = jsonReader.nextName();
			jsonReader.beginObject();
			jsonReader.nextName();
			int x = jsonReader.nextInt();
			jsonReader.nextName();
			int y = jsonReader.nextInt();
			jsonReader.nextName();
			boolean visible = jsonReader.nextBoolean();
			jsonReader.nextName();
			int sizeX = jsonReader.nextInt();
			jsonReader.nextName();
			int sizeY = jsonReader.nextInt();
			jsonReader.endObject();

			map.put(Identifier.of(id), new HudPlacement(x, y, visible, sizeX, sizeY));
		}

		jsonReader.endObject();
		jsonReader.endObject();

		return new HudConfig(map);
	}
}
