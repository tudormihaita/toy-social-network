package application.toysocialnetwork.domain.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class GroupMessage extends Message {
    private final List<Long> idReceivers;

    public GroupMessage(Long idSender, String text, LocalDateTime sentAt, Long replyTo, List<Long> idReceivers) {
        super(idSender, text, sentAt, replyTo);
        this.idReceivers = idReceivers;
    }

    public GroupMessage(Long idFromUser, String text, List<Long> idReceivers) {
        super(idFromUser, text);
        this.idReceivers = idReceivers;
    }

    public List<Long> getIdReceivers() {
        return idReceivers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getIdReceivers(), that.getIdReceivers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdReceivers());
    }
}
