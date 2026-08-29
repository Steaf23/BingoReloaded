package io.github.steaf23.bingoreloaded.api.network;

public enum BingoReloadedPackets {

	CLIENT_HELLO("bingoreloaded:hello"),
	CLIENT_REQUEST_ALL_TASKS("bingoreloaded:request_all_tasks"),

	SERVER_UPDATE_CARD("bingoreloaded:update_card"),
	SERVER_HOTSWAP_TASKS("bingoreloaded:hotswap_tasks"),
	SERVER_ALL_TASKS("bingoreloaded:all_tasks"),
	SERVER_OPEN_CREATOR("bingoreloaded:open_creator")
	;

	private final String id;

	BingoReloadedPackets(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}
}
