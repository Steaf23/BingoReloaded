package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.Set;

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
	Set<String> tags();

    ItemType getDisplayMaterial(CardDisplayInfo context);

    int getRequiredAmount();

    /**
     * @return Copy with the new amount.
     */
    TaskData setRequiredAmount(int newAmount);

	default boolean hasAnyTag(Set<String> tagsToCheck) {
		return !Collections.disjoint(tagsToCheck, tags());
	}
}
