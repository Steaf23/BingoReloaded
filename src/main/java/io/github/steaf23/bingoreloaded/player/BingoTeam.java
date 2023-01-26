package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;

import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.List;

public class BingoTeam
{
    public final Team team;
    public BingoCard card;
    public boolean outOfTheGame = false;

    private FlexColor color;

    public BingoTeam(Team team, BingoCard card, FlexColor color)
    {
        this.team = team;
        this.card = card;
        this.color = color;
    }

    public String getName()
    {
        return team.getDisplayName();
    }

    public FlexColor getColor()
    {
        return color;
    }

    public List<OfflinePlayer> getPlayers()
    {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> team.getEntries().contains(p.getName())).toList();
    }

    public ItemText getColoredName()
    {
        return new ItemText(color.getTranslatedName(), color.chatColor, ChatColor.BOLD);
    }
}