package simplforum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import simplforum.models.Topic;

/** Repository for topics.
 */
public interface TopicRepository extends JpaRepository<Topic, Long> {
    //No custom methods
}
