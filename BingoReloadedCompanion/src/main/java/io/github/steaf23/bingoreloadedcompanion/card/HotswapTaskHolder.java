package io.github.steaf23.bingoreloadedcompanion.card;

public record HotswapTaskHolder(long totalTimeSeconds, long currentTimeSeconds, boolean recovering, boolean expires) {

}
