package io.github.steaf23.bingoreloaded.protocol.payload;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.BingoCard;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorContext;
import io.github.steaf23.bingoreloaded.protocol.codec.Empty;
import io.github.steaf23.bingoreloaded.protocol.data.TaskSlot;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;

import java.util.List;
import java.util.Optional;

public class BingoReloadedPayloads {

	// General
	public static final PayloadDefinition<Empty> CLIENT_HELLO = new PayloadDefinition<>("client_hello", Empty.CODEC);

	// Game loop
	public static final PayloadDefinition<Optional<BingoCard>> UPDATE_CARD = new PayloadDefinition<>("server_update_card", BingoCard.CODEC.optional());
	public static final PayloadDefinition<List<TaskSlot>> UPDATE_HOTSWAP_CARD = new PayloadDefinition<>("server_update_hotswap_card", TaskSlot.CODEC.list());

	// Creator
	public static final PayloadDefinition<String> CLIENT_GET_CREATOR_LIST = new PayloadDefinition<>("client_get_creator_list", ByteCodec.STRING);
	public static final PayloadDefinition<CustomList> CLIENT_UPSERT_CREATOR_LIST = new PayloadDefinition<>("client_upsert_creator_list", CustomList.CODEC);
	public static final PayloadDefinition<CustomCard> CLIENT_UPSERT_CREATOR_CARD = new PayloadDefinition<>("client_upsert_creator_card", CustomCard.CODEC);

	public static final PayloadDefinition<CreatorContext> OPEN_CREATOR = new PayloadDefinition<>("server_open_creator", CreatorContext.CODEC);
	public static final PayloadDefinition<CustomList> CREATOR_LIST = new PayloadDefinition<>("server_creator_list", CustomList.CODEC);
}
