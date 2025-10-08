package io.github.steaf23.bingoreloaded.lib.util;

import java.awt.*;

public class ExtraMath
{
    public static float lerp(float from, float to, float by) {
        return from + (to - from) * by;
    }

    static public float map(float value, float iStart, float iStop, float oStart, float oStop) {
        return oStart + (oStop - oStart) * ((value - iStart) / (iStop - iStart));
    }

    public static Color modulateColor(Color color, Color with) {
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;
        red *= with.getRed() / 255.0;
        green *= with.getGreen() / 255.0;
        blue *= with.getBlue() / 255.0;

        return new Color((int)(red * 255), (int)(green * 255), (int)(blue * 255));
    }
}
