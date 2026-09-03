package io.github.steaf23.bingoreloaded.protocol.payload;

import io.github.steaf23.bingoreloaded.protocol.data.BingoCard;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.codec.Empty;
import io.github.steaf23.bingoreloaded.protocol.data.TaskSlot;

import java.util.List;
import java.util.Optional;

public class BingoReloadedPayloads {

	public static final PayloadDefinition<Empty> CLIENT_HELLO = new PayloadDefinition<>("hello", Empty.CODEC);

	public static final PayloadDefinition<Optional<BingoCard>> UPDATE_CARD = new PayloadDefinition<>("update_card", BingoCard.CODEC.optional());
	public static final PayloadDefinition<List<TaskSlot>> UPDATE_HOTSWAP_CARD = new PayloadDefinition<>("update_hotswap_card", TaskSlot.CODEC.list());

	public static final PayloadDefinition<CreatorTaskSupplier> OPEN_CREATOR = new PayloadDefinition<>("open_creator", CreatorTaskSupplier.CODEC);
}
