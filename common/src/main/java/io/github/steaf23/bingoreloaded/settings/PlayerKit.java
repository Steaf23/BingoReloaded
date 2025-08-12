package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.CustomKitData;
import io.github.steaf23.bingoreloaded.data.DefaultKitData;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public enum PlayerKit
{
    HARDCORE("hardcore", BingoMessage.KIT_HARDCORE_NAME.asPhrase(), EnumSet.noneOf(EffectOptionFlags.class)),
    NORMAL("normal", BingoMessage.KIT_NORMAL_NAME.asPhrase(), EnumSet.of(EffectOptionFlags.SPEED, EffectOptionFlags.NO_FALL_DAMAGE)),
    OVERPOWERED("overpowered", BingoMessage.KIT_OVERPOWERED_NAME.asPhrase(), EnumSet.allOf(EffectOptionFlags.class)),
    RELOADED("reloaded", BingoMessage.KIT_RELOADED_NAME.asPhrase(), EnumSet.allOf(EffectOptionFlags.class)),
    CUSTOM_1("custom_1", Component.text("CUSTOM 1"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_2("custom_2", Component.text("CUSTOM 2"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_3("custom_3", Component.text("CUSTOM 3"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_4("custom_4", Component.text("CUSTOM 4"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_5("custom_5", Component.text("CUSTOM 5"), EnumSet.noneOf(EffectOptionFlags.class)),
    ;

    public static final ItemTemplate WAND_ITEM = new ItemTemplate(
            ItemType.of("minecraft:warped_fungus_on_a_stick"),
            BingoMessage.WAND_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.WAND_ITEM_DESC.asMultiline())
//            .addEnchantment(Enchantment.UNBREAKING, 3)
            .setCompareKey("wand");

    public static final ItemTemplate CARD_ITEM_RENDERABLE = new ItemTemplate(
            ItemType.of("minecraft:filled_map"),
            BingoMessage.CARD_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.CARD_ITEM_DESC.asMultiline())
            .setGlowing(true)
            .setCompareKey("card");

    public static final ItemTemplate CARD_ITEM = new ItemTemplate(
            ItemType.of("minecraft:flower_banner_pattern"),
            BingoMessage.CARD_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.CARD_ITEM_DESC.asMultiline())
            .setGlowing(true)
            .setCompareKey("card");

    public static final ItemTemplate VOTE_ITEM = new ItemTemplate(
            ItemType.of("minecraft:emerald"),
            BingoMessage.VOTE_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.VOTE_ITEM_DESC.asMultiline())
            .setCompareKey("vote");
//            .addEnchantment(Enchantment.VANISHING_CURSE, 1);

    public static final ItemTemplate TEAM_ITEM = new ItemTemplate(
            ItemType.of("minecraft:white_glazed_terracotta"),
            BingoMessage.TEAM_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.TEAM_ITEM_DESC.asMultiline())
            .setCompareKey("team");
//            .addEnchantment(Enchantment.VANISHING_CURSE, 1);

    public final String configName;
    private final Component displayName;
    public final EnumSet<EffectOptionFlags> defaultEffects;
    private static final DefaultKitData DEFAULT_KIT_DATA = new DefaultKitData();
    private static final CustomKitData CUSTOM_KIT_DATA = new CustomKitData();

    PlayerKit(String configName, Component displayName, EnumSet<EffectOptionFlags> defaultEffects)
    {
        this.configName = configName;
        this.displayName = displayName;
        this.defaultEffects = defaultEffects;
    }

    public Component getDisplayName() {
        if (isCustomKit()) {
            return CUSTOM_KIT_DATA.getCustomKit(this).name();
        }
        return displayName;
    }

    public List<SerializableItem> getItems(TextColor teamColor, ServerSoftware server)
    {
        List<SerializableItem> items = switch (this)
        {
            case HARDCORE, NORMAL, OVERPOWERED, RELOADED -> {
                DefaultKitData.Kit kit = DEFAULT_KIT_DATA.getKit(this);
                if (kit != null) {
                    yield kit.items();
                }
                yield List.of();
            }
            case CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5 -> {
                CustomKit kit = CUSTOM_KIT_DATA.getCustomKit(this);
                if (kit != null) {
                    yield kit.items();
                }
                yield List.of();
            }
        };

        return items.stream()
            .map(item -> new SerializableItem(item.slot(), server.colorItemStack(item.stack(), teamColor)))
            .toList();
    }

    public int getCardSlot() {
        if (isCustomKit()) {
            return CUSTOM_KIT_DATA.getCustomKit(this).cardSlot();
        }
        else {
            // off-hand slot: 40
            return 40;
        }
    }

    public boolean isCustomKit() {
        return customKits().contains(this);
    }

    public boolean isValid() {
        return !isCustomKit() || (PlayerKit.customKits().contains(this) && CUSTOM_KIT_DATA.getCustomKit(this) != null);
    }

    public static PlayerKit fromConfig(String name)
    {
        return fromConfig(name, false);
    }

    public static @Nullable PlayerKit fromConfig(String name, boolean strict)
    {
        if (name == null)
            return strict ? null : HARDCORE;
        return switch (name.toLowerCase())
        {
            case "normal" -> NORMAL;
            case "overpowered" -> OVERPOWERED;
            case "reloaded" -> RELOADED;
            case "custom", "custom_1" -> CUSTOM_1;
            case "custom_2" -> CUSTOM_2;
            case "custom_3" -> CUSTOM_3;
            case "custom_4" -> CUSTOM_4;
            case "custom_5" -> CUSTOM_5;
            default -> strict ? null : HARDCORE;
        };
    }

    public static List<PlayerKit> customKits() {
        return List.of(CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5);
    }
}
