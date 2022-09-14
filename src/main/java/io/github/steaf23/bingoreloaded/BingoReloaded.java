package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoTabCompleter;
import io.github.steaf23.bingoreloaded.command.CardCommand;
import io.github.steaf23.bingoreloaded.command.ItemListCommand;
import io.github.steaf23.bingoreloaded.data.RecoveryCardData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.UIManager;
import io.github.steaf23.bingoreloaded.player.TeamChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BingoReloaded extends JavaPlugin
{
    public static final String NAME = "BingoReloaded";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;

    public static boolean usesPlaceholder = false;

    @Override
    public void onEnable()
    {
        usesPlaceholder = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        BingoGame game = new BingoGame();
        // create UIManager singleton.
        UIManager.getUIManager();

        PluginCommand bingoCommand = getCommand("bingo");
        if (bingoCommand != null)
        {
            bingoCommand.setExecutor(new BingoCommand(game));
            bingoCommand.setTabCompleter( new BingoTabCompleter());
        }

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

        Message.log("[" + getName() + "]" + TranslationData.translate("changed"));
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
    }

    @Override
    public void onDisable()
    {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

    public static void registerListener(Listener listener)
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
