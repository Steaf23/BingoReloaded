package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.gameloop.BingoGameManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeamTeleportTabCompleter implements TabCompleter {

    private final BingoGameManager gameManager;

    public TeamTeleportTabCompleter(BingoGameManager gameManager)
    {
        this.gameManager = gameManager;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player p) {
            BingoSession session = gameManager.getSession(p);
            if (session == null)
                return null;

            TeamManager teamManager = session.teamManager;

            BingoParticipant player = teamManager.getBingoParticipant(p);
            if (player == null) {
                return null;
            }
            BingoTeam team = player.getTeam();
            if (team == null) {
                return null;
            }
            if (args.length == 1) {
                List<String> teammates = new ArrayList<>(team.team.getEntries());
                teammates.remove(p.getUniqueId().toString());
                return teammates;
            }
            return null;
        }
        return null;
    }
}
