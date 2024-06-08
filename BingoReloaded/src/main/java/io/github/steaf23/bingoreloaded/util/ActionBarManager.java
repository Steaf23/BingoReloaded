package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class created to manage the action bar messages sent to the player.
 * users can request to put a message into the action bar of all players.
 * This message can either be shown or discarded based on the priority/ lingerTime
 * New messages need to be sent (in between updates) for old messages to get overwritten
 * Call update() periodically to update the timer and message being displayed.
 */
public class ActionBarManager
{
    private record ActionBarMessage(Function<Player, BaseComponent[]> messageTemplate, int priority, int lingerTime, int insertionTime) {}
    private final BingoSession session;
    private int tickCounter;
    private BaseComponent[] currentMessage;

    private final PriorityQueue<ActionBarMessage> messages;

    public ActionBarManager(BingoSession session) {
        this.session = session;
        this.messages = new PriorityQueue<>(Comparator.comparingInt(b -> -b.priority));
    }

    /**
     * requests an actionbar message. No linger time is specified making it last only until it fades or gets replaced in update by a new message
     * @param priority
     */
    public void requestMessage(Function<Player, BaseComponent[]> messageTemplate, int priority) {
        requestMessage(messageTemplate, priority, 0);
    }

    public void requestMessage(Function<Player, BaseComponent[]> messageTemplate, int priority, int lingerTime) {
        messages.add(new ActionBarMessage(messageTemplate, priority, lingerTime, tickCounter));
    }

    public void update() {
        ActionBarMessage topMessage = messages.peek();
        if (topMessage == null) {
            // We don't actually care about the tick counter at this point since there are no messages anyway...
            return;
        }

        if (topMessage.insertionTime + topMessage.lingerTime <= tickCounter) {
            messages.poll();
        }

        ActionBarMessage messageToShow = topMessage;
        session.teamManager.getParticipants().forEach(p -> {
            p.sessionPlayer().ifPresent(player -> {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, messageToShow.messageTemplate.apply(player));
            });
        });

        // Remove messages that were already expired in the same tick from the queue
        while (topMessage != null) {
            if (topMessage.insertionTime + topMessage.lingerTime <= tickCounter) {
                messages.poll();
            }
            else { // This message is not yet expired, we want to show it to the players.
                break;
            }
            topMessage = messages.peek();
        }

        tickCounter++;
    }
}
