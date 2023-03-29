package io.github.steaf23.bingoreloaded.core.command;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoGameManager;
import io.github.steaf23.bingoreloaded.core.BingoSession;
import io.github.steaf23.bingoreloaded.core.data.BingoStatsData;
import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.core.player.PlayerKit;
import io.github.steaf23.bingoreloaded.gui.BingoMenu;
import io.github.steaf23.bingoreloaded.gui.creator.BingoCreatorUI;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

public class BingoCommand implements CommandExecutor
{
    private final BingoGameManager manager;

    public BingoCommand(BingoGameManager manager)
    {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (!(commandSender instanceof Player player) || !player.hasPermission("bingo.player"))
        {
            return false;
        }

        String worldName = BingoGameManager.getWorldName(player.getWorld());
        BingoSession session = manager.getSession(worldName);
        if (session == null)
            return false;

        if (args.length == 0)
        {
            BingoMenu.openOptions(player, session);
            return true;
        }

        switch (args[0])
        {
            case "join":
                session.teamManager.openTeamSelector(player, null);
                break;
            case "leave":
                if (session.isRunning())
                {
                    BingoPlayer participant = session.teamManager.getBingoPlayer(player);
                    if (participant != null)
                        session.removePlayer(participant);
                }
                break;

            case "start":
                if (player.hasPermission("bingo.settings"))
                {
                    if (args.length > 1)
                    {
                        int seed = Integer.parseInt(args[1]);
                        session.settings.cardSeed = seed;
                    }

                    manager.startGame(worldName);
                    return true;
                }
                break;


            case "end":
                if (player.hasPermission("bingo.settings"))
                    manager.endGame(worldName);
                break;


            case "getcard":
                if (session.isRunning())
                {
                    BingoPlayer participant = session.teamManager.getBingoPlayer(player);
                    if (participant != null)
                        session.game().returnCardToPlayer(participant);
                    return true;
                }
                break;

            case "back":
                if (session.isRunning())
                {
                    if (BingoReloaded.get().config().teleportAfterDeath)
                    {
                        session.game().teleportPlayerAfterDeath(player);
                        return true;
                    }
                }
                break;

            case "deathmatch":
                if (!player.hasPermission("bingo.settings"))
                {
                    return false;
                }
                else if (!session.isRunning())
                {
                    new TranslatedMessage("command.bingo.no_deathmatch").color(ChatColor.RED).send(player);
                    return false;
                }

                session.game().startDeathMatch(3);
                return true;

            case "creator":
                if (player.hasPermission("bingo.manager"))
                {
                    BingoCreatorUI creatorUI = new BingoCreatorUI(null);
                    creatorUI.open(player);
                }
                break;

            case "stats":
//                Hologram holo = BingoReloaded.holograms().create("scoreboard", player.getLocation(), "LINE UNO", Message.PREFIX_STRING, "LINOS " + ChatColor.BOLD + "DOS is very long I can not see it because it goes into the sun lol");
//
//                String path = "logo.png";
//                try
//                {
//                    Hologram holo2 = BingoReloaded.holograms().createImage("logo", player.getLocation(), path, ChatColor.WHITE);
//                } catch (IOException e)
//                {
//                    Message.log("NO IMAGE AT " + path);
//                    throw new RuntimeException(e);
//                }

                if (!BingoReloaded.get().config().savePlayerStatistics)
                {
                    TextComponent text = new TextComponent("Player statistics are not being tracked at this moment!");
                    text.setColor(ChatColor.RED);
                    Message.sendDebug(text, player);
                    return true;
                }
                BingoStatsData statsData = new BingoStatsData();
                Message msg;
                if (args.length > 1 && player.hasPermission("bingo.admin"))
                {
                    msg = statsData.getPlayerStatsFormatted(args[1]);
                }
                else
                {
                    msg = statsData.getPlayerStatsFormatted(player.getUniqueId());
                }
                msg.send(player);
                return true;

            case "destroy":
                if (args.length > 1 && player.hasPermission("bingo.admin"))
                {
                    BingoReloaded.get().holograms().destroy(args[1]);
                }
                break;

            case "kit":
                if (args.length <= 2)
                    return false;

                switch (args[1])
                {
                    case "item":
                        givePlayerBingoItem(player, args[2]);
                        break;

                    case "add":
                        if (args.length < 4)
                        {
                            Message.sendDebug(ChatColor.RED + "Please specify a kit name for slot " + args[2], player);
                            return false;
                        }
                        addPlayerKit(args[2], Arrays.stream(args).toList().subList(3, args.length), player);
                        break;

                    case "remove":
                        removePlayerKit(args[2], player);
                        break;
                }
                break;

            default:
                new TranslatedMessage("command.use").color(ChatColor.RED).arg("/bingo [getcard | stats | start | end | join | back | leave | deathmatch | creator]").send(player);
                break;
        }

        return false;
    }

    public void addPlayerKit(String slot, List<String> kitNameParts, Player commandSender)
    {
        PlayerKit kit = switch (slot)
        {
            case "1" -> PlayerKit.CUSTOM_1;
            case "2" -> PlayerKit.CUSTOM_2;
            case "3" -> PlayerKit.CUSTOM_3;
            case "4" -> PlayerKit.CUSTOM_4;
            case "5" -> PlayerKit.CUSTOM_5;
            default -> {
                Message.sendDebug(ChatColor.RED + "Invalid slot, please a slot from 1 through 5 to save this kit in", commandSender);
                yield null;
            }
        };

        String kitName = "";
        for (int i = 0; i < kitNameParts.size() - 1; i++)
        {
            kitName += kitNameParts.get(i) + " ";
        }
        kitName += kitNameParts.get(kitNameParts.size() - 1);

        if (!PlayerKit.assignCustomKit(kitName, kit, commandSender))
        {
            BaseComponent msg = new TextComponent("");
            msg.setColor(ChatColor.RED);
            msg.addExtra("Cannot add custom kit ");
            msg.addExtra(TranslationData.convertColors(kitName));
            msg.addExtra(" to slot " + slot + ", this slot already contains kit ");
            msg.addExtra(PlayerKit.getCustomKit(kit).getName());
            msg.addExtra(". Remove it first!");
            Message.sendDebug(msg, commandSender);
        }
        else
        {
            BaseComponent msg = new TextComponent("");
            msg.setColor(ChatColor.GREEN);
            msg.addExtra("Created custom kit ");
            msg.addExtra(TranslationData.convertColors(kitName));
            msg.addExtra(" in slot " + slot + " from your inventory");
            Message.sendDebug(msg, commandSender);
        }
    }

    public void removePlayerKit(String slot, Player commandSender)
    {
        PlayerKit kit = switch (slot)
        {
            case "1" -> PlayerKit.CUSTOM_1;
            case "2" -> PlayerKit.CUSTOM_2;
            case "3" -> PlayerKit.CUSTOM_3;
            case "4" -> PlayerKit.CUSTOM_4;
            case "5" -> PlayerKit.CUSTOM_5;
            default -> {
                Message.sendDebug(ChatColor.RED + "Invalid slot, please a slot from 1 through 5 to save this kit in", commandSender);
                yield null;
            }
        };

        if (PlayerKit.getCustomKit(kit) == null)
        {
            BaseComponent msg = new TextComponent("");
            msg.setColor(ChatColor.RED);
            msg.addExtra("Cannot remove kit from slot " + slot + " because no custom kit is assigned to this slot");
            Message.sendDebug(msg, commandSender);
        }
        else
        {
            BaseComponent msg = new TextComponent("");
            msg.setColor(ChatColor.GREEN);
            msg.addExtra("Removed custom kit ");
            msg.addExtra(PlayerKit.getCustomKit(kit).getName());
            msg.addExtra(" from slot " + slot);
            Message.sendDebug(msg, commandSender);
            PlayerKit.removeCustomKit(kit);
        }
    }

    public void givePlayerBingoItem(Player player, String itemName)
    {
        if (itemName.equals("wand"))
        {
            player.getInventory().addItem(PlayerKit.wandItem);
        }
    }

    /**
     * @param in
     * @param defaultValue
     * @return Integer the string represents or defaultValue if a conversion failed.
     */
    private int toInt(String in, int defaultValue)
    {
        try
        {
            return Integer.parseInt(in);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }
}
