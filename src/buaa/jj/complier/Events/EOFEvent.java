package buaa.jj.complier.Events;

import java.util.EventObject;

public class EOFEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public EOFEvent(Object source) {
        super(source);
    }
}
