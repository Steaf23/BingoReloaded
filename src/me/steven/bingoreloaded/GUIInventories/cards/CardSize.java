package me.steven.bingoreloaded.GUIInventories.cards;

public enum CardSize
{
    X1(1, 4),
    X2(2, 4),
    X3(3, 3),
    X4(4, 3),
    X5(5, 2),
    X6(6, 2),
    ;

    public int cardSize;
    public int leftSpacing;
    public int rightSpacing;
    public int fullCardSize;
    CardSize(int size, int leftSpacing)
    {
        this.cardSize = size;
        this.leftSpacing = leftSpacing;
        this.rightSpacing = 9 - size - leftSpacing;
        this.fullCardSize = (int)Math.pow(size, 2);
    }

    public int getCardInventorySlot(int itemIndex)
    {
        int row;
        if (itemIndex == Math.pow(cardSize, 2) - 1)
        {
            row = cardSize - 1;
        }
        else
        {
            row = (int) Math.floor(itemIndex / (double)cardSize);
        }

        return itemIndex + leftSpacing + row * (leftSpacing + rightSpacing);
    }
}
