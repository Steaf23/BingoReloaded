package io.github.steaf23.bingoreloaded.core.cards;

import java.util.HashSet;
import java.util.Set;

public enum CardSize
{
    X1(1, 4),
    X2(2, 4),
    X3(3, 3),
    X4(4, 3),
    X5(5, 2),
    X6(6, 2),
    ;

    public int size;
    public int leftSpacing;
    public int rightSpacing;
    public int fullCardSize;

    public final Set<Integer> taskSlots;

    CardSize(int size, int leftSpacing)
    {
        this.size = size;
        this.leftSpacing = leftSpacing;
        this.rightSpacing = 9 - size - leftSpacing;
        this.fullCardSize = (int)Math.pow(size, 2);

        this.taskSlots = new HashSet<>();
        for (int i = 0; i < fullCardSize; i++)
        {
            taskSlots.add(getCardInventorySlot(i));
        }
    }

    public int getCardInventorySlot(int itemIndex)
    {
        int row;
        if (itemIndex == fullCardSize - 1) //set correct row for last item
        {
            row = size - 1;
        }
        else //set correct row for other items
        {
            row = (int) Math.floor(itemIndex / (double)size);
        }

        return itemIndex + leftSpacing + row * (leftSpacing + rightSpacing);
    }

    public static CardSize fromWidth(int width)
    {
        for (CardSize size : CardSize.values())
        {
            if (size.size == width)
            {
                return size;
            }
        }

        return CardSize.X5;
    }
}
