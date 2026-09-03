package io.github.steaf23.bingoreloaded.protocol.data;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public enum BingoGamemode implements Keyed {
	REGULAR(Key.key("bingoreloaded", "gamemode/bingo"), 0),
	LOCKOUT(Key.key("bingoreloaded", "gamemode/lockout"), 1),
	COMPLETE(Key.key("bingoreloaded", "gamemode/complete"), 2),
	HOTSWAP(Key.key("bingoreloaded", "gamemode/hotswap"), 3),
	BLITZ(Key.key("bingoreloaded", "gamemode/blitz"), 4),
	;

	private final Key id;
	private final int index;

	BingoGamemode(Key id, int index) {
		this.id = id;
		this.index = index;
	}

	public static BingoGamemode fromIdentifier(Key id, boolean strict) {
		for (BingoGamemode mode : BingoGamemode.values()) {
			if (mode.id.equals(id)) {
				return mode;
			}
		}

		if (strict) {
			return null;
		}
		return BingoGamemode.REGULAR;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public Key key() {
		return id;
	}
}