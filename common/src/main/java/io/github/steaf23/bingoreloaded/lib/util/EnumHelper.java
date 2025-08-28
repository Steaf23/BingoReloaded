package io.github.steaf23.bingoreloaded.lib.util;

import org.jetbrains.annotations.Nullable;

public class EnumHelper {
	public static <T extends Enum<T>> @Nullable T valueOfOrNull(Class<T> type, String value) {
		try {
			return Enum.valueOf(type, value);
		} catch (IllegalArgumentException invalidArg) {
			return null;
		}
	}
}
