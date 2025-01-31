package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.item.TaskItemAction;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import io.github.steaf23.playerdisplay.util.PDCHelper;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class GameTask
{
    public enum TaskType
    {
        ITEM,
        STATISTIC,
        ADVANCEMENT,
    }

    public enum TaskDisplayMode
    {
        GENERIC_TASK_ITEMS, // Shows a filled map for all advancements and a banner pattern for all statistic tasks.
        UNIQUE_TASK_ITEMS, // Item type to show is based on the actual contents of the tasks, just like for item tasks.
    }

    private BingoParticipant completedBy;
    public long completedAt;
    private boolean voided;

    public final TaskType type;
    public final TaskData data;
    public final Material material;
    public final boolean glowing;

    public final TaskDisplayMode displayMode;

    public GameTask(TaskData data, TaskDisplayMode displayMode)
    {
        this.data = data;
        this.completedBy = null;
        this.voided = false;
        this.completedAt = -1L;
        this.displayMode = displayMode;

        switch (data) {
            case ItemTask itemTask -> {
                this.type = TaskType.ITEM;
                this.material = itemTask.material();
                this.glowing = false;
            }
            case AdvancementTask advancementTask -> {
                this.type = TaskType.ADVANCEMENT;
                AdvancementDisplay display = advancementTask.advancement().getDisplay();
                if (display == null || displayMode == TaskDisplayMode.GENERIC_TASK_ITEMS) {
                    this.material = Material.FILLED_MAP;
                } else {
                    this.material = advancementTask.advancement().getDisplay().icon().getType();
                }
                this.glowing = true;
            }
            case StatisticTask statTask -> {
                this.type = TaskType.STATISTIC;
                if (displayMode == TaskDisplayMode.GENERIC_TASK_ITEMS) {
                    this.material = Material.GLOBE_BANNER_PATTERN;
                } else {
                    this.material = BingoStatistic.getMaterial(statTask.statistic());
                }
                this.glowing = true;
            }
            case null, default -> {
                ConsoleMessenger.bug("This Type of data is not supported by BingoTask: '" + data + "'!", this);
                this.type = TaskType.ITEM;
                this.glowing = false;
                this.material = Material.BEDROCK;
            }
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
            item = new ItemTemplate(Material.STRUCTURE_VOID, null);
            Component[] addedDesc = BingoMessage.VOIDED.asMultiline(NamedTextColor.DARK_GRAY);

            item.setName(getName());
            item.setLore(addedDesc);
            item.setGlowing(true);
        }
        else if (isCompleted()) // COMPLETED TASK
        {
            Material completeMaterial = Material.BARRIER;

            String timeString = GameTimer.getTimeAsString(completedAt);

            Component[] desc = BingoMessage.COMPLETED_LORE.asMultiline(NamedTextColor.DARK_PURPLE,
                    completedBy.getDisplayName()
                            .color(completedBy.getTeam().getColor())
                            .decorate(TextDecoration.BOLD)
                            .decorate(TextDecoration.ITALIC),
                    Component.text(timeString)
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.ITALIC));

            item = new ItemTemplate(completeMaterial, getName(), desc);
        }
        else // DEFAULT TASK
        {
            item = new ItemTemplate(material, data.getName(), data.getItemDescription());
            item.setAmount(Math.min(64, data.getRequiredAmount()));
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

    public static @Nullable GameTask fromItem(ItemStack in)
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
            ConsoleMessenger.log("Cannot create a valid task from this item stack!");
            return null;
        }

        type = TaskType.valueOf(typeStr);
        GameTask task = switch (type)
        {
            case ADVANCEMENT -> new GameTask(AdvancementTask.fromPdc(pdcData), TaskDisplayMode.UNIQUE_TASK_ITEMS);
            case STATISTIC -> new GameTask(StatisticTask.fromPdc(pdcData), TaskDisplayMode.UNIQUE_TASK_ITEMS);
            default -> new GameTask(ItemTask.fromPdc(pdcData), TaskDisplayMode.UNIQUE_TASK_ITEMS);
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

    public GameTask copy()
    {
        return new GameTask(data, displayMode);
    }

    public Optional<BingoParticipant> getCompletedBy() {
        return Optional.ofNullable(completedBy);
    }

    public Component getName() {
        if (isVoided())
        {
            TextComponent.Builder nameBuilder = Component.text()
                    .color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.STRIKETHROUGH);
            nameBuilder.append(Component.text("A").decorate(TextDecoration.OBFUSCATED));
            nameBuilder.append(data.getName().color(NamedTextColor.DARK_GRAY));
            nameBuilder.append(Component.text("A").decorate(TextDecoration.OBFUSCATED));
            return nameBuilder.build();
        }
        else if (isCompleted()) {
            TextComponent.Builder nameBuilder = Component.text()
                    .color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH);
            nameBuilder.append(data.getName());
            return nameBuilder.build();
        }
        else {
            return data.getName();
        }
    }
}
