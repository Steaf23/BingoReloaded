package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.util.CounterTimer;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import io.github.steaf23.bingoreloaded.util.GameTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
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

    /**
     * @return the key used to store this task in lists.yml
     */
    public abstract String getKey();

    /**
     * Use isComplete() to change the name depending on if the task has been completed.
     * Used to display task information in the chat.
     * @return chat component holding the name.
     */
    public abstract BaseComponent getDisplayName();

    /**
     * Used to display task information in the chat.
     * @return chat component holding the description.
     */
    public abstract BaseComponent getDescription();

    /**
     * @return item lore of the task that will be displayed on the bingo card item.
     */
    public abstract List<String> getItemLore();

    /**
     * Similar to getName. Use this method to update the item's name through NBT.
     * This means it supports things like translations.
     */
    public abstract void updateItemName();

    public AbstractBingoTask(Material material, ChatColor nameColor)
    {
        this.item = new InventoryItem(material, null);
        this.nameColor = nameColor;
    }

    protected void updateItem()
    {
        updateItemName();
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setLore(getItemLore());
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

        updateItemName();
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
