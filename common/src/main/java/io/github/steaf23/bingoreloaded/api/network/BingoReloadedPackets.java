package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.api.network.packets.DataWriter;
import io.github.steaf23.bingoreloaded.api.network.packets.TaskCardWriter;

public enum BingoReloadedPackets {

	CLIENT_HELLO( "bingoreloaded:hello", new DataWriter.Empty()),

	SERVER_UPDATE_CARD("bingoreloaded:update_card", new TaskCardWriter()),
	;

	private final String id;
	private final DataWriter<?> writer;

	BingoReloadedPackets(String id, DataWriter<?> writer) {
		this.id = id;
		this.writer = writer;
	}

	public DataWriter<?> writer() {
		return writer;
	}

	public String id() {
		return id;
	}
}
