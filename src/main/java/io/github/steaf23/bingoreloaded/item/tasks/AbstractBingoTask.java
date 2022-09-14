package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.GameTimer;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.AdvancementData;
import io.github.steaf23.bingoreloaded.data.RecoveryCardData;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractBingoTask
{
    public final InventoryItem item;
    public final ChatColor nameColor;

    private BingoTeam completedBy = null;

    public abstract AbstractBingoTask copy();
    public abstract String getKey();
    // use isComplete() to change the name depending on if the task has been completed.
    public abstract BaseComponent getDisplayName();
    // Similar to updateItemName. use this method to update the item's NBT.
    public abstract void updateItemNBT();
    public abstract List<String> getDescription();

    public AbstractBingoTask(Material material, ChatColor nameColor)
    {
        this.item = new InventoryItem(material, null);
        this.nameColor = nameColor;
    }

    protected void updateItem()
    {
        updateItemNBT();
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setLore(getDescription());
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
    }

    public boolean isComplete()
    {
        return completedBy != null;
    }

    public boolean isCompletedByTeam(BingoTeam team)
    {
        if (!isComplete())
            return false;

        return completedBy.getName().equals(team.getName());
    }

    public boolean complete(BingoTeam team, int time)
    {
        if (isComplete())
            return false;

        Material completeMaterial = CardBuilder.completeColor(team);

        BaseComponent itemName = getDisplayName();
        completedBy = team;

        String timeString = GameTimer.getTimeAsString(time);

        new Message("game.item.completed").color(ChatColor.GREEN)
                .component(itemName).color(nameColor)
                .arg(FlexibleColor.fromName(completedBy.getName()).getTranslation()).color(completedBy.getColor()).bold()
                .arg(timeString).color(ChatColor.WHITE)
                .sendAll();

        updateItemNBT();
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setLore(Arrays.stream(new Message("game.item.complete_lore").color(ChatColor.DARK_PURPLE).italic()
                    .arg(FlexibleColor.fromName(completedBy.getName()).getTranslation()).color(completedBy.getColor()).bold()
                    .arg(timeString).color(ChatColor.GOLD).toLegacyString().split("\\n")).toList());
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        item.setType(completeMaterial);
        return true;
    }

    public BingoTeam getWhoCompleted()
    {
        return completedBy;
    }

    public void voidTask()
    {
        item.setType(Material.BEDROCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName("" + ChatColor.BLACK + ChatColor.STRIKETHROUGH + getDisplayName());
            meta.setLore(List.of(ChatColor.BLACK + "This team is out of the game!"));
            item.setItemMeta(meta);
        }
    }
}
