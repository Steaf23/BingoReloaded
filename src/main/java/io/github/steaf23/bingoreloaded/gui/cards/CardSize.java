package io.github.steaf23.bingoreloaded.gui.cards;

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
        if (itemIndex == fullCardSize - 1) //set correct row for last item
        {
            row = cardSize - 1;
        }
        else //set correct row for other items
        {
            row = (int) Math.floor(itemIndex / (double)cardSize);
        }

        return itemIndex + leftSpacing + row * (leftSpacing + rightSpacing);
    }

    public static CardSize fromWidth(int width)
    {
        for (CardSize size : CardSize.values())
        {
            if (size.cardSize == width)
            {
                return size;
            }
        }

        return CardSize.X5;
    }
}
