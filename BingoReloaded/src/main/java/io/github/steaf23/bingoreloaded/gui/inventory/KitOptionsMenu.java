package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public class KitOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public KitOptionsMenu(MenuBoard menuBoard, BingoSession session)
    {
        super(menuBoard, BingoTranslation.OPTIONS_KIT.asSingleComponent(), 5);
        this.session = session;

        ItemTemplate HARDCORE = new ItemTemplate(1, 1,
                Material.RED_CONCRETE, PlayerKit.HARDCORE.getDisplayName(),
                BingoTranslation.KIT_HARDCORE_DESC.asComponent());
        ItemTemplate NORMAL = new ItemTemplate(3, 1,
                Material.YELLOW_CONCRETE, PlayerKit.NORMAL.getDisplayName(),
                BingoTranslation.KIT_NORMAL_DESC.asComponent());
        ItemTemplate OVERPOWERED = new ItemTemplate(5, 1,
                Material.PURPLE_CONCRETE, PlayerKit.OVERPOWERED.getDisplayName(),
                BingoTranslation.KIT_OVERPOWERED_DESC.asComponent());
        ItemTemplate RELOADED = new ItemTemplate(7, 1,
                Material.CYAN_CONCRETE, PlayerKit.RELOADED.getDisplayName(),
                BingoTranslation.KIT_RELOADED_DESC.asComponent());

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
                addAction(new ItemTemplate(kitIdx * 2, 3, Material.WHITE_CONCRETE,
                        Component.text(customkit.getName()), Component.text("Custom kit")), p -> {
                    setKit(PlayerKit.fromConfig(kit.configName));
                    close(p);
                });
            } else {
                int kitNr = kitIdx + 1;
                addItem(new ItemTemplate(kitIdx * 2, 3, Material.GRAY_CONCRETE,
                        Component.text("Custom Kit Slot " + kitNr).color(NamedTextColor.GRAY),
                        Component.text("Create a custom kit from your inventory using "),
                        Component.text("/bingo kit add " + kitNr + " <name>!").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC)));
            }
            kitIdx++;
        }
    }

    private void setKit(PlayerKit kit)
    {
        session.settingsBuilder.kit(kit);
    }
}
