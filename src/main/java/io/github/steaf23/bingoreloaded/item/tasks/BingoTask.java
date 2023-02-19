package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.GameTimer;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BingoTask
{
    public enum TaskType
    {
        ITEM,
        STATISTIC,
        ADVANCEMENT,
    }

    public Optional<BingoPlayer> completedBy;
    public long completedAt;
    private boolean voided;

    public final TaskType type;
    public final TaskData data;
    public final ChatColor nameColor;
    public final Material material;
    public final boolean glowing;

    public BingoTask(TaskData data)
    {
        this.data = data;
        this.completedBy = Optional.ofNullable(null);
        this.voided = false;
        this.completedAt = -1L;

        if (data instanceof ItemTask itemTask)
        {
            this.type = TaskType.ITEM;
            this.nameColor = ChatColor.YELLOW;
            this.material = itemTask.material();
            this.glowing = false;
        }
        else if (data instanceof AdvancementTask advTask)
        {
            this.type = TaskType.ADVANCEMENT;
            this.nameColor = ChatColor.GREEN;
            this.material = Material.FILLED_MAP;
            this.glowing = true;
        }
        else if (data instanceof StatisticTask statTask)
        {
            this.type = TaskType.STATISTIC;
            this.nameColor = ChatColor.LIGHT_PURPLE;
            this.material = BingoStatistic.getMaterial(statTask.statistic());
            this.glowing = true;
        }
        else
        {
            Message.log("This Type of data is not supported by BingoTask: '" + data + "'!");
            this.type = TaskType.ITEM;
            this.glowing = false;
            this.nameColor = ChatColor.WHITE;
            this.material = Material.BEDROCK;
        }
    }

    public void setVoided(boolean value)
    {
        if (value && completedBy.isEmpty())
            return;

        voided = value;
    }

    public boolean isVoided()
    {
        return voided && completedBy.isPresent();
    }

    public InventoryItem asStack()
    {
        ItemStack item;

        // Step 1: create the item and put the new name, description and material on it.
        if (isVoided()) // VOIDED TASK
        {
            ItemText addedDesc = new ItemText(TranslationData.translate("game.team.voided",
                    completedBy.get().team().getColoredName().asLegacyString()), ChatColor.DARK_GRAY);

            ItemText itemName = new ItemText(ChatColor.DARK_GRAY, ChatColor.STRIKETHROUGH);
            itemName.addText("A", ChatColor.MAGIC);
            itemName.add(data.getItemDisplayName());
            itemName.addText("A", ChatColor.MAGIC);

            item = new ItemStack(Material.BEDROCK);
            ItemText.buildItemText(item, itemName, addedDesc);
        }
        else if (completedBy.isPresent()) // COMPLETED TASK
        {
            Material completeMaterial = completedBy.get().getTeam().getColor().glassPane;

            String timeString = GameTimer.getTimeAsString(completedAt);

            ItemText itemName = new ItemText(ChatColor.GRAY, ChatColor.STRIKETHROUGH);
            itemName.add(data.getItemDisplayName());

            Set<ChatColor> modifiers = new HashSet<>(){{
                add(ChatColor.DARK_PURPLE);
                add(ChatColor.ITALIC);
            }};
            ItemText[] desc = TranslationData.translateToItemText("game.item.complete_lore", modifiers,
                    new ItemText(completedBy.get().gamePlayer().get().getDisplayName(),
                            completedBy.get().getTeam().getColor().chatColor, ChatColor.BOLD),
                    new ItemText(timeString, ChatColor.GOLD));

            item = new ItemStack(completeMaterial);
            ItemText.buildItemText(item,
                    itemName,
                    desc);

            ItemMeta meta = item.getItemMeta();
            if (meta != null)
            {
                item.setItemMeta(meta);
            }
        }
        else // DEFAULT TASK
        {
            ItemText itemName = new ItemText(nameColor);
            itemName.add(data.getItemDisplayName());

            item = new ItemStack(material);
            ItemText.buildItemText(item,
                    itemName,
                    data.getItemDescription());

            item.setAmount(data.getStackSize());
        }

        // STEP 2: Add additional stuff like pdc data and glowing effect.

        InventoryItem finalItem = new InventoryItem(item);
        ItemMeta meta = finalItem.getItemMeta();
        PersistentDataContainer pdcData = meta.getPersistentDataContainer();
        // Serialize specific data first, to catch null pointers from incomplete implementations.
        pdcData = data.pdcSerialize(pdcData);
        // Then serialize generic task info/ live data
        pdcData.set(getTaskDataKey("type"), PersistentDataType.STRING, type.name());
        pdcData.set(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)(voided ? 1 : 0));
        pdcData.set(getTaskDataKey("completed_at"), PersistentDataType.LONG, completedAt);
        if (completedBy.isPresent())
            pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, completedBy.get().gamePlayer().get().getUniqueId().toString());
        else
            pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");

        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        finalItem.setItemMeta(meta);

        if (glowing && completedBy.isEmpty())
        {
            finalItem.highlight(true);
        }

        return finalItem;
    }

    public static BingoTask fromStack(ItemStack in)
    {
        PersistentDataContainer pdcData = in.getItemMeta().getPersistentDataContainer();

        boolean voided = pdcData.getOrDefault(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)0) != 0;
        UUID completedBy = null;
        String idStr = pdcData.getOrDefault(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");
        long timeStr = pdcData.getOrDefault(getTaskDataKey("completed_at"), PersistentDataType.LONG, -1L);
        if (idStr != "")
            completedBy = UUID.fromString(idStr);

        String typeStr = pdcData.getOrDefault(getTaskDataKey("type"), PersistentDataType.STRING, "");
        TaskType type;
        if (typeStr.isEmpty())
        {
            Message.log("Cannot create a valid task from this item stack!");
            return null;
        }

        type = TaskType.valueOf(typeStr);
        BingoTask task = switch (type)
        {
            case ADVANCEMENT -> new BingoTask(AdvancementTask.fromPdc(pdcData));
            case STATISTIC -> new BingoTask(StatisticTask.fromPdc(pdcData));
            default ->  new BingoTask(ItemTask.fromPdc(pdcData));
        };

        return task;
    }

    public static NamespacedKey getTaskDataKey(String property)
    {
        return PDCHelper.createKey("task." + property);
    }

    public boolean complete(BingoPlayer player, long time)
    {
        if (completedBy.isPresent())
            return false;

        completedBy = Optional.of(player);
        completedAt = time;

        String timeString = GameTimer.getTimeAsString(time);

        new Message("game.item.completed").color(ChatColor.GREEN)
                .component(data.getItemDisplayName().asComponent()).color(nameColor)
                .arg(new ItemText(player.gamePlayer().get().getDisplayName(), player.team().getColor().chatColor, ChatColor.BOLD).asLegacyString())
                .arg(timeString).color(ChatColor.WHITE)
                .sendAll(player.worldName());
        return true;
    }

    public BingoTask copy()
    {
        return new BingoTask(data);
    }
}
