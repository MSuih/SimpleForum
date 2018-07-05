package simplforum.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import simplforum.utils.PersistableIdSerializer;

/** Model for a message in a thread.
 */
@Entity
@JsonIgnoreProperties({"new", "account"})
public class Message extends AbstractPersistable<Long> {
    @JoinColumn
    @ManyToOne
    private Account account;
    @Lob
    @NotBlank
    @Length(min=3)
    private String content;
    @ManyToOne
    @JoinColumn
    @NotNull
    @JsonSerialize(using = PersistableIdSerializer.class)
    private MessageThread thread;
    @Lob
    private String moderatorNote;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    @NotNull
    private Timestamp timePosted;

    public Message() {

    }

    /** Sets the time posted timestamp automatically when a thread is created.
     */
    @PrePersist
    protected void onCreate() {
        timePosted = new Timestamp(System.currentTimeMillis());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageThread getThread() {
        return thread;
    }

    public void setThread(MessageThread thread) {
        this.thread = thread;
    }

    public String getModeratorNote() {
        return moderatorNote;
    }

    public void setModeratorNote(String moderatorNote) {
        this.moderatorNote = moderatorNote;
    }

    public Timestamp getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(Timestamp timePosted) {
        this.timePosted = timePosted;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
