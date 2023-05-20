package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.BingoReloadedExtension;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.util.Message;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class UserInputMenu
{
    private static final MenuItem EMPTY = new MenuItem(Material.ELYTRA, "" + ChatColor.GRAY + ChatColor.BOLD + BingoTranslation.MENU_CLEAR_FILTER.translate(), "");
    private static final MenuItem ACCEPT = new MenuItem(Material.DIAMOND, "" + ChatColor.AQUA + ChatColor.BOLD + BingoTranslation.MENU_ACCEPT.translate(), "");

    public static void open(String title, Consumer<String> result, Player player, MenuInventory parent)
    {
        UserInputMenu.open(title, result, player, parent, "name");
    }

    public static void open(String title, Consumer<String> result, Player player, MenuInventory parent, String startingText)
    {
        AnvilGUI anvil = new AnvilGUI.Builder()
                .onComplete(completion -> {
                    if (parent != null)
                        parent.open(completion.getPlayer());
                    result.accept(completion.getText());
                    return List.of(AnvilGUI.ResponseAction.close());
                })
                .title(Message.PREFIX_STRING_SHORT + " " + ChatColor.DARK_RED + title)
                .text(startingText)
                .itemRight(EMPTY)
                .itemLeft(new ItemStack(Material.ELYTRA))
                .onRightInputClick(p -> {
                    result.accept("");
                    if (parent != null)
                        parent.open(p);
                })
                .itemOutput(ACCEPT)
                .plugin(BingoReloadedExtension.getPlugin(BingoReloadedExtension.class))
                .open(player);
    }
}
