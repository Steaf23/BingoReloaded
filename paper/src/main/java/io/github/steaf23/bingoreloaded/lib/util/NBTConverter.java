package io.github.steaf23.bingoreloaded.lib.util;

import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
import com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTLong;
import com.github.retrooper.packetevents.protocol.nbt.NBTLongArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTShort;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.Tag;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagList;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagTree;

public class NBTConverter {

	public static Tag.CompoundTag tagFromPacketEventsNBT(NBTCompound input) {
		return (Tag.CompoundTag) decodeTag(input); // decodeTag makes sure that the input is indeed a compound tag, so casting is fine.
	}

	private static Tag<?> decodeTag(NBT in) {
		switch (in)
		{
			case NBTByte nbtByte: return new Tag.ByteTag(nbtByte.getAsByte());
			case NBTShort nbtShort: return new Tag.ShortTag(nbtShort.getAsShort());
			case NBTInt nbtInt: return new Tag.IntegerTag(nbtInt.getAsInt());
			case NBTLong nbtLong: return new Tag.LongTag(nbtLong.getAsLong());
			case NBTFloat nbtFloat: return new Tag.FloatTag(nbtFloat.getAsFloat());
			case NBTDouble nbtDouble: return new Tag.DoubleTag(nbtDouble.getAsDouble());
			case NBTByteArray nbtByteArray: return new Tag.ByteArrayTag(nbtByteArray.getValue());
			case NBTString nbtString: return new Tag.StringTag(nbtString.getValue());
			case NBTIntArray nbtIntArray: return new Tag.IntegerArrayTag(nbtIntArray.getValue());
			case NBTLongArray nbtLongArray: return new Tag.LongArrayTag(nbtLongArray.getValue());
			case NBTList<?> nbtList: {
				TagList list = new TagList();
				for (NBT nbt : nbtList.getTags())
				{
					list.addTag(decodeTag(nbt));
				}
				return new Tag.ListTag(list);
			}
			case NBTCompound nbtCompound: {
				var map = nbtCompound.getTags();
				TagTree tree = new TagTree();
				for (String name : map.keySet()) {
					tree.putChild(name, decodeTag(map.get(name)));
				}

				return new Tag.CompoundTag(tree);
			}
			default:
				throw new IllegalStateException("Unexpected value: " + in);
		}
	}

}
