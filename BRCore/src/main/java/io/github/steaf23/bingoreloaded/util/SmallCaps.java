package io.github.steaf23.bingoreloaded.util;

import java.util.HashMap;
import java.util.Map;

public class SmallCaps
{
    private static final Map<Character, Character> charTable = new HashMap<>(){{
        put('a', 'ᴀ');
        put('b', 'ʙ');
        put('c', 'ᴄ');
        put('d', 'ᴅ');
        put('e', 'ᴇ');
        put('f', 'ғ');
        put('g', 'ɢ');
        put('h', 'ʜ');
        put('i', 'ɪ');
        put('j', 'ᴊ');
        put('k', 'ᴋ');
        put('l', 'ʟ');
        put('m', 'ᴍ');
        put('n', 'ɴ');
        put('o', 'ᴏ');
        put('p', 'ᴘ');
        put('q', 'ǫ');
        put('r', 'ʀ');
        put('s', 's');
        put('t', 'ᴛ');
        put('u', 'ᴜ');
        put('v', 'ᴠ');
        put('w', 'ᴡ');
        put('x', 'x');
        put('y', 'ʏ');
        put('z', 'ᴢ');
    }};

    public static String toSmallCaps(String input)
    {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray())
        {
            result.append(charTable.getOrDefault(c, c));
        }
        return result.toString();
    }
}
