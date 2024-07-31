package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Function;

/**
 * Class created to manage the action bar messages sent to the player.
 * users can request to put a message into the action bar of all players.
 * This message can either be shown or discarded based on the priority/ lingerTime
 * New messages need to be sent (in between updates) for old messages to get overwritten
 * Call update() periodically to update the timer and message being displayed.
 * Sens action bar messages to all players in the given session.
 */
public class ActionBarManager
{
    private record ActionBarMessage(Function<Player, Component> messageTemplate, int priority, int lingerTime, int insertionTime) {}
    private final BingoSession session;
    private int tickCounter;

    private final PriorityQueue<ActionBarMessage> messages;

    public ActionBarManager(BingoSession session) {
        this.session = session;
        this.messages = new PriorityQueue<>(Comparator.comparingInt(b -> -b.priority));
    }

    /**
     * Requests an actionbar message. No linger time is specified making it last only until it fades or gets replaced in update by a new message
     */
    public void requestMessage(Function<Player, Component> messageTemplate, int priority) {
        requestMessage(messageTemplate, priority, 0);
    }

    /**
     * Requests an actionbar message. Stays on the players screen until it gets replaced with a higher priority message or until the lingerTime expired, whichever comes first.
     */
    public void requestMessage(Function<Player, Component> messageTemplate, int priority, int lingerTime) {
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
                player.sendActionBar(messageToShow.messageTemplate.apply(player));
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
