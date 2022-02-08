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
    public boolean checkBingo()
    {
        //check for rows and columns
        for (int y = 0; y < CARD_SIZE; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < CARD_SIZE; x++)
            {
                int indexRow = CARD_SIZE * y + x;
                if (items.get(indexRow).isCompleted())
                {
                    completedRow = false;
                }

                int indexCol = CARD_SIZE * x + y;
                if (items.get(indexCol).isCompleted())
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
        boolean completedDiagonal = true;
        for (int idx = 0; idx < Math.pow(CARD_SIZE, 2); idx += CARD_SIZE + 1)
        {
            if (items.get(idx).isCompleted())
            {
                completedDiagonal = false;
            }
        }

        for (int idx = 0; idx < Math.pow(CARD_SIZE, 2); idx += CARD_SIZE - 1)
        {
            if (idx != 0 && idx != Math.pow(CARD_SIZE, 2) - 1)
            {
                if (items.get(idx).isCompleted())
                {
                    completedDiagonal = false;
                }
            }
        }

        return completedDiagonal;
    }
}
