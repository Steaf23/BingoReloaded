package io.github.steaf23.bingoreloaded.protocol.data.card;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

import java.util.List;

public record CustomCard(String name, List<ListReference> lists, String description, boolean readOnly) {

	public static final ByteCodec<CustomCard> CODEC = ByteCodec.create(
			(buf, card) -> {
				ByteCodec.STRING.encode(buf, card.name);
				ListReference.CODEC.list().encode(buf, card.lists);
				ByteCodec.STRING.encode(buf, card.description);
				ByteCodec.BOOL.encode(buf, card.readOnly);
			}, (buf) -> new CustomCard(
					ByteCodec.STRING.decode(buf),
					ListReference.CODEC.list().decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.BOOL.decode(buf)
			));
}
