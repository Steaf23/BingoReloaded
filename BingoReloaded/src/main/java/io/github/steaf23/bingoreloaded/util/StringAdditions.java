package io.github.steaf23.bingoreloaded.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringAdditions
{
    public static @NotNull String capitalize(@NotNull String input) {
        String[] words = input.split(" ");
        return Arrays.stream(words)
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1))
                .collect(Collectors.joining(" "));
    }
}
