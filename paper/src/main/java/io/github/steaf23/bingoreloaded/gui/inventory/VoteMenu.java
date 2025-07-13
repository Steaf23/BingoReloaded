package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.CollectionHelper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

import java.util.function.BiConsumer;

public class VoteMenu extends BasicMenu
{
    private final PregameLobby lobby;

    private static final int[][] categoryPositions = new int[][] {{4}, {3, 5}, {2, 4, 6}, {1, 3, 5, 7}};

    private static final ItemTemplate EXIT = new ItemTemplate(8, Material.BARRIER,
            BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    public VoteMenu(MenuBoard menuBoard, BingoConfigurationData.VoteList voteList, PregameLobby lobbyPhase) {
        super(menuBoard, BingoMessage.OPTIONS_VOTE.asPhrase(), 3);

        this.lobby = lobbyPhase;

        // Determine the position of the vote items in the GUI based on the amount of categories available to vote.
        int categoryMax = 0, categoryIndex = 0;
        if (!voteList.cards().isEmpty()) categoryMax++;
        if (!voteList.gamemodes().isEmpty()) categoryMax++;
        if (!voteList.kits().isEmpty()) categoryMax++;
        if (!voteList.cardSizes().isEmpty()) categoryMax++;

        if (categoryMax == 0) {
            return;
        }

        int[] positions = categoryPositions[categoryMax-1];

        if (!voteList.gamemodes().isEmpty()) {
            BasicMenu gamemodeOptions = new BasicMenu(menuBoard, BingoMessage.VOTE_GAMEMODE.asPhrase(), 1);

            int itemIndex = 0;
            if (voteList.gamemodes().contains("regular")) {
                gamemodeOptions.addAction(new ItemTemplate(itemIndex, Material.LIME_CONCRETE,
                        BingoGamemode.REGULAR.asComponent().decorate(TextDecoration.BOLD),
                        BingoMessage.INFO_REGULAR_DESC.asMultiline()), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteGamemode("regular", player);
                    gamemodeOptions.close(player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes().contains("lockout")) {
                gamemodeOptions.addAction(new ItemTemplate(itemIndex, Material.PINK_CONCRETE,
                        BingoGamemode.LOCKOUT.asComponent().decorate(TextDecoration.BOLD),
                        BingoMessage.INFO_LOCKOUT_DESC.asMultiline()), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteGamemode("lockout", player);
                    gamemodeOptions.close(player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes().contains("complete")) {
                gamemodeOptions.addAction(new ItemTemplate(itemIndex, Material.LIGHT_BLUE_CONCRETE,
                        BingoGamemode.COMPLETE.asComponent().decorate(TextDecoration.BOLD),
                        BingoMessage.INFO_COMPLETE_DESC.asMultiline()), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteGamemode("complete", player);
                    gamemodeOptions.close(player);
                });
                itemIndex++;
            }
            if (voteList.gamemodes().contains("hotswap")) {
                gamemodeOptions.addAction(new ItemTemplate(itemIndex, Material.YELLOW_CONCRETE,
                        BingoGamemode.HOTSWAP.asComponent().decorate(TextDecoration.BOLD),
                        CollectionHelper.concatWithArrayCopy(
                                BingoMessage.INFO_HOTSWAP_DESC_EXPIRE.asMultiline(),
                                BingoMessage.INFO_HOTSWAP_DESC_ANY.asMultiline(Component.text("x")))), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteGamemode("hotswap", player);
                    gamemodeOptions.close(player);
                });
                itemIndex++;
            }
            gamemodeOptions.addCloseAction(EXIT.copy());

            addAction(new ItemTemplate(positions[categoryIndex], 1, Material.ENCHANTED_BOOK, BasicMenu.applyTitleFormat(BingoMessage.VOTE_GAMEMODE.asPhrase())),
                    args -> gamemodeOptions.open(args.player()));
            categoryIndex++;
        }

        if (!voteList.kits().isEmpty()) {
            BasicMenu kitOptions = new BasicMenu(menuBoard, BingoMessage.VOTE_KIT.asPhrase(), 1);

            int itemIndex = 0;
            if (voteList.kits().contains("hardcore")) {
                kitOptions.addAction(new ItemTemplate(itemIndex, Material.RED_DYE,
                        PlayerKit.HARDCORE.getDisplayName(),
                        BingoMessage.KIT_HARDCORE_DESC.asMultiline()).setGlowing(true), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteKit(PlayerKit.HARDCORE.configName, player);
                    kitOptions.close(player);
                });
                itemIndex++;
            }
            if (voteList.kits().contains("normal")) {
                kitOptions.addAction(new ItemTemplate(itemIndex, Material.YELLOW_DYE,
                        PlayerKit.NORMAL.getDisplayName(),
                        BingoMessage.KIT_NORMAL_DESC.asMultiline()).setGlowing(true), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteKit(PlayerKit.NORMAL.configName, player);
                    kitOptions.close(player);
                });
                itemIndex++;
            }
            if (voteList.kits().contains("overpowered")) {
                kitOptions.addAction(new ItemTemplate(itemIndex, Material.PURPLE_DYE,
                        PlayerKit.OVERPOWERED.getDisplayName(),
                        BingoMessage.KIT_OVERPOWERED_DESC.asMultiline()).setGlowing(true), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteKit(PlayerKit.OVERPOWERED.configName, player);
                    kitOptions.close(player);
                });
                itemIndex++;
            }
            if (voteList.kits().contains("reloaded")) {
                kitOptions.addAction(new ItemTemplate(itemIndex, Material.CYAN_DYE,
                        PlayerKit.RELOADED.getDisplayName(),
                        BingoMessage.KIT_RELOADED_DESC.asMultiline()).setGlowing(true), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteKit(PlayerKit.RELOADED.configName, player);
                    kitOptions.close(player);
                });
                itemIndex++;
            }

            BiConsumer<PlayerKit, Integer> addCustomKit = (kit, slot) -> {
                kitOptions.addAction(new ItemTemplate(slot, Material.GRAY_DYE,
                        kit.getDisplayName()).setGlowing(true), (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteKit(kit.configName, player);
                    kitOptions.close(player);
                });
            };

            if (voteList.kits().contains("custom_1") && PlayerKit.CUSTOM_1.isValid()) {
                addCustomKit.accept(PlayerKit.CUSTOM_1, itemIndex);
                itemIndex++;
            }
            if (voteList.kits().contains("custom_2") && PlayerKit.CUSTOM_2.isValid()) {
                addCustomKit.accept(PlayerKit.CUSTOM_2, itemIndex);
                itemIndex++;
            }
            if (voteList.kits().contains("custom_3") && PlayerKit.CUSTOM_3.isValid()) {
                addCustomKit.accept(PlayerKit.CUSTOM_3, itemIndex);
                itemIndex++;
            }
            if (voteList.kits().contains("custom_4") && PlayerKit.CUSTOM_4.isValid()) {
                addCustomKit.accept(PlayerKit.CUSTOM_4, itemIndex);
                itemIndex++;
            }
            if (voteList.kits().contains("custom_5") && PlayerKit.CUSTOM_5.isValid()) {
                addCustomKit.accept(PlayerKit.CUSTOM_5, itemIndex);
                itemIndex++;
            }

            if (itemIndex < 8)
                kitOptions.addCloseAction(EXIT.copy());

            addAction(new ItemTemplate(positions[categoryIndex], 1, Material.ENCHANTED_BOOK, BasicMenu.applyTitleFormat(BingoMessage.VOTE_KIT.asPhrase())),
                    arguments -> kitOptions.open(arguments.player()));
            categoryIndex++;
        }

        if (!voteList.cards().isEmpty()) {
            BasicMenu cardOptions = new BasicMenu(menuBoard, BingoMessage.VOTE_CARD.asPhrase(), 1);

            int itemIndex = 0;
            for (String card : voteList.cards()) {
                Material material = Material.PAPER;
                ItemTemplate itemTemplate = new ItemTemplate(itemIndex, material, Component.text(card).decorate(TextDecoration.BOLD));

                // Add the menu item to the cardOptions menu
                cardOptions.addAction(itemTemplate, (args) -> {
                    HumanEntity player = args.player();
                    lobby.voteCard(card, player);
                    cardOptions.close(player);
                });

                itemIndex++;
            }

            if (itemIndex < 8) {
                cardOptions.addCloseAction(EXIT.copy());
            }
            addAction(new ItemTemplate(positions[categoryIndex], 1, Material.ENCHANTED_BOOK, BasicMenu.applyTitleFormat(BingoMessage.VOTE_CARD.asPhrase())),
                    args -> cardOptions.open(args.player()));
            categoryIndex++;
        }

        if (!voteList.cardSizes().isEmpty()) {
            BasicMenu cardSizeOptions = new BasicMenu(menuBoard, BingoMessage.VOTE_CARDSIZE.asPhrase(), 1);

            int itemIndex = 0;
            if (voteList.cardSizes().contains("3")) {
                ItemTemplate smallSize = new ItemTemplate(itemIndex, Material.RABBIT_HIDE, CardSize.X3.asComponent());
                cardSizeOptions.addAction(smallSize, args -> {
                    lobby.voteCardsize("3", args.player());
                    cardSizeOptions.close(args.player());
                });
            }
            itemIndex++;

            if (voteList.cardSizes().contains("5")) {
                ItemTemplate smallSize = new ItemTemplate(itemIndex, Material.LEATHER, CardSize.X5.asComponent());
                cardSizeOptions.addAction(smallSize, args -> {
                    lobby.voteCardsize("5", args.player());
                    cardSizeOptions.close(args.player());
                });
            }
            itemIndex++;

            if (itemIndex < 8) {
                cardSizeOptions.addCloseAction(EXIT.copy());
            }
            addAction(new ItemTemplate(positions[categoryIndex], 1, Material.ENCHANTED_BOOK, BasicMenu.applyTitleFormat(BingoMessage.VOTE_CARDSIZE.asPhrase())),
                    args -> cardSizeOptions.open(args.player()));
            categoryIndex++;
        }

        addCloseAction(EXIT.copyToSlot(0, 2));
    }
}
