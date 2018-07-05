package simplforum.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;
import simplforum.utils.PersistableIdSerializer;

/** Model for a topic in a category. */
@Entity
@JsonIgnoreProperties({"threads", "new"})
public class Topic extends AbstractPersistable<Long> {
    @ManyToOne
    @JoinColumn
    @NotNull
    @JsonSerialize(using = PersistableIdSerializer.class)
    private Category category;
    @OneToMany(mappedBy="topic")
    private List<MessageThread> threads;
    @NotBlank
    @Length(min = 3)
    private String name;
    @Lob
    @NotNull
    private String description;

    public Topic() {

    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<MessageThread> getThreads() {
        if (threads == null)
            threads = new ArrayList<>();
        return threads;
    }

    public void setThreads(List<MessageThread> threads) {
        this.threads = threads;
    }

    public void addThread(MessageThread thread) {
        if (threads == null)
            threads = new ArrayList<>();
        threads.add(thread);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
