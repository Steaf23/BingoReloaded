package io.github.steaf23.bingoreloaded.data;

public enum BingoStatType {
	PLAYED(-1),
	WINS(0),
	LOSSES(1),
	TASKS(2),
	RECORD_TASKS(3),
	ITEM_USES(-1),
	WAND_USES(4),
	PEARL_USES(5),
	POUCH_USES(6),
	TELEPORTER_USES(7),
	;

	public final int idx;

	BingoStatType(int idx) {
		this.idx = idx;
	}
}
