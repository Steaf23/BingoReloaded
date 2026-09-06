package io.github.steaf23.bingoreloaded.protocol.codec;

import net.kyori.adventure.key.Key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface ByteCodec<T> {

	void encode(DataOutput stream, T object) throws IOException;

	T decode(DataInput stream) throws IOException;

	static <T> ByteCodec<T> create(ByteEncoder<T> encoder, ByteDecoder<T> decoder) {
		return new ByteCodec<>() {
			@Override
			public void encode(DataOutput stream, T object) throws IOException {
				encoder.encode(stream, object);
			}

			@Override
			public T decode(DataInput stream) throws IOException {
				return decoder.decode(stream);
			}
		};
	}

	ByteCodec<String> STRING = ByteCodec.create(DataOutput::writeUTF, DataInput::readUTF);
	ByteCodec<Integer> INT = ByteCodec.create(DataOutput::writeInt, DataInput::readInt);
	ByteCodec<Boolean> BOOL = ByteCodec.create(DataOutput::writeBoolean, DataInput::readBoolean);
	ByteCodec<Key> KEY = STRING.map(Key::key, Key::asString);

	static <T> ByteCodec<T> unit(T value) {
		return ByteCodec.create((_, _) -> {}, (_) -> value);
	}

	default <Out> ByteCodec<Out> map(Function<T, Out> to, Function<Out, T> from) {
		return ByteCodec.create(
				(buf, out) -> encode(buf, from.apply(out)),
				(buf) -> to.apply(decode(buf)));
	}

	default ByteCodec<List<T>> list() {
		return ByteCodec.create(
				(buf, in) -> {
					buf.writeInt(in.size());
					for (T i : in) {
						this.encode(buf, i);
					}
				}, (buf) -> {
					List<T> result = new ArrayList<>();
					int size = buf.readInt();
					for (int i = 0; i < size; i++) {
						result.add(this.decode(buf));
					}
					return result;
				});
	}

	default <V> ByteCodec<Map<T, V>> mapWithValues(ByteCodec<V> valueCodec) {
		return ByteCodec.create(
				(buf, map) -> {
					ByteCodec.INT.encode(buf, map.size());
					for (T key : map.keySet()) {
						this.encode(buf, key);
						valueCodec.encode(buf, map.get(key));
					}

				}, (buf) -> {
					Map<T, V> result = new HashMap<>();
					int count = ByteCodec.INT.decode(buf);
					for (int i = 0; i < count; i++) {
						result.put(this.decode(buf), valueCodec.decode(buf));
					}
					return result;
				});
	}

	default ByteCodec<Set<T>> hashSet() {
		return ByteCodec.create(
				(buf, in) -> {
					buf.writeInt(in.size());
					for (T i : in) {
						this.encode(buf, i);
					}
				}, (buf) -> {
					Set<T> result = new HashSet<>();
					int size = buf.readInt();
					for (int i = 0; i < size; i++) {
						result.add(this.decode(buf));
					}
					return result;
				});
	}

	default ByteCodec<Optional<T>> optional() {
		return ByteCodec.create(
				(buf, in) -> {
					buf.writeBoolean(in.isPresent());

					if (in.isPresent()) {
						encode(buf, in.get());
					}
				}, (buf) -> {
					if (buf.readBoolean()) {
						return Optional.of(decode(buf));
					}
					return Optional.empty();
				});
	}


	@FunctionalInterface
	interface ByteDecoder<T> {

		T decode(DataInput stream) throws IOException;
	}

	@FunctionalInterface
	interface ByteEncoder<T> {

		void encode(DataOutput stream, T object) throws IOException;
	}

}
