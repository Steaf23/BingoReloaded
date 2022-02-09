package me.steven.bingoreloaded.cards;


public class RegularBingoCard extends BingoCard
{
    /*
        00, 01, 02, 03, 04,
        05, 06, 07, 08, 09,
        10, 11, 12, 13, 14,
        15, 16, 17, 18, 19,
        20, 21, 22, 23, 24,
     */
    @Override
    public boolean hasBingo()
    {
        //check for rows and columns
        for (int y = 0; y < size.cardSize; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.cardSize; x++)
            {
                int indexRow = size.cardSize * y + x;
                if (!items.get(indexRow).isCompleted())
                {
                    completedRow = false;
                }

                int indexCol = size.cardSize * x + y;
                if (!items.get(indexCol).isCompleted())
                {
                    completedCol = false;
                }
            }

            if (completedRow || completedCol)
            {
                return true;
            }
        }

        // check for diagonals
        boolean completedDiagonal1 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize + 1)
        {
            if (!items.get(idx).isCompleted())
            {
                completedDiagonal1 = false;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize - 1)
        {
            if (idx != 0 && idx != size.fullCardSize - 1)
            {
                if (!items.get(idx).isCompleted())
                {
                    completedDiagonal2 = false;
                }
            }
        }

        return completedDiagonal1 || completedDiagonal2;
    }
}
