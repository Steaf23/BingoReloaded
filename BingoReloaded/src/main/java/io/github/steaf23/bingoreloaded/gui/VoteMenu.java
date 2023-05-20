package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.ActionMenu;
import io.github.steaf23.bingoreloaded.gui.base.TreeMenu;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

public class VoteMenu extends TreeMenu
{
    private final ActionMenu gamemodeOptions;
    private final ActionMenu kitOptions;

    public VoteMenu(MenuInventory parent)
    {
        super(27, "Vote", parent);

        gamemodeOptions = new ActionMenu(9, "Vote for Gamemode", this);
        gamemodeOptions.addAction(new MenuItem(0, Material.LIME_CONCRETE,
                ChatColor.BOLD + BingoGamemode.REGULAR.name,
                BingoTranslation.INFO_REGULAR_DESC.translate().split("\\n")), (player) -> {
            selectGamemode(BingoGamemode.REGULAR);
        });
        gamemodeOptions.addAction(new MenuItem(1, Material.PINK_CONCRETE,
                ChatColor.BOLD + BingoGamemode.LOCKOUT.name,
                BingoTranslation.INFO_LOCKOUT_DESC.translate().split("\\n")), (player) -> {
            selectGamemode(BingoGamemode.LOCKOUT);
        });
        gamemodeOptions.addAction(new MenuItem(2, Material.LIGHT_BLUE_CONCRETE,
                ChatColor.BOLD + BingoGamemode.COMPLETE.name,
                BingoTranslation.INFO_COMPLETE_DESC.translate().split("\\n")), (player) -> {
            selectGamemode(BingoGamemode.COMPLETE);
        });
        gamemodeOptions.addCloseAction(8);

        kitOptions = new ActionMenu(9, "Vote for Kit", this);
        kitOptions.addAction(new MenuItem(0, Material.RED_DYE,
                PlayerKit.HARDCORE.displayName,
                BingoTranslation.KIT_HARDCORE_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
            selectKit(PlayerKit.HARDCORE);
        });
        kitOptions.addAction(new MenuItem(1, Material.YELLOW_DYE,
                PlayerKit.NORMAL.displayName,
                BingoTranslation.KIT_NORMAL_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
            selectKit(PlayerKit.NORMAL);
        });
        kitOptions.addAction(new MenuItem(2, Material.PURPLE_DYE,
                PlayerKit.OVERPOWERED.displayName,
                BingoTranslation.KIT_OVERPOWERED_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
            selectKit(PlayerKit.OVERPOWERED);
        });
        kitOptions.addAction(new MenuItem(3, Material.CYAN_DYE,
                PlayerKit.RELOADED.displayName,
                BingoTranslation.KIT_RELOADED_DESC.translate().split("\\n")).setGlowing(true), (player) -> {
            selectKit(PlayerKit.RELOADED);
        });


        BiConsumer<PlayerKit, Integer> addCustomKit = (kit, slot) -> {
            if (PlayerKit.getCustomKit(kit) != null)
            {
                kitOptions.addAction(new MenuItem(slot, Material.LIME_DYE,
                        kit.displayName).setGlowing(true), (player) -> {
                    selectKit(kit);
                });
            }
        };

        addCustomKit.accept(PlayerKit.CUSTOM_1, 4);
        addCustomKit.accept(PlayerKit.CUSTOM_2, 5);
        addCustomKit.accept(PlayerKit.CUSTOM_3, 6);
        addCustomKit.accept(PlayerKit.CUSTOM_4, 7);
        addCustomKit.accept(PlayerKit.CUSTOM_5, 8);

        addMenuAction(new MenuItem(2, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Gamemode"), gamemodeOptions);
        addMenuAction(new MenuItem(4, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Kit"), kitOptions);

        addCloseAction(MenuItem.slotFromXY(0, 2));
    }

    public void selectGamemode(BingoGamemode mode)
    {

    }

    public void selectKit(PlayerKit kit)
    {

    }
}
