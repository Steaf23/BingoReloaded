package me.steven.bingoreloaded.item;

import me.steven.bingoreloaded.GameTimer;
import me.steven.bingoreloaded.data.MessageSender;
import me.steven.bingoreloaded.data.TranslationData;
import me.steven.bingoreloaded.gui.cards.CardBuilder;
import me.steven.bingoreloaded.player.BingoTeam;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BingoItem
{
    public final Material item;
    public final InventoryItem stack;
    public final String translatePath;

    private BingoTeam completedBy = null;

    public BingoItem(Material item)
    {
        this.item = item;
        this.stack = new InventoryItem(item, null, "I am a bingo Item :D");
        this.translatePath = getTranslatePath(item);
    }

    public boolean isComplete(BingoTeam team)
    {
        if (completedBy == null) return false;

        return completedBy.getName().equals(team.getName());
    }

    public void complete(BingoTeam team, int time)
    {
        if (completedBy != null) return;

        completedBy = team;
        Material completeMaterial = CardBuilder.completeColor(team);
        String timeString = GameTimer.getTimeAsString(time);

        MessageSender.send("game.item.completed", List.of(translatePath, completedBy.team.getColor() + completedBy.getName(), timeString));

        String crossedName = "" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + new TranslatableComponent(translatePath).toPlainText();
        stack.setType(completeMaterial);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(crossedName);
            meta.setLore(List.of("Completed by team " + completedBy.team.getColor() + completedBy.getName(),
                    "At " + ChatColor.GOLD + timeString + ChatColor.RESET + ""));
            stack.setItemMeta(meta);
        }
    }

    public BingoTeam getWhoCompleted()
    {
        return completedBy;
    }

    public void voidItem()
    {
        stack.setType(Material.BEDROCK);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName("" + ChatColor.BLACK + ChatColor.STRIKETHROUGH + TranslationData.get(translatePath));
            meta.setLore(List.of(ChatColor.BLACK + "This team is out of the game!"));
            stack.setItemMeta(meta);
        }
    }

    public static String getTranslatePath(Material item)
    {
        NamespacedKey keyedName = item.getKey();
        String type = item.isBlock() ? "block" : "item";
        return type + ".minecraft." + keyedName.getKey();
    }
}
