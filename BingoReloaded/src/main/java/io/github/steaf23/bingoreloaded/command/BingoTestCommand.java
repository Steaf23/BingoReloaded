package io.github.steaf23.bingoreloaded.command;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BingoTestCommand implements TabExecutor
{
    private final BingoReloaded plugin;
    private final MenuBoard board;

    public BingoTestCommand(BingoReloaded plugin, MenuBoard board) {
        this.plugin = plugin;
        this.board = board;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
            return false;
        }

        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "complete" -> {
                if (args.length < 3) {
                    break;
                }

                Player player = Bukkit.getPlayer(args[1]);
                if (player == null)
                    return false;

                // Grossly beautiful line of code...
                BingoParticipant virtualPlayer = BingoReloaded.getInstance().getGameManager().getSession("world").teamManager.getPlayerAsParticipant(player);
                int taskIndex = Integer.parseInt(args[2]);
                if (virtualPlayer == null) {
                    ConsoleMessenger.error("Cannot complete task " + args[2] + " for non existing player: " + args[1]);
                    break;
                }
                completeTaskByPlayer(virtualPlayer, taskIndex);
            }
            case "menu" -> {
                if (!(commandSender instanceof Player p)) {
                    return false;
                }

                TexturedMenuData.Texture test = BingoReloaded.getInstance().getTextureData().getTexture("test");
                TextComponent.Builder title = Component.text().color(NamedTextColor.WHITE);
                if (test == null) {
                    return false;
                }

                title.append(Component.translatable("space." + (test.menuOffset())));

                var inv = Bukkit.createInventory(null, 9*6, title.append(Component.text("\uE030").append(Component.translatable("space.-" + (test.textureEnd() + 2)))).append(Component.text("\uE030")).build());
                p.openInventory(inv);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    void completeTaskByPlayer(BingoParticipant player, int taskIndex) {
        if (!player.getSession().isRunning())
            return;

        TaskCard card = player.getTeam().getCard();

        if (card == null || taskIndex >= card.getTasks().size()) {
            ConsoleMessenger.log(Component.text("index out of bounds for task list!").color(NamedTextColor.RED));
            return;
        }

        GameTask task = card.getTasks().get(taskIndex);
        task.complete(player, ((BingoGame) player.getSession().phase()).getGameTime());
        var slotEvent = new BingoTaskProgressCompletedEvent(player.getSession(), task);
        Bukkit.getPluginManager().callEvent(slotEvent);
    }
}
