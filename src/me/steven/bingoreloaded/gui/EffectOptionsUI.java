package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.List;

public class EffectOptionsUI extends AbstractGUIInventory
{
    private final BingoGame game;
    private final EnumSet<EffectOptionFlags> flags;

    private final InventoryItem[] options;

    public EffectOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(45, "Choose gameplay effects", parent);
        this.game = game;

        options = new InventoryItem[]{
                new InventoryItem(GUIBuilder5x9.OptionPositions.FIVE_TOP_WIDE.positions[0],
                        Material.CARROT, "" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.NIGHT_VISION.name + " DISABLED",
                        ChatColor.RED + "Click to enable this option"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.FIVE_TOP_WIDE.positions[1],
                        Material.PUFFERFISH, "" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.WATER_BREATHING.name + " DISABLED",
                        ChatColor.RED + "Click to enable this option"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.FIVE_TOP_WIDE.positions[2],
                        Material.MAGMA_CREAM, "" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.FIRE_RESISTANCE.name + " DISABLED",
                        ChatColor.RED + "Click to enable this option"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.FIVE_TOP_WIDE.positions[3],
                        Material.NETHERITE_BOOTS, "" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.NO_FALL_DAMAGE.name + " DISABLED",
                        ChatColor.RED + "Click to enable this option"),
                new InventoryItem(GUIBuilder5x9.OptionPositions.FIVE_TOP_WIDE.positions[4],
                        Material.FEATHER, "" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.CARD_SPEED.name + " DISABLED",
                        ChatColor.RED + "Click to enable this option"),
                new InventoryItem(44, Material.DIAMOND, "Save and exit")
        };
        fillOptions(options);

        flags = game.getEffects();
        updateUI();
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        for (int i = 0; i < options.length - 1; i++)
        {
            if (slotClicked == options[i].getSlot())
            {
                toggleOption(i);
            }
        }

        if (slotClicked == options[5].getSlot())
        {
            game.setEffects(flags);
            close(player);
        }
    }

    private void toggleOption(int index)
    {
        EffectOptionFlags option = EffectOptionFlags.values()[index];
        if (flags.contains(option))
            flags.remove(option);
        else
            flags.add(option);
        updateUI();
    }

    public void updateUI()
    {
        for (int i = 0; i < options.length - 1; i++)
        {
            ItemMeta meta = options[i].getItemMeta();
            if (meta != null)
            {
                if (flags.contains(EffectOptionFlags.values()[i]))
                {
                    meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " ENABLED");
                    meta.setLore(List.of(ChatColor.GREEN + "Click to DISABLE this option"));
                }
                else
                {
                    meta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " DISABLED");
                    meta.setLore(List.of(ChatColor.RED + "Click to ENABLE this option"));
                }
            }
            options[i].setItemMeta(meta);
            addOption(options[i]);
        }
    }
}
