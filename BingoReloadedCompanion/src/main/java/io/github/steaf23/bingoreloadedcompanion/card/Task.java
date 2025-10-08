package io.github.steaf23.bingoreloadedcompanion.card;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public record Task(TaskCompletion completion, Identifier taskType, Item itemType, int requiredAmount) {


	public record TaskCompletion(boolean completed, String completedByPlayer, String completedByTeam, int teamColor) {

		public static final TaskCompletion INCOMPLETE = new TaskCompletion(false, "", "", 0);
	}

}
