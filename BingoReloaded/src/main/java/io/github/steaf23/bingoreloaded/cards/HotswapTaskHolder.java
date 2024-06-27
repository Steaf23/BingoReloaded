package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.TextColorGradient;
import io.github.steaf23.easymenulib.util.ExtraMath;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class HotswapTaskHolder
{
    public BingoTask task;
    public int expirationTimeSeconds;
    public int recoveryTime;
    public int currentTime;
    public boolean recovering;

    private final boolean showExpirationAsDurability;

    private static final TextColorGradient EXPIRATION_GRADIENT = new TextColorGradient()
            .addColor(TextColor.fromHexString("#ffd200"), 0.0f)
            .addColor(TextColor.fromHexString("#e85e21"), 0.5f)
            .addColor(TextColor.fromHexString("#750e0e"), 0.8f)
            .addColor(NamedTextColor.DARK_GRAY, 1.0f);

    public HotswapTaskHolder(BingoTask task, int expirationTimeMinutes, int recoverTime, boolean showExpirationAsDurability) {
        this.task = task;
        this.expirationTimeSeconds = expirationTimeMinutes;
        this.recoveryTime = recoverTime;
        this.currentTime = expirationTimeMinutes;
        this.recovering = false;
        this.showExpirationAsDurability = showExpirationAsDurability;
    }

    public ItemTemplate convertToItem() {
        ItemTemplate item = task.toItem();
        if (isRecovering()) {
            item.addDescription("time", 1, BingoTranslation.HOTSWAP_RECOVER.asSingleComponent().color(TextColor.fromHexString("#5cb1ff")), GameTimer.getTimeAsComponent(currentTime));
        }
        else {
            item.addDescription("time", 1, BingoTranslation.HOTSWAP_EXPIRE.asSingleComponent().color(getColorForExpirationTime()), GameTimer.getTimeAsComponent(currentTime));
            if (showExpirationAsDurability) {
                item.setMaxDamage(expirationTimeSeconds);
                item.setDamage(currentTime);
            }
        }
        return item;
    }

    public void startRecovering() {
        recovering = true;
        currentTime = recoveryTime;
    }

    public boolean isRecovering() {
        return recovering;
    }

    private TextColor getColorForExpirationTime() {
        return EXPIRATION_GRADIENT.sample(ExtraMath.map(currentTime, 0.0f, expirationTimeSeconds, 1.0f, 0.0f));
    }
}
