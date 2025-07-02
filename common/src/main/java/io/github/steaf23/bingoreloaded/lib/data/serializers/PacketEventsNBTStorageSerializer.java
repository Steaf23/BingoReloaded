package io.github.steaf23.bingoreloaded.lib.data.serializers;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.codec.NBTCodec;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketEventsNBTStorageSerializer implements DataStorageSerializer<NBT> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull NBT value) {
		byte[] buff = {};
		NBTCodec.writeNBTToBuffer(buff, ServerVersion.getLatest(), value);
		TagDataAccessor.readTagDataFromRawBytes(buff);
	}

	@Override
	public @Nullable NBT fromDataStorage(@NotNull DataStorage storage) {
		return null;
	}
}
