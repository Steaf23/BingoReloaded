package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.item.itemtext.ItemText;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.GameTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SerializableAs("BingoTask")
public class BingoTask implements ConfigurationSerializable
{
    enum TaskType
    {
        ITEM,
        STATISTIC,
        ADVANCEMENT,
    }

    public Optional<BingoPlayer> completedBy;
    public long completedAt;
    public boolean voided;

    public final TaskType type;
    public final TaskData data;
    public final ChatColor nameColor;
    public final Material material;
    public final boolean glowing;

    public BingoTask(TaskData data)
    {
        this.data = data;
        this.completedBy = Optional.ofNullable(null);
        this.voided = false;
        this.completedAt = -1L;

        if (data instanceof ItemTask itemTask)
        {
            this.type = TaskType.ITEM;
            this.nameColor = ChatColor.YELLOW;
            this.material = itemTask.material();
            this.glowing = false;
        }
        else if (data instanceof AdvancementTask advTask)
        {
            this.type = TaskType.ADVANCEMENT;
            this.nameColor = ChatColor.GREEN;
            this.material = Material.FILLED_MAP;
            this.glowing = true;
        }
        else if (data instanceof StatisticTask statTask)
        {
            this.type = TaskType.STATISTIC;
            this.nameColor = ChatColor.LIGHT_PURPLE;
            this.material = BingoStatistic.getMaterial(statTask.statistic());
            this.glowing = true;
        }
        else
        {
            Message.log("This Type of data is not supported by BingoTask: '" + data + "'!");
            this.type = TaskType.ITEM;
            this.glowing = false;
            this.nameColor = ChatColor.WHITE;
            this.material = Material.BEDROCK;
        }
    }

    public ItemStack asStack()
    {
        ItemStack item;

        if (voided && completedBy.isPresent())
        {
            ItemText addedDesc = new ItemText(TranslationData.translate("game.team.dropped",
                            completedBy.get().player().getName()), ChatColor.BLACK);

            item = new ItemStack(Material.BEDROCK);
            ItemText.buildItemText(item,
                    data.getDisplayName().setModifiers(ChatColor.BLACK, ChatColor.STRIKETHROUGH),
                    new ItemText[]{addedDesc, data.getDescription().setModifiers(ChatColor.BLACK)});
        }
        else if (completedBy.isPresent())
        {
            FlexColor color = CardBuilder.completeColor(completedBy.get().team());
            Material completeMaterial = color.glassPane;

            String timeString = GameTimer.getTimeAsString(completedAt);

            String desc1 = new Message("game.item.complete_lore").color(ChatColor.DARK_PURPLE).italic()
                    .arg(FlexColor.fromName(completedBy.get().team().getName()).getTranslatedName()).color(completedBy.get().team().getColor()).bold()
                    .arg(timeString).color(ChatColor.GOLD).toLegacyString();

            item = new ItemStack(completeMaterial);
            ItemText.buildItemText(item,
                    data.getDisplayName().setModifiers(ChatColor.GRAY, ChatColor.STRIKETHROUGH),
                    new ItemText[]{new ItemText(desc1)});

            ItemMeta meta = item.getItemMeta();
            if (meta != null)
            {
                item.setItemMeta(meta);
            }
        }
        else
        {
            item = new ItemStack(material);
            ItemText.buildItemText(item,
                    data.getDisplayName().setModifiers(nameColor),
                    new ItemText[]{data.getDescription()});
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdcData = meta.getPersistentDataContainer();
        pdcData.set(getTaskDataKey("type"), PersistentDataType.STRING, type.name());
        pdcData.set(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)(voided ? 1 : 0));
        pdcData.set(getTaskDataKey("completed_at"), PersistentDataType.LONG, completedAt);
        if (completedBy.isPresent())
            pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, completedBy.get().player().getUniqueId().toString());
        else
            pdcData.set(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");

        pdcData = data.pdcSerialize(pdcData);

        if (glowing && completedBy.isEmpty())
        {

            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static BingoTask fromStack(ItemStack in)
    {
        PersistentDataContainer pdcData = in.getItemMeta().getPersistentDataContainer();

        boolean voided = pdcData.getOrDefault(getTaskDataKey("voided"), PersistentDataType.BYTE, (byte)0) != 0;
        UUID completedBy = null;
        String idStr = pdcData.getOrDefault(getTaskDataKey("completed_by"), PersistentDataType.STRING, "");
        long timeStr = pdcData.getOrDefault(getTaskDataKey("completed_at"), PersistentDataType.LONG, -1L);
        if (idStr != "")
            completedBy = UUID.fromString(idStr);

        String typeStr = pdcData.getOrDefault(getTaskDataKey("type"), PersistentDataType.STRING, "");
        TaskType type;
        if (typeStr.isEmpty())
        {
            Message.log("Cannot create a valid task from this item stack!");
            return null;
        }

        type = TaskType.valueOf(typeStr);
        BingoTask task = switch (type)
        {
            case ADVANCEMENT -> new BingoTask(AdvancementTask.fromPdc(pdcData));
            case STATISTIC -> new BingoTask(StatisticTask.fromPdc(pdcData));
            default ->  new BingoTask(ItemTask.fromPdc(pdcData));
        };

        return task;
    }

    public static NamespacedKey getTaskDataKey(String property)
    {
        return new NamespacedKey(BingoReloaded.get(), "task." + property);
    }

//    // TODO: Move to BingoTaskBuilder
//    public boolean complete(BingoPlayer player, long time, BingoTeam team)
//    {
//        if (completedBy.isPresent())
//            return false;
//
//        completedBy = Optional.of(player);
//        completedAt = time;
//
//        FlexColor color = CardBuilder.completeColor(team);
//        Material completeMaterial = color.glassPane;
//
//        BaseComponent itemName = data.getDisplayName().asComponent();
//
//        String timeString = GameTimer.getTimeAsString(time);
//
//        new Message("game.item.completed").color(ChatColor.GREEN)
//                .component(itemName).color(nameColor)
//                .arg(FlexColor.fromName(team.getName()).getTranslatedName()).color(color.chatColor).bold()
//                .arg(timeString).color(ChatColor.WHITE)
//                .sendAll();
//        return true;
//    }

    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        return new HashMap<>(){{
           put("type", type.name());
           put("data", data);
        }};
    }

    public static BingoTask deserialize(Map<String, Object> data)
    {
        return new BingoTask(
                ((TaskData) data.get("data"))
        );
    }

    public BingoTask copy()
    {
        return new BingoTask((ItemTask) data);
    }
}
