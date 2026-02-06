package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.key.Keyed;

public interface ItemType extends Keyed {

	boolean isBlock();
	boolean isAir();
	boolean isSolid();

	boolean equals(Object other);
}
