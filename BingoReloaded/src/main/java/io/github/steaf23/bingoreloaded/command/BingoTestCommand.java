package io.github.steaf23.bingoreloaded.command;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import io.github.steaf23.easymenulib.menu.item.action.ComboBoxButtonAction;
import io.github.steaf23.easymenulib.menu.item.action.SpinBoxButtonAction;
import io.github.steaf23.easymenulib.menu.item.action.ToggleButtonAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
                    Message.error("Cannot complete task " + args[2] + " for non existing player: " + args[1]);
                    break;
                }
                completeTaskByPlayer(virtualPlayer, taskIndex);
            }
            case "menu" -> {
                Player player = commandSender instanceof Player p ? p : null;
                if (player == null)
                    return false;

                BasicMenu menu = new BasicMenu(board, "Easy Menu Lib Test", 6);

                menu.addItem(new MenuItem(0, 0, Material.BEDROCK, "" + ChatColor.RED + ChatColor.UNDERLINE + "Some name??"));

                menu.addAction(new MenuItem(1, 0, Material.ACACIA_BOAT, "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "Toggle Action", "Toggle me :D"),
                        new ToggleButtonAction(toggled -> {
                            Message.sendDebug("I am toggled and " + toggled, player);
                        }));

                MenuItem spinboxItem = new MenuItem(2, 0, Material.LEAD, "Spinbox action", "I have value >:)");
                spinboxItem.setAction(new SpinBoxButtonAction(13, 34, 2, value -> {
                    Message.sendDebug("I have a value of " + value, player);
                    spinboxItem.setLore("I have a value of " + value + "!");
                }));
                menu.addItem(spinboxItem);

                menu.addAction(new MenuItem(3, 0, Material.SAND, "I should not exist..."), arguments -> {
                    Message.sendDebug("but yet I am alive ;(", player);
                });

                MenuItem comboItem = new MenuItem(3, 0, Material.YELLOW_CONCRETE, "I am actually 4 items", "fr fr");
                menu.addAction(comboItem, new ComboBoxButtonAction(selection -> {
                    Message.sendDebug("You have selected " + selection, player);
                })
                        .addOption("Cheese", new ItemStack(Material.YELLOW_CONCRETE))
                        .addOption("Potato", new ItemStack(Material.POISONOUS_POTATO))
                        .addOption("Tomato", new ItemStack(Material.RED_CONCRETE))
                        .addOption("Carrotato", new ItemStack(Material.ORANGE_CONCRETE))
                        .selectOption("Tomato"));

                menu.open(player);
                Message.log("Opened menu");
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

        BingoCard card = player.getTeam().card;

        if (taskIndex >= card.tasks.size()) {
            Message.log(ChatColor.RED + "index out of bounds for task list!");
            return;
        }

        card.tasks.get(taskIndex).complete(player, ((BingoGame) player.getSession().phase()).getGameTime());
        var slotEvent = new BingoCardTaskCompleteEvent(card.tasks.get(taskIndex), player, card.hasBingo(player.getTeam()));
        Bukkit.getPluginManager().callEvent(slotEvent);
    }
}
