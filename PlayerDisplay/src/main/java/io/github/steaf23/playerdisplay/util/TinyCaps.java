package io.github.steaf23.playerdisplay.util;

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.HashMap;
import java.util.Map;

public class TinyCaps
{
    public static final TagResolver TAG_RESOLVER = tagResolver();

    private static final Map<Character, Character> charTable = new HashMap<>(){{
        put('a', 'ᴀ');
        put('b', 'ʙ');
        put('c', 'ᴄ');
        put('d', 'ᴅ');
        put('e', 'ᴇ');
        put('f', 'ꜰ');
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
        put('q', 'ꞯ');
        put('r', 'ʀ');
        put('s', 's');
        put('t', 'ᴛ');
        put('u', 'ᴜ');
        put('v', 'ᴠ');
        put('w', 'ᴡ');
        put('x', 'х');
        put('y', 'ʏ');
        put('z', 'ᴢ');

        put('A', 'ᴀ');
        put('B', 'ʙ');
        put('C', 'ᴄ');
        put('D', 'ᴅ');
        put('E', 'ᴇ');
        put('F', 'ꜰ');
        put('G', 'ɢ');
        put('H', 'ʜ');
        put('I', 'ɪ');
        put('J', 'ᴊ');
        put('K', 'ᴋ');
        put('L', 'ʟ');
        put('M', 'ᴍ');
        put('N', 'ɴ');
        put('O', 'ᴏ');
        put('P', 'ᴘ');
        put('Q', 'ꞯ');
        put('R', 'ʀ');
        put('S', 's');
        put('T', 'ᴛ');
        put('U', 'ᴜ');
        put('V', 'ᴠ');
        put('W', 'ᴡ');
        put('X', 'х');
        put('Y', 'ʏ');
        put('Z', 'ᴢ');
    }};

    public static String toTinyCaps(String input)
    {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray())
        {
            result.append(charTable.getOrDefault(c, c));
        }
        return result.toString();
    }

    /**
     * Tag resolver for MiniMessage. Can be used by typing
     *  {@code <tiny:'some text'>} which will convert the text {@code some text} into tiny caps
     * @return
     */
    private static TagResolver tagResolver() {
        return TagResolver.resolver("tiny", (args, ctx) -> {
            if (args.hasNext()) {
                return Tag.preProcessParsed(toTinyCaps(args.pop().value()));
            }
            return Tag.preProcessParsed("");
        });
    }
}
