package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import io.github.steaf23.bingoreloadedcompanion.network.PayloadHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public sealed interface TaskId {

	StreamCodec<ByteBuf, TaskId> STREAM_CODEC = StreamCodec.of((buf, id) -> {
		TaskType.STREAM_CODEC.encode(buf, id.type());
		switch (id.type()) {
			case ITEM -> {
				Item item = (Item)id;
				PayloadHelper.ID_CODEC.encode(buf, item.id());
			}
			case ADVANCEMENT -> {
				Advancement advancement = (Advancement) id;
				PayloadHelper.ID_CODEC.encode(buf, advancement.id());
			}
			case STATISTIC -> {
				Statistic statistic = (Statistic) id;
				PayloadHelper.ID_CODEC.encode(buf, statistic.statisticType());
				PayloadHelper.ID_CODEC.encode(buf, statistic.statisticSpecification());
			}
		}
	}, (buf) -> {
		TaskType type = TaskType.STREAM_CODEC.decode(buf);
		return switch (type) {
			case ITEM -> new Item(PayloadHelper.ID_CODEC.decode(buf));
			case ADVANCEMENT -> new Advancement(PayloadHelper.ID_CODEC.decode(buf));
			case STATISTIC -> new Statistic(PayloadHelper.ID_CODEC.decode(buf), PayloadHelper.ID_CODEC.decode(buf));
		};
	});

	TaskType type();

	record Item(Identifier id) implements TaskId {
		@Override
		public TaskType type() {
			return TaskType.ITEM;
		}
	}

	record Advancement(Identifier id) implements TaskId {
		@Override
		public TaskType type() {
			return TaskType.ADVANCEMENT;
		}
	}

	record Statistic(Identifier statisticType, Identifier statisticSpecification) implements TaskId {
		@Override
		public TaskType type() {
			return TaskType.STATISTIC;
		}
	}

	TaskId DUMMY = new Item(Identifier.parse("bingoreloadedcompanion:dummy_id"));
}
