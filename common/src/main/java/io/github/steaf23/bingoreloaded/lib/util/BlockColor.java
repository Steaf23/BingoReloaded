package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.text.format.TextColor;

public enum BlockColor
{
    BROWN("brown", TextColor.fromHexString("#734829")),
    ORANGE("orange", TextColor.fromHexString("#f17716")),
    WHITE("white", TextColor.fromHexString("#eaeded")),
    GRAY("gray", TextColor.fromHexString("#3f4548")),
    LIGHT_GRAY("light_gray", TextColor.fromHexString("#8e8f87")),
    BLACK("black", TextColor.fromHexString("#16161b")),
    BLUE("blue", TextColor.fromHexString("#353a9e")),
    LIGHT_BLUE("light_blue", TextColor.fromHexString("#3cb0da")),
    GREEN("green", TextColor.fromHexString("#556e1c")),
    LIME("lime", TextColor.fromHexString("#71ba1a")),
    MAGENTA("magenta", TextColor.fromHexString("#be46b5")),
    PURPLE("purple", TextColor.fromHexString("#7b2bad")),
    PINK("pink", TextColor.fromHexString("#ee90ad")),
    RED("red", TextColor.fromHexString("#a12823")),
    YELLOW("yellow", TextColor.fromHexString("#f9c629")),
    CYAN("cyan", TextColor.fromHexString("#158a91")),
    ;

    public final String name;
    public final TextColor textColor;

    public final ItemType dye;
    public final ItemType concretePowder;
    public final ItemType concrete;
    public final ItemType glass;
    public final ItemType glassPane;
    public final ItemType banner;
    public final ItemType shulker;
    public final ItemType bundle;
    public final ItemType wool;
    public final ItemType woolStair;
    public final ItemType woolSlab;
    public final ItemType cushion;
    public final ItemType carpet;
    public final ItemType terracotta;
    public final ItemType glazedTerracotta;
    public final ItemType candle;
    public final ItemType bed;
    public final ItemType harness;

    BlockColor(String name, TextColor textColor)
    {
        this.textColor = textColor;
        this.name = name;

        this.dye = ItemType.of(name + "_dye");
        this.concrete = ItemType.of(name + "_concrete");
        this.concretePowder = ItemType.of(name + "_concrete_powder");
        this.glass = ItemType.of(name + "_stained_glass");
        this.glassPane = ItemType.of(name + "_stained_glass_pane");
        this.banner = ItemType.of(name + "_banner");
        this.shulker = ItemType.of(name + "_shulker_box");
        this.bundle = ItemType.of(name + "_bundle");
        this.wool = ItemType.of(name + "_wool");
        this.woolStair = ItemType.of(name + "_wool_stairs");
        this.woolSlab = ItemType.of(name + "_wool_slab");
        this.cushion = ItemType.of(name + "_cushion");
        this.carpet = ItemType.of(name + "_carpet");
        this.terracotta = ItemType.of(name + "_terracotta");
        this.glazedTerracotta = ItemType.of(name + "_glazed_terracotta");
        this.candle = ItemType.of(name + "_candle");
        this.bed = ItemType.of(name + "_bed");
        this.harness = ItemType.of(name + "_harness");
    }

    public static BlockColor fromName(String name) throws IllegalArgumentException
    {
        for (BlockColor c : BlockColor.values())
        {
            if (c.name.equals(name))
            {
                return c;
            }
        }

        ConsoleMessenger.warn("Could not find a color fitting to " + name + "!");
        throw new IllegalArgumentException();
    }

    public String getName() {
        return name;
    }

    //TODO: finish all colored blocks
}
