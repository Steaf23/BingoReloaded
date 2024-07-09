package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ComponentConverter
{
    public static Component bungeeComponentToAdventure(BaseComponent component) {
        return GsonComponentSerializer.gson().deserialize(ChatComponentUtils.toJsonString(component).toString());
    }
}
