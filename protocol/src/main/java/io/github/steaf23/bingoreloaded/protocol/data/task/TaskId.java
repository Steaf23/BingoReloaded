package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import net.kyori.adventure.key.Key;

public sealed interface TaskId {

	TaskType type();

	record Item(Key id) implements TaskId {
		@Override
		public TaskType type() {
			return TaskType.ITEM;
		}
	}

	record Advancement(Key id) implements TaskId {
		@Override
		public TaskType type() {
			return TaskType.ADVANCEMENT;
		}
	}

	record Statistic(Key statisticType, Key statisticSpecification, StatisticCategory category) implements TaskId {
		@Override
		public TaskType type() {
			return TaskType.STATISTIC;
		}
	}

	TaskId DUMMY = new Item(Key.key("bingoreloadedcompanion:dummyid"));

	ByteCodec<TaskId> CODEC = ByteCodec.create((buf, id) -> {
		TaskType.CODEC.encode(buf, id.type());
		switch (id.type()) {
			case TaskType.ITEM -> {
				TaskId.Item item = (TaskId.Item)id;
				ByteCodec.KEY.encode(buf, item.id());
			}
			case TaskType.ADVANCEMENT -> {
				TaskId.Advancement advancement = (TaskId.Advancement) id;
				ByteCodec.KEY.encode(buf, advancement.id());
			}
			case TaskType.STATISTIC -> {
				TaskId.Statistic statistic = (TaskId.Statistic) id;
				ByteCodec.KEY.encode(buf, statistic.statisticType());
				ByteCodec.KEY.encode(buf, statistic.statisticSpecification());
				StatisticCategory.CODEC.encode(buf, statistic.category());
			}
		}
	}, (buf) -> {
		TaskType type = TaskType.CODEC.decode(buf);
		return switch (type) {
			case TaskType.ITEM -> new TaskId.Item(ByteCodec.KEY.decode(buf));
			case TaskType.ADVANCEMENT -> new TaskId.Advancement(ByteCodec.KEY.decode(buf));
			case TaskType.STATISTIC -> new TaskId.Statistic(ByteCodec.KEY.decode(buf), ByteCodec.KEY.decode(buf), StatisticCategory.CODEC.decode(buf));
		};
	});
}
