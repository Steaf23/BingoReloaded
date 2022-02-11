package me.steven.bingoreloaded.GUIInventories.cards;

public enum CardDifficulty
{
    EASY("easy"),
    NORMAL("medium"),
    HARD("hard"),
    ;

    public String itemDifficulty;

    CardDifficulty(String itemDifficulty)
    {
        this.itemDifficulty = itemDifficulty;
    }
}
