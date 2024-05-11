package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.item.TaskItemAction;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.menu.item.ItemText;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import io.github.steaf23.easymenulib.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BingoTask
{
    public enum TaskType
    {
        ITEM,
        STATISTIC,
        ADVANCEMENT,
    }

    private BingoParticipant completedBy;
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
        this.completedBy = null;
        this.voided = false;
        this.completedAt = -1L;

        if (data instanceof ItemTask itemTask)
        {
            this.type = TaskType.ITEM;
            this.nameColor = ChatColor.YELLOW;
            this.material = itemTask.material();
            this.glowing = false;
        }
        else if (data instanceof AdvancementTask)
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
        if (isCompleted())
            return;

        voided = value;
    }

    public boolean isVoided()
    {
        return voided;
    }

    public boolean isCompleted()
    {
        return completedBy != null;
    }

    public MenuItem asMenuItem()
    {
        ItemStack item;
        // Step 1: create the item and put the new name, description and material on it.
        if (isVoided()) // VOIDED TASK
        {
            ItemText addedDesc = new ItemText(BingoTranslation.VOIDED.translate(
                    completedBy.getTeam().getColoredName().asLegacyString()), ChatColor.DARK_GRAY);

            ItemText itemName = new ItemText(ChatColor.DARK_GRAY, ChatColor.STRIKETHROUGH);
            itemName.addText("A", ChatColor.MAGIC);
            itemName.add(data.getItemDisplayName());
            itemName.addText("A", ChatColor.MAGIC);

            item = new ItemStack(Material.BEDROCK);
            ItemText.buildItemText(item, itemName, addedDesc);
        }
        else if (isCompleted()) // COMPLETED TASK
        {
            Material completeMaterial = Material.BARRIER;

            String timeString = GameTimer.getTimeAsString(completedAt);

            ItemText itemName = new ItemText(ChatColor.GRAY, ChatColor.STRIKETHROUGH);
            itemName.add(data.getItemDisplayName());

            Set<ChatColor> modifiers = new HashSet<>(){{
                add(ChatColor.DARK_PURPLE);
                add(ChatColor.ITALIC);
            }};
            ItemText[] desc = BingoTranslation.COMPLETED_LORE.asItemText(modifiers,
                    new ItemText(completedBy.getDisplayName(),
                            completedBy.getTeam().getColor(), ChatColor.BOLD),
                    new ItemText(timeString, ChatColor.GOLD));

            item = new ItemStack(completeMaterial);
            ItemText.buildItemText(item,
                    itemName,
                    desc);
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

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdcData = meta.getPersistentDataContainer();
        // Serialize specific data first, to catch null pointers from incomplete implementations.
        pdcData = data.pdcSerialize(pdcData);
        // Then serialize generic task info/ live data
        pdcData.set(getTaskDataKey("type"), PersistentDataType.STRING, type.name());
        pdcData.set(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)(voided ? 1 : 0));
        pdcData.set(getTaskDataKey("completed_at"), PersistentDataType.LONG, completedAt);
        if (isCompleted())
            pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, completedBy.getId().toString());
        else
            pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");

        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
        item.setItemMeta(meta);

        MenuItem finalItem = new MenuItem(item);
        if (glowing || isCompleted())
        {
            finalItem.setGlowing(true);
        }

        finalItem.setAction(new TaskItemAction(this));
        return finalItem;
    }

    public static @Nullable BingoTask fromMenuItem(MenuItem in)
    {
        PersistentDataContainer pdcData = in.buildStack().getItemMeta().getPersistentDataContainer();

        boolean voided = pdcData.getOrDefault(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)0) != 0;
        UUID completedByUUID = null;
        String idStr = pdcData.getOrDefault(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");
        long timeStr = pdcData.getOrDefault(getTaskDataKey("completed_at"), PersistentDataType.LONG, -1L);
        if (!idStr.isEmpty())
            completedByUUID = UUID.fromString(idStr);

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
            default -> new BingoTask(ItemTask.fromPdc(pdcData));
        };

        task.voided = voided;
        task.completedAt = timeStr;
        //TODO: implement completedBy deserialization (need access to teamManager to get participant object).

        return task;
    }

    public static NamespacedKey getTaskDataKey(String property)
    {
        return PDCHelper.createKey("task." + property);
    }

    public boolean complete(BingoParticipant participant, long gameTime)
    {
        if (isCompleted())
            return false;

        completedBy = participant;
        completedAt = gameTime;
        return true;
    }

    public BingoTask copy()
    {
        return new BingoTask(data);
    }

    public Optional<BingoParticipant> getCompletedBy() {
        return Optional.ofNullable(completedBy);
    }
}
