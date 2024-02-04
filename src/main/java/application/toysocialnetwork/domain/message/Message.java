package application.toysocialnetwork.domain.message;

import application.toysocialnetwork.domain.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long> {
    private final Long idSender;
    private final String text;
    private final LocalDateTime sentAt;
    private Long replyTo;

    public Message(Long idSender, String text, LocalDateTime sentAt, Long replyTo) {
        this.idSender = idSender;
        this.text = text;
        this.sentAt = sentAt;
        this.replyTo = replyTo;
    }

    public Message(Long idSender, String text) {
        this.idSender = idSender;
        this.text = text;
        this.sentAt = LocalDateTime.now();
        this.replyTo = null;
    }

    public Long getIdSender() {
        return idSender;
    }

    public String getText() {
        return text;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getIdSender(), message1.getIdSender()) && Objects.equals(getText(), message1.getText()) && Objects.equals(getSentAt(), message1.getSentAt()) && Objects.equals(getReplyTo(), message1.getReplyTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdSender(), getText(), getSentAt(), getReplyTo());
    }

    @Override
    public String toString() {
        return text;
    }
}
