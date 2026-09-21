package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public record ClientSettings(boolean useClientCreator) {

	public static final ByteCodec<ClientSettings> CODEC = ByteCodec.BOOL.map(ClientSettings::new, ClientSettings::useClientCreator);
}
