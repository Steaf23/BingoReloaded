package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.item.TaskItemAction;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.bingoreloaded.lib.inventory.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GameTask
{
    public enum TaskDisplayMode
    {
        GENERIC_TASK_ITEMS, // Shows a filled map for all advancements and a banner pattern for all statistic tasks.
        UNIQUE_TASK_ITEMS, // Item type to show is based on the actual contents of the tasks, just like for item tasks.
    }

    private BingoParticipant completedBy;
    private BingoTeam completedByTeam;
    public long completedAt;
    private boolean voided;

    public final TaskData data;

    public final TaskDisplayMode displayMode;

    public GameTask(TaskData data, TaskDisplayMode displayMode)
    {
        this.data = data;
        this.completedBy = null;
        this.completedByTeam = null;
        this.voided = false;
        this.completedAt = -1L;
        this.displayMode = displayMode;
    }

    public static GameTask simpleItemTask(ItemType material, int count) {
        return new GameTask(new ItemTask(material, count), TaskDisplayMode.UNIQUE_TASK_ITEMS);
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
        return completedBy != null || completedByTeam != null;
    }

    public ItemTemplate toItem()
    {
        ItemTemplate item;
        // Step 1: create the item and put the new name, description and material on it.
        if (isVoided()) // VOIDED TASK
        {
            item = new ItemTemplate(ItemType.of("structure_void"), null);
            Component[] addedDesc = BingoMessage.VOIDED.asMultiline(NamedTextColor.DARK_GRAY);

            item.setName(getName());
            item.setLore(addedDesc);
            item.setGlowing(true);
        }
        else if (isCompleted()) // COMPLETED TASK
        {
            ItemType completeMaterial = ItemType.of("barrier");

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
            item = new ItemTemplate(material(), data.getName(), data.getItemDescription());
            item.setAmount(data.getRequiredAmount());
        }

        // STEP 2: Add additional stuff like pdc data and glowing effect.

        item.addMetaModifier(meta -> {
            PersistentDataContainer pdcData = meta.getPersistentDataContainer();
            // Serialize specific data first, to catch null pointers from incomplete implementations.
            pdcData = data.pdcSerialize(pdcData);
            // Then serialize generic task info/ live data
            pdcData.set(getTaskDataKey("type"), PersistentDataType.STRING, taskType().name());
            pdcData.set(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)(voided ? 1 : 0));
            pdcData.set(getTaskDataKey("completed_at"), PersistentDataType.LONG, completedAt);
            if (isCompleted()) {
                pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, completedBy.getId().toString());
                pdcData.set(getTaskDataKey("completed_by_team"), PersistentDataType.STRING, completedByTeam.getIdentifier());
            } else {
                pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");
                pdcData.set(getTaskDataKey("completed_by_team"), PersistentDataType.STRING, "");
            }
            return meta;
        });

        if ((data.shouldItemGlow() || isCompleted()) && !isVoided())
        {
            item.setGlowing(true);
        }

        item.setAction(new TaskItemAction(this));
        item.setMaxStackSize(64);
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
        TaskData.TaskType type;
        if (typeStr.isEmpty())
        {
            ConsoleMessenger.log("Cannot create a valid task from this item stack!");
            return null;
        }

        type = TaskData.TaskType.valueOf(typeStr);
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

    public static Key getTaskDataKey(String property)
    {
        return PDCHelper.createKey("task." + property);
    }

    public boolean complete(BingoParticipant participant, long gameTime)
    {
        if (isCompleted() || isVoided())
            return false;

        completedByTeam = participant.getTeam();
        completedBy = participant;
        completedAt = gameTime;
        return true;
    }

    public GameTask copy()
    {
        return new GameTask(data, displayMode);
    }

    public Optional<BingoParticipant> getCompletedByPlayer() {
        return Optional.ofNullable(completedBy);
    }

    public Optional<BingoTeam> getCompletedByTeam() {
        return Optional.ofNullable(completedByTeam);
    }

    public boolean isCompletedByTeam(@NotNull BingoTeam team) {
        return team.equals(completedByTeam);
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

    public ItemType material() {
        return data.getDisplayMaterial(displayMode == TaskDisplayMode.GENERIC_TASK_ITEMS);
    }

    public TaskData.TaskType taskType() {
        return data.getType();
    }
}
