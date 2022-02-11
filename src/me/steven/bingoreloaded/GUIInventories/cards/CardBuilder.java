package me.steven.bingoreloaded.GUIInventories.cards;

import me.steven.bingoreloaded.BingoGameMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGameMode mode)
    {
        return switch (mode)
                {
                    case LOCKOUT -> lockout();
                    case RUSH -> rush();
                    default -> regular();
                };
    }

    private static BingoCard regular()
    {
        return new BingoCard(CardSize.X5);
    }

    private static BingoCard lockout()
    {
        return new BingoCard(CardSize.X5);
    }

    private static BingoCard rush()
    {
        return new BingoCard(CardSize.X3);
    }

    public static Material completeColor(Team team)
    {
        ChatColor color = ChatColor.getByChar((char) team.getDisplayName().getBytes()[1]);
        return switch (Objects.requireNonNull(color))
                {
                    case RED -> Material.RED_STAINED_GLASS_PANE;
                    case DARK_RED -> Material.BROWN_STAINED_GLASS_PANE;
                    case DARK_PURPLE -> Material.PURPLE_STAINED_GLASS_PANE;
                    case DARK_AQUA -> Material.CYAN_STAINED_GLASS_PANE;
                    case DARK_BLUE -> Material.BLUE_STAINED_GLASS_PANE;
                    case DARK_GRAY -> Material.GRAY_STAINED_GLASS_PANE;
                    case DARK_GREEN -> Material.GREEN_STAINED_GLASS_PANE;
                    case BLACK -> Material.BLACK_STAINED_GLASS_PANE;
                    case GOLD -> Material.ORANGE_STAINED_GLASS_PANE;
                    case GREEN -> Material.LIME_STAINED_GLASS_PANE;
                    case GRAY -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
                    case LIGHT_PURPLE -> Material.PINK_STAINED_GLASS_PANE;
                    case AQUA -> Material.MAGENTA_STAINED_GLASS_PANE;
                    case YELLOW -> Material.YELLOW_STAINED_GLASS_PANE;
                    case BLUE -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                    default -> Material.WHITE_STAINED_GLASS_PANE;
                };
    }
}
