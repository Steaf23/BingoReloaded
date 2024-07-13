package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.jetbrains.annotations.Nullable;

public class CustomTextureData
{
    public record Texture (String character, int textureEnd, int menuOffset){}

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/textures.yml");

    public String mapCharacter(String key) {
        return data.getConfig().getString(key, "");
    }

    public @Nullable Texture getTexture(String name) {
        if (!data.getConfig().contains(name)) {
            ConsoleMessenger.bug("Invalid texture " + name, this);
            return null;
        }

        String character = data.getConfig().getString(name + ".char", " ");
        int textureEnd = data.getConfig().getInt(name + ".texture_end", 0);
        int menuOffset = data.getConfig().getInt(name + ".menu_offset", 0);
        return new Texture(character, textureEnd, menuOffset);
    }
}
