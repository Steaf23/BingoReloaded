package io.github.steaf23.bingoreloaded.command;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.inventory.item.action.ComboBoxButtonAction;
import io.github.steaf23.playerdisplay.inventory.item.action.MenuAction;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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
            case "texture_menu" -> {
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
            case "menu" -> {
                if (!(commandSender instanceof Player p)) {
                    return false;
                }

                BasicMenu menu = new BasicMenu(board, Component.text("Some test menu..."), 6);
                ItemTemplate cardSizeItem = new ComboBoxButtonAction.Builder("one",
                        new ItemTemplate(Material.WHITE_CONCRETE, Component.text(1)))
                        .addOption("two",
                                new ItemTemplate(Material.YELLOW_CONCRETE, Component.text(2)))
                        .addOption("three",
                                new ItemTemplate(Material.ORANGE_CONCRETE, Component.text(3)))
                        .addOption("four",
                                new ItemTemplate(Material.RED_CONCRETE, Component.text(4)))
                        .addOption("five",
                                new ItemTemplate(Material.PURPLE_CONCRETE, Component.text(5)))
                        .addOption("six",
                                new ItemTemplate(Material.BLUE_CONCRETE, Component.text(6)))
                        .buildItem(12, "three");
                menu.addItem(cardSizeItem);
                menu.addAction(new ItemTemplate(11, Material.GOLDEN_APPLE, Component.text("Vibe Check")), a -> {
                    ConsoleMessenger.warn("Vibe incoming");
                    MenuAction action = cardSizeItem.getAction();
                    if (action instanceof ComboBoxButtonAction comboAction) {
                        ConsoleMessenger.error("VIBE CHECKED: " + comboAction.getSelectedOptionName());
                    }
                });
                menu.open(p);
            }
            case "participants" -> {
                commandSender.sendMessage("---");
                BingoReloaded.getInstance().getGameManager().getSession("world").participantMap.forEach((key,value) -> {
                    commandSender.sendMessage(String.format("%s: %s", key.toString(), value.getName()));
                });
            }
            case "teams" -> {
                BingoSession session = BingoReloaded.getInstance().getGameManager().getSession("world");
                commandSender.sendMessage("-- Active Teams --");
                session.teamManager.getActiveTeams().getTeams().forEach(team -> {
                    commandSender.sendMessage(team.getColoredName());
                    team.getMembers().forEach(member -> {
                        commandSender.sendMessage(String.format(" ┗ %s", member.getName()));
                    });
                });
                commandSender.sendMessage("-- Joinable Teams --");
                session.teamManager.getJoinableTeams().forEach((name, template) -> {
                    commandSender.sendMessage(template.nameComponent().color(template.color()));
                });
            }
            case "remove-team" -> {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(Team::unregister);
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

        Optional<TaskCard> card = player.getCard();

        if (card.isEmpty() || taskIndex >= card.get().getTasks().size()) {
            ConsoleMessenger.error("index out of bounds for task list!");
            return;
        }

        GameTask task = card.get().getTasks().get(taskIndex);
        task.complete(player, ((BingoGame) player.getSession().phase()).getGameTime());
        var slotEvent = new BingoTaskProgressCompletedEvent(player.getSession(), task);
        Bukkit.getPluginManager().callEvent(slotEvent);
    }
}
