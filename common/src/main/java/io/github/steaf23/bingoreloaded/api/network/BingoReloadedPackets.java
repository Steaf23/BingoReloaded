package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.api.network.packets.DataWriter;
import io.github.steaf23.bingoreloaded.api.network.packets.HotswapTasksWriter;
import io.github.steaf23.bingoreloaded.api.network.packets.TaskCardWriter;

public enum BingoReloadedPackets {

	CLIENT_HELLO( "bingoreloaded:hello"),

	SERVER_UPDATE_CARD("bingoreloaded:update_card"),
	SERVER_HOTSWAP_TASKS("bingoreloaded:hotswap_tasks")
	;

	private final String id;

	BingoReloadedPackets(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}
}
