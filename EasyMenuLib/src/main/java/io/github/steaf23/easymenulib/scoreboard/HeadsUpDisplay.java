package io.github.steaf23.easymenulib.scoreboard;

import org.bukkit.entity.Player;

public interface HeadsUpDisplay
{
    void setText(int lineNumber, String text);
    void setText(int pageNumber, int lineNumber, String text);
    void subscribePlayer(Player player);
    void unsubscribePlayer(Player player);
    boolean isPlayerSubscribed(Player player);
    void unsubscribeAll();
}
