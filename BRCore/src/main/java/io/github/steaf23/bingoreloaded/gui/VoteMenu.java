package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.TreeMenu;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Material;

public class VoteMenu extends TreeMenu
{
    private final OptionMenu gamemodeOptions;
    private final OptionMenu kitOptions;

    public VoteMenu(MenuInventory parent)
    {
        super(27, "Vote", parent);

        gamemodeOptions = new OptionMenu(9, "Vote for Gamemode", this);
        kitOptions = new OptionMenu(9, "Vote for Kit", this);
        gamemodeOptions.addOption("POTAT", new InventoryItem(0, Material.GRAY_CONCRETE, "POTAT"), () -> {
            Message.log("CHEE");
        });

        addMenuOption(new InventoryItem(2, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Gamemode"), gamemodeOptions);
        addMenuOption(new InventoryItem(4, 1, Material.ENCHANTED_BOOK, TITLE_PREFIX + "Vote for Kit"), kitOptions);
    }
}
