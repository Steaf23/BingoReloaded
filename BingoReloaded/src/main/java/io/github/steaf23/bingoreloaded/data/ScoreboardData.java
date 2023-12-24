package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.InfoScoreboard;

import java.util.List;

public class ScoreboardData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("scoreboards.yml");

    public InfoScoreboard toInfoBoard(String name, InfoScoreboard board) {

        String boardData = data.getConfig().getString("name", "");
        String[] lines = boardData.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            board.setLineText(i, lines[i]);
        }

        return board;
    }

}
