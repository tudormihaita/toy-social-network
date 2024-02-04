package application.toysocialnetwork.domain.message;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReplyMessage extends Message {
    private final String repliedText;

    public  ReplyMessage(Long idSender, String text, LocalDateTime sentAt, Long replyTo, String repliedText) {
        super(idSender, text, sentAt, replyTo);
        this.repliedText = repliedText;
    }

    public String getRepliedText() {
        return repliedText;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplyMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getRepliedText(), that.getRepliedText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRepliedText());
    }

    @Override
    public String toString() {
        return "(Reply to: " + repliedText.substring(0,repliedText.length()/2) + "... " + ")  " +  super.toString();
    }
}
