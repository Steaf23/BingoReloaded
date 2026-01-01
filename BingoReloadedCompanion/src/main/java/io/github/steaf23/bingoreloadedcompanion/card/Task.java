package io.github.steaf23.bingoreloadedcompanion.card;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record Task(TaskCompletion completion, Identifier taskType, Item itemType, int requiredAmount) {


	public record TaskCompletion(boolean completed, String completedByPlayer, String completedByTeam, int teamColor) {

		public static final TaskCompletion INCOMPLETE = new TaskCompletion(false, "", "", 0);
	}

}
