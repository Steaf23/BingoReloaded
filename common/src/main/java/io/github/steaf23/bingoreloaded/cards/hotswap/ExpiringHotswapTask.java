package io.github.steaf23.bingoreloaded.cards.hotswap;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.bingoreloaded.lib.util.ExtraMath;
import io.github.steaf23.bingoreloaded.lib.util.TextColorGradient;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class ExpiringHotswapTask implements HotswapTaskHolder
{
    public GameTask task;
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

    public ExpiringHotswapTask(GameTask task, int expirationTimeMinutes, int recoverTime, boolean showExpirationAsDurability) {
        this.task = task;
        this.expirationTimeSeconds = expirationTimeMinutes;
        this.recoveryTime = recoverTime;
        this.currentTime = expirationTimeMinutes;
        this.recovering = false;
        this.showExpirationAsDurability = showExpirationAsDurability;
    }

    @Override
    public ItemTemplate convertToItem(CardDisplayInfo displayInfo) {
        ItemTemplate item = task.toItem(displayInfo);
        if (isRecovering()) {
            item.addDescription("time", 1, BingoMessage.HOTSWAP_RECOVER.asPhrase(GameTimer.getTimeAsComponent(currentTime)).color(TextColor.fromHexString("#5cb1ff")));
        }
        else {
            item.addDescription("time", 1, BingoMessage.HOTSWAP_EXPIRE.asPhrase(GameTimer.getTimeAsComponent(currentTime)).color(getColorForExpirationTime()));
            if (showExpirationAsDurability) {
                item.setMaxDamage(expirationTimeSeconds);
                item.setDamage(currentTime);
            }
        }
        return item;
    }

    @Override
    public void startRecovering() {
        recovering = true;
        currentTime = recoveryTime;
    }

    @Override
    public void updateTaskTime() {
        currentTime -= 1;
    }

    @Override
    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public GameTask getTask() {
        return task;
    }

    @Override
    public boolean isRecovering() {
        return recovering;
    }

    private TextColor getColorForExpirationTime() {
        return EXPIRATION_GRADIENT.sample(ExtraMath.map(currentTime, 0.0f, expirationTimeSeconds, 1.0f, 0.0f));
    }
}
