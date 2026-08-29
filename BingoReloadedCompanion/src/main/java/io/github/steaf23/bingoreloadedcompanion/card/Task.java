package io.github.steaf23.bingoreloadedcompanion.card;

import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskDefinition;

public record Task(TaskDefinition task, TaskCompletion completion, int requiredAmount) {


	public record TaskCompletion(boolean completed, String completedByPlayer, String completedByTeam, int teamColor) {

		public static final TaskCompletion INCOMPLETE = new TaskCompletion(false, "", "", 0);
	}

}
