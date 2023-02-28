package io.github.steaf23.bingoreloaded.core.data;

public enum BingoStatType
{
    PLAYED(-1),
    WINS(0),
    LOSSES(1),
    TASKS(2),
    RECORD_TASKS(3),
    WAND_USES(4),
    ;

    public final int idx;

    BingoStatType(int idx)
    {
        this.idx = idx;
    }
}
