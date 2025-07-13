package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    // FIXME: REFACTOR display mode
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
            item = new ItemTemplate(icon(), data.getName(), data.getItemDescription());
            item.setAmount(data.getRequiredAmount());
        }

        // STEP 2: Add additional stuff like pdc data and glowing effect.

        TagDataStorage storage = new TagDataStorage();
        new GameTaskSerializer().toDataStorage(storage, this);
        item.addExtraData("game_task", storage);

        if ((data.shouldItemGlow() || isCompleted()) && !isVoided())
        {
            item.setGlowing(true);
        }

        //FIXME: REFACTOR add task action back to item
//        item.setAction(new TaskItemAction(this));
        item.setMaxStackSize(64);
        return item;
    }

    public static @Nullable GameTask fromItem(StackHandle in)
    {
        DataStorage store = in.getStorage("game_task");
        if (store == null) {
            ConsoleMessenger.bug("No task data found in item", GameTask.class);
            return null;
        }
        return store.toSerializable(GameTask.class);
    }

    public static Key getTaskDataKey(String property)
    {
        return BingoReloaded.resourceKey("task." + property);
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

    public ItemType icon() {
        return data.getDisplayMaterial(displayMode == TaskDisplayMode.GENERIC_TASK_ITEMS);
    }

    public TaskData.TaskType taskType() {
        return data.getType();
    }
}
