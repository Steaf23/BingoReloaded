package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public class KitOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    private static final MenuItem HARDCORE = new MenuItem(1, 1,
            Material.RED_CONCRETE, PlayerKit.HARDCORE.displayName,
            BingoTranslation.KIT_HARDCORE_DESC.translate().split("\\n"));
    private static final MenuItem NORMAL = new MenuItem(3, 1,
            Material.YELLOW_CONCRETE, PlayerKit.NORMAL.displayName,
            BingoTranslation.KIT_NORMAL_DESC.translate().split("\\n"));
    private static final MenuItem OVERPOWERED = new MenuItem(5, 1,
            Material.PURPLE_CONCRETE, PlayerKit.OVERPOWERED.displayName,
            BingoTranslation.KIT_OVERPOWERED_DESC.translate().split("\\n"));
    private static final MenuItem RELOADED = new MenuItem(7, 1,
            Material.CYAN_CONCRETE, PlayerKit.RELOADED.displayName,
            BingoTranslation.KIT_RELOADED_DESC.translate().split("\\n"));

    public KitOptionsMenu(MenuManager menuManager, BingoSession session)
    {
        super(menuManager, BingoTranslation.OPTIONS_KIT.translate(), 5);
        this.session = session;
        addAction(HARDCORE, p -> {
            setKit(PlayerKit.HARDCORE);
            close(p);
        });
        addAction(NORMAL, p -> {
            setKit(PlayerKit.NORMAL);
            close(p);
        });
        addAction(OVERPOWERED, p -> {
            setKit(PlayerKit.OVERPOWERED);
            close(p);
        });
        addAction(RELOADED, p -> {
            setKit(PlayerKit.RELOADED);
            close(p);
        });
        addAction(HARDCORE, p -> {
            setKit(PlayerKit.HARDCORE);
            close(p);
        });

        int kitIdx = 0;
        for (PlayerKit kit : PlayerKit.customKits()) {
            CustomKit customkit = PlayerKit.getCustomKit(kit);
            if (customkit != null) {
                addAction(new MenuItem(kitIdx * 2, 3, Material.WHITE_CONCRETE,
                        ChatColor.RESET + customkit.getName(), "Custom kit"), p -> {
                    setKit(PlayerKit.fromConfig(kit.configName));
                    close(p);
                });
            } else {
                int kitNr = kitIdx + 1;
                addItem(new MenuItem(kitIdx * 2, 3, Material.GRAY_CONCRETE,
                        "" + ChatColor.GRAY + "Custom Kit Slot " + kitNr,
                        "Create a custom kit from your inventory using ",
                        "" + ChatColor.RED + ChatColor.ITALIC + "/bingo kit add " + kitNr + " <name>!"));
            }
            kitIdx++;
        }
    }

    private void setKit(PlayerKit kit)
    {
        session.settingsBuilder.kit(kit, session);
    }
}
