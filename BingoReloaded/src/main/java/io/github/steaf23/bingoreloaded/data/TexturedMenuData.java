package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.jetbrains.annotations.Nullable;

public class TexturedMenuData
{
    public record Texture (String character, int textureEnd, int menuOffset){}

    private final DataAccessor data = BingoReloaded.getDataAccessor("data/textures");

    public @Nullable Texture getTexture(String name) {
        if (!data.contains(name)) {
            ConsoleMessenger.bug("Invalid texture " + name, this);
            return null;
        }

        DataStorage n = data.getStorage(name);
        if (n == null) {
            return null;
        }

        String character = n.getString("char", " ");
        int textureEnd = n.getInt("texture_end", 0);
        int menuOffset = n.getInt("menu_offset", 0);

        return new Texture(character, textureEnd, menuOffset);
    }
}
