package me.steven.bingoreloaded;

import me.steven.bingoreloaded.command.BingoCommand;
import me.steven.bingoreloaded.command.CardCommand;
import me.steven.bingoreloaded.command.ItemListCommand;
import me.steven.bingoreloaded.data.RecoveryCardData;
import me.steven.bingoreloaded.data.TranslationData;
import me.steven.bingoreloaded.player.TeamChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BingoReloaded extends JavaPlugin
{
    public static final String NAME = "BingoReloaded";

    @Override
    public void onEnable()
    {
        TranslationData.populateTranslations();

        BingoGame game = new BingoGame();

        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());

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

        getServer().getPluginManager().registerEvents(game, this);

        if (RecoveryCardData.loadCards(game.getTeamManager()))
        {
            game.resume();
        }
    }

    @Override
    public void onDisable()
    {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }
}
