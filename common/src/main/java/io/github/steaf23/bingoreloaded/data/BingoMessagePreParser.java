package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.jetbrains.annotations.Nullable;

public interface BingoMessagePreParser {

	String parse(@Nullable PlayerHandle player, String message);

	class PassthroughMessagePreParser implements BingoMessagePreParser {

		@Override
		public String parse(@Nullable PlayerHandle player, String message) {
			return message;
		}
	}
}
