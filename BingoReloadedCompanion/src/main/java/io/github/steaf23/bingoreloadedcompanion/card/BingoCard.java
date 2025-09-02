package io.github.steaf23.bingoreloadedcompanion.card;

import java.util.List;

public record BingoCard(BingoGamemode mode, int size, List<Task> tasks) {
}
