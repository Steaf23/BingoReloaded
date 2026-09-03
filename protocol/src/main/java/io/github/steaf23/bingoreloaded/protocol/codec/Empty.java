package io.github.steaf23.bingoreloaded.protocol.codec;

public record Empty() {

	private static final Empty INSTANCE = new Empty();

	public static final ByteCodec<Empty> CODEC = ByteCodec.unit(INSTANCE);

}
