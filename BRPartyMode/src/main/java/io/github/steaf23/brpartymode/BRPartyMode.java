package io.github.steaf23.brpartymode;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoReloadedExtension;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.command.BingoTabCompleter;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.gui.base.MenuEventListener;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class BRPartyMode extends BingoReloadedExtension
{
    public static final String NAME = "BingoReloadedPartyMode";

    private BingoSession session;
    private BingoEventListener listener;
    private MenuEventListener menuManager;

    @Override
    public void onEnable()
    {

        core.onEnable();

        this.core = new BingoReloadedCore(this);
        //TODO: replace with world_name config option
        this.session = new BingoSession("bingo");
        this.listener = new BingoEventListener(world ->
            BingoReloadedCore.getWorldNameOfDimension(world).equals(session.worldName) ? session : null
        , false, false);
        this.menuManager = new MenuEventListener(inventoryView -> {
            String worldName = BingoReloadedCore.getWorldNameOfDimension(inventoryView.getPlayer().getWorld());
            return worldName.equals(session.worldName);
        });

        Bukkit.getPluginManager().registerEvents(listener, this);
        Bukkit.getPluginManager().registerEvents(menuManager, this);

        Message.log(ChatColor.GREEN + "Enabled " + this.getName());
    }

    @Override
    public void onDisable()
    {
        core.onDisable();

        HandlerList.unregisterAll(listener);
        HandlerList.unregisterAll(menuManager);

        Bukkit.getLogger().info(org.bukkit.ChatColor.RED + "Disabled " + this.getName());
    }

    @Override
    public BingoReloadedCore getCore()
    {
        return core;
    }
}
