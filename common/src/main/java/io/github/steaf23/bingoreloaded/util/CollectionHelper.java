package io.github.steaf23.bingoreloaded.util;

import java.util.Arrays;

public class CollectionHelper
{
    public static <T> T[] concatWithArrayCopy(T[] arrayLeft, T[] arrayRight) {
        T[] result = Arrays.copyOf(arrayLeft, arrayLeft.length + arrayRight.length);
        System.arraycopy(arrayRight, 0, result, arrayLeft.length, arrayRight.length);
        return result;
    }

    public static byte[] concatWithArrayCopy(byte[] arrayLeft, byte[] arrayRight) {
        byte[] result = Arrays.copyOf(arrayLeft, arrayLeft.length + arrayRight.length);
        System.arraycopy(arrayRight, 0, result, arrayLeft.length, arrayRight.length);
        return result;
    }
}
