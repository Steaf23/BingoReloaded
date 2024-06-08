package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.inventory.item.TaskItemAction;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import io.github.steaf23.easymenulib.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;

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
            this.material = itemTask.material();
            this.glowing = false;
        }
        else if (data instanceof AdvancementTask)
        {
            this.type = TaskType.ADVANCEMENT;
            this.material = Material.FILLED_MAP;
            this.glowing = true;
        }
        else if (data instanceof StatisticTask statTask)
        {
            this.type = TaskType.STATISTIC;
            this.material = BingoStatistic.getMaterial(statTask.statistic());
            this.glowing = true;
        }
        else
        {
            Message.log("This Type of data is not supported by BingoTask: '" + data + "'!");
            this.type = TaskType.ITEM;
            this.glowing = false;
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

    public ItemTemplate toItem()
    {
        ItemTemplate item;
        // Step 1: create the item and put the new name, description and material on it.
        if (isVoided()) // VOIDED TASK
        {
            item = new ItemTemplate(Material.STRUCTURE_VOID, "");
            BaseComponent[] addedDesc = BingoTranslation.VOIDED.asComponent(Set.of(ChatColor.DARK_GRAY));

            ComponentBuilder nameBuilder = new ComponentBuilder().color(ChatColor.DARK_GRAY).strikethrough(true);
            nameBuilder.append("A").obfuscated(true);
            nameBuilder.append(data.getName()).obfuscated(false);
            nameBuilder.append("A").obfuscated(true);

            item.setName(nameBuilder.create());
            item.setLore(addedDesc);
            item.setGlowing(true);
        }
        else if (isCompleted()) // COMPLETED TASK
        {
            Material completeMaterial = Material.BARRIER;

            String timeString = GameTimer.getTimeAsString(completedAt);

            ComponentBuilder nameBuilder = new ComponentBuilder().color(ChatColor.GRAY).strikethrough(true);
            nameBuilder.append(data.getName());

            Set<ChatColor> modifiers = new HashSet<>(){{
                add(ChatColor.DARK_PURPLE);
                add(ChatColor.ITALIC);
            }};
            BaseComponent[] desc = BingoTranslation.COMPLETED_LORE.asComponent(Set.of(ChatColor.DARK_PURPLE, ChatColor.ITALIC),
                    ChatComponentUtils.convert(completedBy.getDisplayName(),
                            completedBy.getTeam().getColor(), ChatColor.BOLD),
                    ChatComponentUtils.convert(timeString, ChatColor.GOLD));

            item = new ItemTemplate(completeMaterial, "");
            item.setName(nameBuilder.create());
            item.setLore(desc);
        }
        else // DEFAULT TASK
        {
            item = new ItemTemplate(material, "");
            item.setName(data.getName());
            item.setLore(data.getItemDescription());

            item.setAmount(data.getStackSize());
        }

        // STEP 2: Add additional stuff like pdc data and glowing effect.

        item.addMetaModifier(meta -> {
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
            return meta;
        });

        if ((glowing || isCompleted()) && !isVoided())
        {
            item.setGlowing(true);
        }

        item.setAction(new TaskItemAction(this));
        return item;
    }

    public static @Nullable BingoTask fromItem(ItemStack in)
    {
        PersistentDataContainer pdcData = in.getItemMeta().getPersistentDataContainer();

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
        if (isCompleted() || isVoided())
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

    public int getCount() {
        if (data instanceof CountableTask countable) {
            return countable.getCount();
        }
        return 1;
    }
}
