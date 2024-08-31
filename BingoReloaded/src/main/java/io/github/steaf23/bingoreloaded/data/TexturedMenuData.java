package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.NodeDataAccessor;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.jetbrains.annotations.Nullable;

public class TexturedMenuData
{
    public record Texture (String character, int textureEnd, int menuOffset){}

    private final NodeDataAccessor data = BingoReloaded.getOrCreateDataAccessor("data/textures.yml", NodeDataAccessor.class);

    public @Nullable Texture getTexture(String name) {
        if (!data.contains(name)) {
            ConsoleMessenger.bug("Invalid texture " + name, this);
            return null;
        }

        //TODO, allow me to do this!
//        Node n = data.get(name);
//        String character = n.getString(".char", " ");

        String character = data.getString(name + ".char", " ");
        int textureEnd = data.getInt(name + ".texture_end", 0);
        int menuOffset = data.getInt(name + ".menu_offset", 0);
        return new Texture(character, textureEnd, menuOffset);
    }
}
