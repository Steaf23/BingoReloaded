package io.github.steaf23.easymenulib.util;

public class ExtraMath
{
    public static int clamped(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }
}
