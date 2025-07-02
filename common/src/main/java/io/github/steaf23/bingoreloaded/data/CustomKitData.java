package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class CustomKitData
{
    private final DataAccessor data = BingoReloaded.getDataAccessor("data/kits");

    public boolean assignCustomKit(Component kitName, PlayerKit slot, Player commandSender)
    {
        if (data.contains(slot.configName))
            return false;

        data.setSerializable(slot.configName, CustomKit.class, CustomKit.fromPlayerInventory(commandSender, kitName, slot));
        data.saveChanges();
        return true;
    }

    public boolean removeCustomKit(PlayerKit slot)
    {
        if (!data.contains(slot.configName))
            return false;

        data.erase(slot.configName);
        data.saveChanges();

        return true;
    }

    public @Nullable CustomKit getCustomKit(PlayerKit slot)
    {
        return data.getSerializable(slot.configName, CustomKit.class);
    }
}
