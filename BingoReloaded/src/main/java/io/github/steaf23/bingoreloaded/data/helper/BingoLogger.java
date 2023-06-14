package io.github.steaf23.bingoreloaded.data.helper;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import net.md_5.bungee.api.ChatColor;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class BingoLogger
{
    private static final Logger logger;
    private static final FileHandler fh;

    static
    {
        logger = Logger.getLogger("BingoReloaded");
        try
        {
            fh = new FileHandler(BingoReloaded.getPlugin(BingoReloaded.class).getDataFolder() + "\\bingo.log", 0,1, true);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        logger.setUseParentHandlers(false);
        logger.addHandler(fh);

        var formatter = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord record)
            {
                return String.format(format,
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage()
                );
            }
        };

        fh.setFormatter(formatter);
        logger.info("Started Bingo Logger");
    }

    public static void log(String clazz, String message)
    {
        logger.info("{" + clazz + "} " + ChatColor.stripColor(message));
    }
}
