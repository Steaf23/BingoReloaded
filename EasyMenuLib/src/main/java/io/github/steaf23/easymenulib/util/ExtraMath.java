package io.github.steaf23.easymenulib.util;

public class ExtraMath
{
    public static int clamped(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    public static float lerp(float from, float to, float by) {
        return from + (to - from) * by;
    }

    static public float map(float value, float iStart, float iStop, float oStart, float oStop) {
        return oStart + (oStop - oStart) * ((value - iStart) / (iStop - iStart));
    }
}
