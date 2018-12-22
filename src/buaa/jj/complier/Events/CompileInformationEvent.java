package buaa.jj.complier.Events;

import java.util.EventObject;

public class CompileInformationEvent extends EventObject {

    private String message;
    private Boolean error;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CompileInformationEvent(Object source, String message, Boolean error) {
        super(source);
        this.message = message;
        this.error = error;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
