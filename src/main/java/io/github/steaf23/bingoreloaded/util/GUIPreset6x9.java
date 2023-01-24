package io.github.steaf23.bingoreloaded.util;

public enum GUIPreset6x9
{
    THREE_CENTER(new int[]{20, 22, 24}),
    TWELVE(new int[]{1, 3, 5, 7, 19, 21, 23, 25, 37, 39, 41, 43})
    ;
    public final int[] positions;

    GUIPreset6x9(int[] positions)
    {
        this.positions = positions;
    }
}
