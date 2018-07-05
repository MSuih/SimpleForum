package simplforum.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;

/** Model for a category of topics.
 */
@Entity
@JsonIgnoreProperties({"topics", "new"})
public class Category extends AbstractPersistable<Long> {
    @NotBlank
    @Length(min = 3)
    private String name;
    @Lob
    @NotNull
    private String description;
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Topic> topics;

    public Category() {

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

    public List<Topic> getTopics() {
        if (topics == null)
            topics = new ArrayList<>();
        return topics;
    }

    public void addTopic(Topic topic) {
        if (topics == null)
            topics = new ArrayList<>();
        topics.add(topic);
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
