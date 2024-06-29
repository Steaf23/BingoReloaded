package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;

public class BingoPlayerSender
{
    public static void sendMessage(Component message, Audience audience) {
        Component prefix = BingoMessage.MESSAGE_PREFIX.asPhrase();
        audience.sendMessage(Component.text().append(prefix, message).build());
    }

    public static void sendTitle(Component title, Audience audience) {
        audience.sendTitlePart(TitlePart.TITLE, title);
    }

    public static void sendTitle(Component title, Component subTitle, Audience audience) {
        audience.sendTitlePart(TitlePart.TITLE, title);
        audience.sendTitlePart(TitlePart.SUBTITLE, subTitle);
    }
}
