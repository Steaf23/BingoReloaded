package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.CardCommand;
import io.github.steaf23.bingoreloaded.command.ItemListCommand;
import io.github.steaf23.bingoreloaded.data.RecoveryCardData;
import io.github.steaf23.bingoreloaded.gui.UIManager;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BingoReloaded extends JavaPlugin
{
    public static final String PRINT_PREFIX = "" + ChatColor.DARK_RED + "[" + ChatColor.DARK_AQUA + ChatColor.BOLD + "Bingo" + ChatColor.YELLOW + ChatColor.ITALIC + "Reloaded" + ChatColor.DARK_RED + "] " + ChatColor.RESET + "";
    public static final String NAME = "BingoReloaded";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;

    @Override
    public void onEnable()
    {
        BingoGame game = new BingoGame();
        // create UIManager singleton.
        UIManager.getUIManager();

        PluginCommand bingoCommand = getCommand("bingo");
        if (bingoCommand != null)
            bingoCommand.setExecutor(new BingoCommand(game));

        PluginCommand cardCommand = getCommand("card");
        if (cardCommand != null)
            cardCommand.setExecutor(new CardCommand());

        PluginCommand itemListCommand = getCommand("itemlist");
        if (itemListCommand != null)
            itemListCommand.setExecutor(new ItemListCommand());

        PluginCommand teamChatCommand = getCommand("btc");
        if (teamChatCommand != null)
            teamChatCommand.setExecutor(new TeamChat(game.getTeamManager()));

        if (RecoveryCardData.loadCards(game))
        {
            game.resume();
        }

        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
    }

    @Override
    public void onDisable()
    {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

    public static void print(String message, @NonNull Player player)
    {
        message = PRINT_PREFIX + message;
        player.sendMessage(message);
    }

    public static void print(TextComponent[] message, @NonNull Player player)
    {
        player.spigot().sendMessage(message);
    }

    public static void print(String message, @NonNull BingoTeam team)
    {
        message = PRINT_PREFIX + message;
        for (String name : team.team.getEntries())
        {
            Player p = Bukkit.getPlayer(name);
            if (p != null)
            {
                p.sendMessage(message);
            }
        }
    }

    public static void print(String message)
    {
        message = PRINT_PREFIX + message;
        Bukkit.getLogger().info(message);
    }

    public static void broadcast(String message)
    {
        for (Player p : Bukkit.getServer().getOnlinePlayers())
        {
            print(message, p);
        }
    }

    public static void broadcast(TextComponent[] component)
    {
        for (Player p : Bukkit.getServer().getOnlinePlayers())
        {
            print(component, p);
        }
    }

    public static void showPlayerActionMessage(String message, Player player)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent[]{new TextComponent(message)});
    }

    public static TextComponent[] createHoverCommandMessage(String beforeText, String commandText, String afterText, String command, String hoverText)
    {
        TextComponent message1 = new TextComponent(PRINT_PREFIX + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + beforeText);
        TextComponent message2 = new TextComponent("" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + afterText);
        TextComponent comp = new TextComponent("" + ChatColor.RED + ChatColor.ITALIC + commandText);
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("" + ChatColor.GREEN + ChatColor.ITALIC + hoverText).create()));

        return new TextComponent[]{message1, comp, message2};
    }

    public static TextComponent[] createHoverInfoMessage(String beforeText, String hoverableText, String afterText, String hoverText)
    {
        TextComponent message1 = new TextComponent(PRINT_PREFIX + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + beforeText);
        TextComponent message2 = new TextComponent("" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + afterText);
        TextComponent comp = new TextComponent("" + ChatColor.RED + ChatColor.ITALIC + hoverableText);
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("" + ChatColor.GREEN + ChatColor.ITALIC + hoverText).create()));

        return new TextComponent[]{message1, comp, message2};
    }

    public static void registerListener(Listener listener)
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
