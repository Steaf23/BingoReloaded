package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.CustomKitData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

public class KitOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public KitOptionsMenu(MenuBoard menuBoard, BingoSession session)
    {
        super(menuBoard, BingoMessage.OPTIONS_KIT.asPhrase(), 5);
        this.session = session;

        ItemTemplate HARDCORE = new ItemTemplate(1, 1,
                ItemTypePaper.of(Material.RED_CONCRETE), PlayerKit.HARDCORE.getDisplayName(),
                BingoMessage.KIT_HARDCORE_DESC.asMultiline());
        ItemTemplate NORMAL = new ItemTemplate(3, 1,
                ItemTypePaper.of(Material.YELLOW_CONCRETE), PlayerKit.NORMAL.getDisplayName(),
                BingoMessage.KIT_NORMAL_DESC.asMultiline());
        ItemTemplate OVERPOWERED = new ItemTemplate(5, 1,
                ItemTypePaper.of(Material.PURPLE_CONCRETE), PlayerKit.OVERPOWERED.getDisplayName(),
                BingoMessage.KIT_OVERPOWERED_DESC.asMultiline());
        ItemTemplate RELOADED = new ItemTemplate(7, 1,
                ItemTypePaper.of(Material.CYAN_CONCRETE), PlayerKit.RELOADED.getDisplayName(),
                BingoMessage.KIT_RELOADED_DESC.asMultiline());

        addAction(HARDCORE, args -> {
            setKit(PlayerKit.HARDCORE);
            close(args.player());
        });
        addAction(NORMAL, args -> {
            setKit(PlayerKit.NORMAL);
            close(args.player());
        });
        addAction(OVERPOWERED, args -> {
            setKit(PlayerKit.OVERPOWERED);
            close(args.player());
        });
        addAction(RELOADED, args -> {
            setKit(PlayerKit.RELOADED);
            close(args.player());
        });
        addAction(HARDCORE, args -> {
            setKit(PlayerKit.HARDCORE);
            close(args.player());
        });

        int kitIdx = 0;
        CustomKitData customKitData = new CustomKitData();
        for (PlayerKit kit : PlayerKit.customKits()) {
            CustomKit customkit = customKitData.getCustomKit(kit);
            if (customkit != null) {
                addAction(new ItemTemplate(kitIdx * 2, 3, ItemTypePaper.of(Material.WHITE_CONCRETE),
                        customkit.name(), Component.text("Custom kit").color(NamedTextColor.DARK_GRAY)), args -> {
                    setKit(PlayerKit.fromConfig(kit.configName));
                    close(args.player());
                });
            } else {
                int kitNr = kitIdx + 1;
                addItem(new ItemTemplate(kitIdx * 2, 3, ItemTypePaper.of(Material.GRAY_CONCRETE),
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
