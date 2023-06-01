package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.game.BingoSession;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("players.yml");

    public void savePlayer(BingoSession session, Player player)
    {
        data.getConfig().set("yeetus", SerializablePlayer.fromPlayer(BingoReloaded.getPlugin(BingoReloaded.class), player));
        data.saveConfig();
    }

    public Player loadPlayer(BingoSession session, Player player)
    {
        data.getConfig().getSerializable("yeetus", SerializablePlayer.class).toPlayer(player);
        return player;
    }
}