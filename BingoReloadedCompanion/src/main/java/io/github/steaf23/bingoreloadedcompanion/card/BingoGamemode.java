package io.github.steaf23.bingoreloadedcompanion.card;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public enum BingoGamemode {
	REGULAR(Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "gamemode/bingo"), 0),
	LOCKOUT(Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "gamemode/lockout"), 1),
	COMPLETE(Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "gamemode/complete"), 2),
	HOTSWAP(Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "gamemode/hotswap"), 3),
	;

	private final Identifier id;
	private final int index;

	BingoGamemode(Identifier id, int index) {
		this.id = id;
		this.index = index;
	}

	public static BingoGamemode fromId(Identifier id) {
		return fromId(id, false);
	}

	public static @Nullable BingoGamemode fromId(Identifier id, boolean strict) {
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

	public Identifier getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}
}