package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gameloop.PregameLobby;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.ActionMenu;
import io.github.steaf23.bingoreloaded.gui.base.TreeMenu;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.function.BiConsumer;

public class VoteMenu extends TreeMenu
{
    private ActionMenu gamemodeOptions;
    private ActionMenu kitOptions;
    private ActionMenu cardOptions;
    private final PregameLobby lobby;

    public VoteMenu(ConfigData.VoteList voteList, MenuInventory parent, PregameLobby lobbyPhase)
    {
        super(27, "Vote", parent);

        this.lobby = lobbyPhase;

        if (voteList.gamemodes.size() != 0)
        {
            gamemodeOptions = new ActionMenu(9, "Vote for Gamemode", this);

            int itemIndex = 0;
            if (voteList.gamemodes.contains("regular_5"))
            {
                gamemodeOptions.addAction(new MenuItem(itemIndex, Material.LIME_CONCRETE,
                        ChatColor.BOLD + BingoGamemode.REGULAR.name + " - 5x5",
                        BingoTranslation.INFO_REGULAR_DESC.translate().split("\\n")), (player) -> {
                    lobby.voteGamemode("regular_5", player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes.contains("regular_3"))
            {
                gamemodeOptions.addAction(new MenuItem(itemIndex, Material.GREEN_CONCRETE,
                        ChatColor.BOLD + BingoGamemode.REGULAR.name + " - 3x3",
                        BingoTranslation.INFO_REGULAR_DESC.translate().split("\\n")), (player) -> {
                    lobby.voteGamemode("regular_3", player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes.contains("lockout_5"))
            {
                gamemodeOptions.addAction(new MenuItem(itemIndex, Material.PINK_CONCRETE,
                        ChatColor.BOLD + BingoGamemode.LOCKOUT.name + " - 5x5",
                        BingoTranslation.INFO_LOCKOUT_DESC.translate().split("\\n")), (player) -> {
                    lobby.voteGamemode("lockout_5", player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes.contains("lockout_3"))
            {
                gamemodeOptions.addAction(new MenuItem(itemIndex, Material.PURPLE_CONCRETE,
                        ChatColor.BOLD + BingoGamemode.LOCKOUT.name + " - 3x3",
                        BingoTranslation.INFO_LOCKOUT_DESC.translate().split("\\n")), (player) -> {
                    lobby.voteGamemode("lockout_3", player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes.contains("complete_5"))
            {
                gamemodeOptions.addAction(new MenuItem(itemIndex, Material.LIGHT_BLUE_CONCRETE,
                        ChatColor.BOLD + BingoGamemode.COMPLETE.name + " - 5x5",
                        BingoTranslation.INFO_COMPLETE_DESC.translate().split("\\n")), (player) -> {
                    lobby.voteGamemode("complete_5", player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes.contains("complete_3"))
            {
                gamemodeOptions.addAction(new MenuItem(itemIndex, Material.BLUE_CONCRETE,
                        ChatColor.BOLD + BingoGamemode.COMPLETE.name + " - 3x3",
                        BingoTranslation.INFO_COMPLETE_DESC.translate().split("\\n")), (player) -> {
                    lobby.voteGamemode("complete_3", player);
                });
                itemIndex++;
            }
            gamemodeOptions.addCloseAction(8);
            addMenuAction(new MenuItem(2, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Gamemode"), gamemodeOptions);
        }

        if (voteList.kits.size() != 0)
        {
            kitOptions = new ActionMenu(9, "Vote for Kit", this);

            int itemIndex = 0;
            if (voteList.kits.contains("hardcore"))
            {
                kitOptions.addAction(new MenuItem(itemIndex, Material.RED_DYE,
                        PlayerKit.HARDCORE.displayName,
                        BingoTranslation.KIT_HARDCORE_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
                    lobby.voteKit(PlayerKit.HARDCORE.configName, player);
                });
                itemIndex++;
            }
            if (voteList.kits.contains("normal"))
            {
                kitOptions.addAction(new MenuItem(itemIndex, Material.YELLOW_DYE,
                        PlayerKit.NORMAL.displayName,
                        BingoTranslation.KIT_NORMAL_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
                    lobby.voteKit(PlayerKit.NORMAL.configName, player);
                });
                itemIndex++;
            }
            if (voteList.kits.contains("overpowered"))
            {
                kitOptions.addAction(new MenuItem(itemIndex, Material.PURPLE_DYE,
                        PlayerKit.OVERPOWERED.displayName,
                        BingoTranslation.KIT_OVERPOWERED_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
                    lobby.voteKit(PlayerKit.OVERPOWERED.configName, player);
                });
                itemIndex++;
            }
            if (voteList.kits.contains("reloaded"))
            {
                kitOptions.addAction(new MenuItem(itemIndex, Material.CYAN_DYE,
                        PlayerKit.RELOADED.displayName,
                        BingoTranslation.KIT_RELOADED_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
                    lobby.voteKit(PlayerKit.RELOADED.configName, player);
                });
                itemIndex++;
            }

            BiConsumer<PlayerKit, Integer> addCustomKit = (kit, slot) -> {
                kitOptions.addAction(new MenuItem(slot, Material.GRAY_DYE,
                        kit.displayName).setGlowing(true), (player) -> {
                    lobby.voteKit(kit.configName, player);
                });
            };

            if (voteList.kits.contains("custom_1") && PlayerKit.getCustomKit(PlayerKit.CUSTOM_1) != null)
            {
                addCustomKit.accept(PlayerKit.CUSTOM_1, itemIndex);
                itemIndex++;
            }
            if (voteList.kits.contains("custom_2") && PlayerKit.getCustomKit(PlayerKit.CUSTOM_2) != null)
            {
                addCustomKit.accept(PlayerKit.CUSTOM_2, itemIndex);
                itemIndex++;
            }
            if (voteList.kits.contains("custom_3") && PlayerKit.getCustomKit(PlayerKit.CUSTOM_3) != null)
            {
                addCustomKit.accept(PlayerKit.CUSTOM_3, itemIndex);
                itemIndex++;
            }
            if (voteList.kits.contains("custom_4") && PlayerKit.getCustomKit(PlayerKit.CUSTOM_4) != null)
            {
                addCustomKit.accept(PlayerKit.CUSTOM_4, itemIndex);
                itemIndex++;
            }
            if (voteList.kits.contains("custom_5") && PlayerKit.getCustomKit(PlayerKit.CUSTOM_5) != null)
            {
                addCustomKit.accept(PlayerKit.CUSTOM_5, itemIndex);
                itemIndex++;
            }

            if (itemIndex < 8)
                kitOptions.addCloseAction(8);

            addMenuAction(new MenuItem(4, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Kit"), kitOptions);
        }

        if (voteList.cards.size() != 0)
        {
            cardOptions = new ActionMenu(9, "Vote for Card", this);

            int itemIndex = 0;
            for (String card : voteList.cards) {
                String displayName = ChatColor.BOLD + card;
                Material material = Material.PAPER;
                MenuItem menuItem = new MenuItem(itemIndex, material, displayName);

                // Add the menu item to the cardOptions menu
                cardOptions.addAction(menuItem, (player) -> {
                    lobby.voteCard(card, player);
                });

                itemIndex++;
            }

            cardOptions.addCloseAction(8);
            addMenuAction(new MenuItem(6, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Card"), cardOptions);
        }

        addCloseAction(MenuItem.slotFromXY(0, 2));
    }
}
