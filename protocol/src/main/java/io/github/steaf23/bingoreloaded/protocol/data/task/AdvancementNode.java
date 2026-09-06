package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AdvancementNode(Optional<Key> parent, boolean hasDisplay, @Nullable Key displayIcon, @Nullable String displayName, @Nullable String displayDescription) {

	public static final ByteCodec<AdvancementNode> CODEC = ByteCodec.create(
			(buf, node) -> {
				ByteCodec.KEY.optional().encode(buf, node.parent);
				ByteCodec.BOOL.encode(buf, node.hasDisplay);
				if (node.hasDisplay) {
					ByteCodec.KEY.encode(buf, node.displayIcon);
					ByteCodec.STRING.encode(buf, node.displayName);
					ByteCodec.STRING.encode(buf, node.displayDescription);
				}
			}, (buf) -> {
				Optional<Key> parent = ByteCodec.KEY.optional().decode(buf);
				boolean hasDisplay = ByteCodec.BOOL.decode(buf);

				Key icon = null;
				String name = null, description = null;
				if (hasDisplay) {
					icon = ByteCodec.KEY.decode(buf);
					name = ByteCodec.STRING.decode(buf);
					description = ByteCodec.STRING.decode(buf);
				}

				return new AdvancementNode(parent, hasDisplay, icon, name, description);
			});
}
