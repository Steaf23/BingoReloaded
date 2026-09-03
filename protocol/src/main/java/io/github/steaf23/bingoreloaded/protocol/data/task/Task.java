package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public record Task(TaskDefinition task, TaskCompletion completion, int requiredAmount) {

	public record TaskCompletion(boolean completed, String completedByPlayer, String completedByTeam, int teamColor) {

		public static final TaskCompletion INCOMPLETE = new TaskCompletion(false, "", "", 0);
	}

	public static final ByteCodec<Task> CODEC = ByteCodec.create(
			(buf, task) -> {
				ByteCodec.BOOL.encode(buf, task.isCompleted());
				if (task.isCompleted()) {
					ByteCodec.STRING.encode(buf, task.completion().completedByPlayer());
					ByteCodec.STRING.encode(buf, task.completion().completedByTeam());
					ByteCodec.INT.encode(buf, task.completion().teamColor());
				}
				TaskDefinition.CODEC.encode(buf, task.task());
				ByteCodec.INT.encode(buf, task.requiredAmount());

			}, (buf) -> {
				boolean completed = ByteCodec.BOOL.decode(buf);
				Task.TaskCompletion completion;
				if (completed) {
					String player = ByteCodec.STRING.decode(buf);
					String team = ByteCodec.STRING.decode(buf);
					int color = ByteCodec.INT.decode(buf);
					completion = new Task.TaskCompletion(true, player, team, color);
				} else {
					completion = Task.TaskCompletion.INCOMPLETE;
				}

				TaskDefinition task = TaskDefinition.CODEC.decode(buf);
				int requiredAmount = ByteCodec.INT.decode(buf);
				return new Task(task, completion, requiredAmount);
			});

	public boolean isCompleted() {
		return completion().completed();
	}
}
