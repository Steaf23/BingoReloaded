package io.github.steaf23.bingoreloaded;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BingoReloadedExtension extends JavaPlugin
{
    protected BingoReloadedCore core;

    public BingoReloadedExtension()
    {
        core = (BingoReloadedCore)Bukkit.getPluginManager().getPlugin(BingoReloadedCore.NAME);
    }
}
