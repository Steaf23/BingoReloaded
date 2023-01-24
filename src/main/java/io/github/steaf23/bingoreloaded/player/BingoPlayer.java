package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoGame;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public record BingoPlayer(OfflinePlayer player, BingoTeam team){}
