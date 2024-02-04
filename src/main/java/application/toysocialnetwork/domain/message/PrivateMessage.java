package application.toysocialnetwork.domain.message;

import java.time.LocalDateTime;
import java.util.Objects;

public class PrivateMessage extends Message {
    private final Long idReceiver;

    public PrivateMessage(Long idSender, String text, LocalDateTime sentAt, Long replyTo, Long idReceiver) {
        super(idSender, text, sentAt, replyTo);
        this.idReceiver = idReceiver;
    }

    public PrivateMessage(Long idSender, String text, Long idReceiver) {
        super(idSender, text);
        this.idReceiver = idReceiver;
    }

    public Long getIdReceiver() {
        return idReceiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrivateMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getIdReceiver(), that.getIdReceiver());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdReceiver());
    }
}
