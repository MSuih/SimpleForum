package simplforum.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import simplforum.utils.PersistableIdSerializer;

/** Model for a thread in a topic. This class is named <code>MessageThread</code> to avoid
 * conflicts with <code>java.lang.Thread</code>.
 */
@Entity
@JsonIgnoreProperties({"messages", "account", "new"})
public class MessageThread extends AbstractPersistable<Long> {
    @JoinColumn
    @ManyToOne
    private Account account;
    @NotBlank
    @Length(min = 3)
    private String title;
    @ManyToOne
    @JoinColumn
    @NotNull
    @JsonSerialize(using = PersistableIdSerializer.class)
    private Topic topic;
    @OneToMany(mappedBy="thread")
    private List<Message> messages;
    @NotNull
    private boolean stickied;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    @NotNull
    private Timestamp lastUpdate;

    public MessageThread() {

    }

    /** Sets the last update to current timestamp automatically when thread is created.
     */
    @PrePersist
    protected void onCreate() {
        lastUpdate = new Timestamp(System.currentTimeMillis());
    }

    /** Updates the last update timestamp automatically when a thread is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        lastUpdate = new Timestamp(System.currentTimeMillis());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Message> getMessages() {
        if (messages == null)
            messages = new ArrayList<>();
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        if (messages == null)
            messages = new ArrayList<>();
        messages.add(message);
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public boolean isStickied() {
        return stickied;
    }
    public boolean getStickied() {
        return stickied;
    }

    public void setStickied(boolean stickied) {
        this.stickied = stickied;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
