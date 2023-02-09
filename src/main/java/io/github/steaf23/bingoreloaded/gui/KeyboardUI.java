package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.BannerBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyboardUI extends AbstractGUIInventory
{
    private String keyword;

    private static final InventoryItem BG_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");
    private static final InventoryItem CLEAR = new InventoryItem(46, Material.HOPPER, "" + ChatColor.GRAY + ChatColor.BOLD + TranslationData.translate("menu.clear"), "");
    private static final InventoryItem ACCEPT = new InventoryItem(49, Material.DIAMOND, "" + ChatColor.AQUA + ChatColor.BOLD + TranslationData.translate("menu.accept"), "");

    private static InventoryItem[] options = new InventoryItem[0];

    private static Map<String, Integer> letterSlots = new HashMap<>(){{
            put("Q", 9); put("W", 10); put("E", 11); put("R", 12); put("T", 13); put("Y", 14); put("U", 15); put("I", 16); put("O", 17);
            put("A", 18); put("S", 19); put("D", 20); put("F", 21); put("G", 22); put("H", 23); put("J", 24); put("K", 25); put("L", 26);
            put("Z", 28); put("X", 29); put("C", 30); put("V", 31); put("B", 32); put("N", 33); put("M", 34); put("P", 35);
            put("_", 40); put("<-", 43);
    }};

    private static Map<String, ItemStack> bannerPatterns = new HashMap<>(){{
            put("Q", BannerBuilder.fromCommand("/give @p minecraft:black_banner{BlockEntityTag:{Patterns:[{Pattern:mr,Color:0},{Pattern:rs,Color:15},{Pattern:ls,Color:15},{Pattern:br,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("W", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:bt,Color:15},{Pattern:bts,Color:0},{Pattern:ls,Color:15},{Pattern:rs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("E", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ls,Color:15},{Pattern:ts,Color:15},{Pattern:ms,Color:15},{Pattern:bs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("R", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:hh,Color:15},{Pattern:cs,Color:0},{Pattern:ts,Color:15},{Pattern:ls,Color:15},{Pattern:drs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("T", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ts,Color:15},{Pattern:cs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("Y", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:drs,Color:15},{Pattern:hhb,Color:0},{Pattern:dls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("U", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:bs,Color:15},{Pattern:ls,Color:15},{Pattern:rs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("I", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:cs,Color:15},{Pattern:ts,Color:15},{Pattern:bs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("O", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ls,Color:15},{Pattern:rs,Color:15},{Pattern:bs,Color:15},{Pattern:ts,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("A", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:rs,Color:15},{Pattern:ls,Color:15},{Pattern:ms,Color:15},{Pattern:ts,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("S", BannerBuilder.fromCommand("/give @p minecraft:black_banner{BlockEntityTag:{Patterns:[{Pattern:mr,Color:0},{Pattern:ms,Color:0},{Pattern:drs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("D", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:rs,Color:15},{Pattern:bs,Color:15},{Pattern:ts,Color:15},{Pattern:cbo,Color:0},{Pattern:ls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("F", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ms,Color:15},{Pattern:rs,Color:0},{Pattern:ts,Color:15},{Pattern:ls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("G", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:rs,Color:15},{Pattern:hh,Color:0},{Pattern:bs,Color:15},{Pattern:ls,Color:15},{Pattern:ts,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("H", BannerBuilder.fromCommand("/give @p minecraft:black_banner{BlockEntityTag:{Patterns:[{Pattern:ts,Color:0},{Pattern:bs,Color:0},{Pattern:ls,Color:15},{Pattern:rs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("J", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ls,Color:15},{Pattern:hh,Color:0},{Pattern:bs,Color:15},{Pattern:rs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("K", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:drs,Color:15},{Pattern:hh,Color:0},{Pattern:dls,Color:15},{Pattern:ls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("L", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:bs,Color:15},{Pattern:ls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("Z", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ts,Color:15},{Pattern:dls,Color:15},{Pattern:bs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("X", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:cr,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("C", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ts,Color:15},{Pattern:bs,Color:15},{Pattern:rs,Color:15},{Pattern:ms,Color:0},{Pattern:ls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("V", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:dls,Color:15},{Pattern:ls,Color:15},{Pattern:bt,Color:0},{Pattern:dls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("B", BannerBuilder.fromCommand("/give @p minecraft:black_banner{BlockEntityTag:{Patterns:[{Pattern:mr,Color:0},{Pattern:rs,Color:15},{Pattern:cbo,Color:0},{Pattern:vh,Color:15},{Pattern:ms,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("N", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ls,Color:15},{Pattern:tt,Color:0},{Pattern:drs,Color:15},{Pattern:rs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("M", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:tt,Color:15},{Pattern:tts,Color:0},{Pattern:ls,Color:15},{Pattern:rs,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("P", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:rs,Color:15},{Pattern:hhb,Color:0},{Pattern:ms,Color:15},{Pattern:ts,Color:15},{Pattern:ls,Color:15},{Pattern:bo,Color:0}]}} 1"));
            put("_", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:cre,Color:15},{Pattern:bo,Color:0},{Pattern:hh,Color:0},{Pattern:bs,Color:0},{Pattern:ms,Color:0}]}} 1"));
            put("<-", BannerBuilder.fromCommand("/give @p minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:ls,Color:15},{Pattern:ms,Color:15},{Pattern:ts,Color:0},{Pattern:bs,Color:0},{Pattern:cbo,Color:0}]}} 1"));
    }};

    public KeyboardUI(AbstractGUIInventory parent)
    {
        super(54, "", parent);

        this.keyword = "";
        this.options = new InventoryItem[28];

        int idx = 0;
        for (String key : letterSlots.keySet())
        {
            InventoryItem bannerKeyItem = new InventoryItem(letterSlots.get(key), bannerPatterns.get(key));
            ItemMeta bannerMeta = bannerKeyItem.getItemMeta();
            bannerMeta.setDisplayName(TITLE_PREFIX + key);
            bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            bannerKeyItem.setItemMeta(bannerMeta);
            options[idx] = bannerKeyItem;
            idx++;
        }

        fillOptions(options);
        fillOptions(BG_ITEM.inSlot(45),
                CLEAR,
                BG_ITEM.inSlot(47),
                BG_ITEM.inSlot(48),
                ACCEPT,
                BG_ITEM.inSlot(50),
                BG_ITEM.inSlot(51),
                BG_ITEM.inSlot(52),
                BG_ITEM.inSlot(53)
        );
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        for (var pair : letterSlots.entrySet())
        {
            if (pair.getValue() == slotClicked)
            {
                if (pair.getKey().equals("<-"))
                {
                    if (keyword.length() > 0)
                    {
                        keyword = keyword.substring(0, keyword.length() - 1);
                    }
                }
                else if (pair.getKey().equals("_"))
                {
                    keyword += " ";
                }
                else
                {
                    keyword += pair.getKey();
                }
                updateTitle(player);
                return;
            }
        }

        if (slotClicked == CLEAR.getSlot())
        {
            keyword = "";
            updateTitle(player);
        }
        else if (slotClicked == ACCEPT.getSlot())
        {
            close(player);
            Bukkit.getScheduler().runTask(BingoReloaded.get(), this::storeResult);
        }
    }

    // Will be called when the inventory closes, i.e. when the value is needed. This method can then be used to store the string that the user typed.
    public abstract void storeResult();

    public String getKeyword()
    {
        return keyword;
    }

    public void open(HumanEntity player, String keyword)
    {
        open(player);
        this.keyword = keyword;
        updateTitle(player);
    }

    public void updateTitle(HumanEntity player)
    {
        inventory = Bukkit.createInventory(new GUIHolder(), 54, Message.PREFIX_STRING + ChatColor.GOLD + "\"" + keyword + "\"");

        fillOptions(options);
        fillOptions(BG_ITEM.inSlot(45),
                CLEAR,
                BG_ITEM.inSlot(47),
                BG_ITEM.inSlot(48),
                ACCEPT,
                BG_ITEM.inSlot(50),
                BG_ITEM.inSlot(51),
                BG_ITEM.inSlot(52),
                BG_ITEM.inSlot(53)
        );
        player.openInventory(inventory);
    }
}
