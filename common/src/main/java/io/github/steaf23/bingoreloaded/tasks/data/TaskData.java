package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.text.Component;

public interface TaskData
{
    enum TaskType
    {
        ITEM("item"),
        STATISTIC("statistic"),
        ADVANCEMENT("advancement"),
		;

		public final String id;

		TaskType(String id) {
			this.id = id;
		}
	}

    TaskType getType();
    Component getName();
    Component getChatDescription();
    Component[] getItemDescription();
    boolean isTaskEqual(TaskData other);
    boolean shouldItemGlow();

    ItemType getDisplayMaterial(CardDisplayInfo context);

    int getRequiredAmount();

    /**
     * @return Copy with the new amount.
     */
    TaskData setRequiredAmount(int newAmount);
}
